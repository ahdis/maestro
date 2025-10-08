/*
 * Copyright 2025 Kereval.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestro.business;

import net.ihe.gazelle.maestro.api.business.acl.AccessControlListBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.*;
import net.ihe.gazelle.maestro.api.business.testrun.*;
import net.ihe.gazelle.maestro.api.business.testrun.context.Context;
import net.ihe.gazelle.maestro.api.business.testrun.context.TestRunSessionStore;
import net.ihe.gazelle.maestro.api.business.testrun.context.TestSetSessionStore;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MaestroImpl implements Maestro {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(MaestroImpl.class);

    private static final String GAZELLE_ENGINE = "Gazelle engine";

    private final MaestroEventProducer maestroEventProducer;
    private final SessionStoreContext sessionStoreContext;
    private final TestReportRecordService testReportRecordService;
    private final String maestroVersion;

    public MaestroImpl(MaestroEventProducer maestroEventProducer,
                       SessionStoreContext sessionStoreContext,
                       TestReportRecordService testReportRecordService, String maestroVersion) {
        this.maestroEventProducer = maestroEventProducer;
        this.sessionStoreContext = sessionStoreContext;
        this.testReportRecordService = testReportRecordService;
        this.maestroVersion = maestroVersion;
    }

    @Override
    public void run(TestRun testRun) {
        if(testRun == null){
            throw new InvalidTestRunException("TestRun is null");
        }

        try {
            // create context
            Context context = extractInputs(testRun.getInputs());

            String testRunId = UUID.randomUUID().toString();
            String testSetId = testRun.getTestSetId();

            putInStore(testRunId, testRun, context);

            StepCursor stepCursor = sessionStoreContext.getExecutionContext(testRunId).getStepCursor();
            Step step = stepCursor.next();

            try {
                // iterate steps starting with the first; on error, record step error and continue
                runStep(new StepRun(testSetId, testRunId, step));
            } catch (Exception e) {
                logger.error("Error running step {}", step.getName(), e);
                StepRun stepRun = new StepRun(testSetId, testRunId, step);
                StepRunReportBuilder builder = new StepRunReportBuilder()
                        .setStepId(step.getName())
                        .setType(step.getType())
                        .setResult(StepResult.UNDEFINED)
                        .addUnexpectedError(getUnexpectedErrorBuilder(e));
                finishStep(stepRun, builder);
            }
        }
        catch (Exception e) {
            logger.error("Error running test {}", (testRun.getTest() != null ? testRun.getTest().getName() : ""), e);
            TestRunReportBuilder testRunReportBuilder = new TestRunReportBuilder()
                    .setDateTime(Date.from(Instant.now()))
                    .setTest(new TestBuilder()
                            .setId(testRun.getTest() != null ? testRun.getTest().getId() : "")
                            .setName(testRun.getTest() != null ? testRun.getTest().getName() : ""))
                    .addUnexpectedError(getUnexpectedErrorBuilder(e));
            finishTest(testRun, testRunReportBuilder);
        }
    }

    @Override
    public void run(TestSetRun testSetRun) {

        if(testSetRun == null){
            throw new InvalidTestSetException("TestSetRun is null");
        }

        try{

            String testSetId = testSetRun.getTestSetId();
            sessionStoreContext.addTestSetContext(testSetId, testSetRun);
            TestSetSessionStore testSetSessionStore = sessionStoreContext.getTestSetContexts(testSetId);
            if (testSetSessionStore == null) {
                logger.error("Test set {} does not exist", testSetId);
                return;
            }
            TestRunCursor testRunCursor = testSetSessionStore.getTestRunCursor();
            if (testRunCursor == null || !testRunCursor.hasNext()) {
                logger.error("Test set {} does not contain test runs to execute", testSetId);
                return;
            }
            TestRun testRun = testRunCursor.next();
            run(testRun);
        }
        catch (Exception e){
            logger.error("Error running test set {}", testSetRun.getTestSetId(), e);
            TestReportBuilder testReportBuilder = buildGenericTestReport(testSetRun)
                    .setDateTime(Date.from(Instant.now()))
                    .addUnexpectedError(getUnexpectedErrorBuilder(e));
            TestReport testReport = testReportBuilder.build();
            String sessionId = testSetRun.getTestSetId();
            if(sessionId != null) {
                sessionStoreContext.clearExecutionContext(sessionId);
                sessionStoreContext.clearTestSetContext(sessionId);
            }
            recordAndReturnTestReport(testReport, testSetRun);
        }

    }

    @Override
    public void finishTest(TestRun testRun, TestRunReportBuilder testRunReportBuilder) {
        TestSetSessionStore testSetSessionStore = sessionStoreContext.getTestSetContexts(testRun.getTestSetId());
        if (testSetSessionStore == null) {
            // Standalone test run execution (not part of a test set) - emit error directly
            maestroEventProducer.returnTestRunReport(testRun, testRunReportBuilder);
            return;
        }
        testSetSessionStore.addTestRunReportBuilder(testRunReportBuilder);
        TestSetRun testSetRun = testSetSessionStore.getTestSetRun();
        TestRunCursor testRunCursor = testSetSessionStore.getTestRunCursor();
        if(testRunCursor == null) {
            throw new InvalidTestSetException("FATAL: No cursor available for test set " + testRun.getTestSetId());
        }
        if (!testRunCursor.hasNext()) {
            TestReport testReport;
            try {
                testReport = buildGenericTestReport(testSetRun)
                        .setTestRunReports(testSetSessionStore.getTestRunReportBuilders())
                        .build();
            } catch (Exception e) {
                TestReportBuilder testReportBuilder = buildGenericTestReport(testSetRun);
                testReport = testReportBuilder
                        .setTestRunReports(testSetSessionStore.getTestRunReportBuilders())
                        .setTestService(getTestServiceBuilder())
                        .addUnexpectedError(new UnexpectedErrorBuilder().fromThrowable(e))
                        .build();
            }
            String sessionId = testRun.getTestSetId();
            sessionStoreContext.clearExecutionContext(sessionId);
            sessionStoreContext.clearTestSetContext(sessionId);
            recordAndReturnTestReport(testReport, testSetRun);
        } else {
            try {
                run(testRunCursor.next());
                //fixme: remove this error handling from here
            } catch (Exception e) {
                logger.error("Error running test {}", (testRun.getTest() != null ? testRun.getTest().getName() : ""), e);
                TestReportBuilder testReportBuilder = buildGenericTestReport(testSetRun);
                TestReport testReport = testReportBuilder.setDateTime(Date.from(Instant.now()))
                        .setTestRunReports(testSetSessionStore.getTestRunReportBuilders())
                        .addUnexpectedError(getUnexpectedErrorBuilder(e))
                        .build();
                String sessionId = testRun.getTestSetId();
                sessionStoreContext.clearExecutionContext(sessionId);
                sessionStoreContext.clearTestSetContext(sessionId);
                recordAndReturnTestReport(testReport, testSetRun);
            }
        }
    }



    @Override
    public void finishStep(StepRun stepRun, StepRunReportBuilder stepRunReportBuilder) {
        String testRunId = stepRun.testRunId();
        TestRun testRun = getTestRunFromStore(testRunId);
        List<InputBuilder> inputs = convertPropertiesToItems(testRun.getInputs());
        addStepReportToStore(testRunId, stepRunReportBuilder);
        StepCursor stepCursor = sessionStoreContext.getExecutionContext(testRunId).getStepCursor();
        if (!stepCursor.hasNext()) {
            TestRunReportBuilder testRunReportBuilder = new TestRunReportBuilder()
                    .addStepRunReports(getStepReportsFromStore(testRunId))
                    .setDateTime(Date.from(Instant.now()))
                    .setInputs(inputs)
                    .setTest(new TestBuilder()
                            .setId(testRun.getTest().getId())
                            .setName(testRun.getTest().getName()));
            maestroEventProducer.returnTestRunReport(testRun, testRunReportBuilder);
        } else {
            runStep(new StepRun(stepRun.testSetId(), stepRun.testRunId(), stepCursor.next()));
        }
    }

    /**
     * @param e an exception
     * @return UnexpectedErrorBuilder containing e data
     */
    private UnexpectedErrorBuilder getUnexpectedErrorBuilder(Exception e) {
        return new UnexpectedErrorBuilder()
                .setName(e.getClass().getSimpleName())
                .setMessage(e.getMessage());
    }

    private TestServiceBuilder getTestServiceBuilder() {
        return new TestServiceBuilder()
                .setDisclaimer("This Gazelle execution service is provided for technical compliance purposes only and does not constitute legal, medical, or regulatory advice.")
                .setServiceIdentification(
                        new EntityIdentificationBuilder().setName(GAZELLE_ENGINE).setVersion(maestroVersion));
    }


    private TestReportBuilder buildGenericTestReport(TestSetRun testSetRun) {
        TestReportBuilder testReportBuilder = new TestReportBuilder()
                .setTestService(getTestServiceBuilder())
                .setDateTime(Date.from(Instant.now()))
                .setTestSuiteName(testSetRun.getTestSuiteName())
                .setAccessControlList(new AccessControlListBuilder(testSetRun.getAccessControlList()))
                .setSystemsUnderTest(testSetRun.getSystemsUnderTest().stream().map(SystemUnderTestBuilder::new).toList());
        if ((testSetRun.getTestSetId() != null && !testSetRun.getTestSetId().isBlank()) ||
                (testSetRun.getTestSuiteName() != null && !testSetRun.getTestSuiteName().isBlank())) {
            StringBuilder noteBuilder = buildNote(testSetRun);
            testReportBuilder.setNote(noteBuilder.toString());
        }
        return testReportBuilder;
    }

    private StringBuilder buildNote(TestSetRun testSetRun) {
        StringBuilder noteBuilder = new StringBuilder("Tests executed as part of the suite ");
        if(testSetRun.getTestSuiteName() != null && !testSetRun.getTestSuiteName().isBlank()) {
            noteBuilder.append(testSetRun.getTestSuiteName());
        }
        if(!noteBuilder.isEmpty()) {
            noteBuilder.append("#");
        }
        if(testSetRun.getTestSetRunId() != null && !testSetRun.getTestSetRunId().isBlank()) {
            noteBuilder.append(testSetRun.getTestSetRunId());
        }
        return noteBuilder;
    }

    private void recordAndReturnTestReport(TestReport testReport, TestSetRun testSetRun) {
        try{
            String reportId = testReportRecordService.recordTestReportWithReferences(testReport);
            TestReportSummary reportSummary = new TestReportSummary()
                    .setId(reportId)
                    .setDateTime(testReport.getDateTime())
                    .setResult(testReport.getResult());
            if (testReport.getUnexpectedErrors() != null) {
                reportSummary.setUnexpectedErrors(testReport.getUnexpectedErrors());
            }
            maestroEventProducer.returnTestSetReport(testSetRun, reportSummary);
        }
        catch (Exception e) {
            maestroEventProducer.emitErrorTestRunEvent(e);
        }
    }

    /**
     * Create a new TestRunSessionStore and put it in SessionStoreContext
     * @param testRunID id used to identify TestRunSessionStore
     * @param testRun TestRun currently being executed
     * @param context testRun's Context with all the TestRun's inputs
     */
    private void putInStore(String testRunID, TestRun testRun, Context context) {
        StepCursor stepCursor = StepCursor.from(testRun.getTest());
        TestRunSessionStore testRunSessionStore = new TestRunSessionStore(context, testRun, new ArrayList<>(), stepCursor);
        sessionStoreContext.addExecutionContext(testRunID, testRunSessionStore);
    }

    /**
     * @param testRunID id of currently executed testRun
     * @return TestRun from TestRunSessionStore with corresponding id
     */
    private TestRun getTestRunFromStore(String testRunID) {
        return sessionStoreContext.getExecutionContext(testRunID).getTestRun();
    }

    /**
     * @param testRunID id of currently executed testRun
     * @return Context from TestRunSessionStore with corresponding id
     */
    private Context getContextFromStore(String testRunID) {
        return sessionStoreContext.getExecutionContext(testRunID).getContext();
    }

    /**
     *
     * @param testRunId id of currently executed testRun
     * @param reportBuilder a builder to put in TestRunSessionStore with corresponding id
     */
    private void addStepReportToStore(String testRunId, StepRunReportBuilder reportBuilder) {
        sessionStoreContext.getExecutionContext(testRunId).addStepRunReport(reportBuilder);
    }

    /**
     * @param testRunId id of currently executed testRun
     * @return A list of StepRunReportBuilders from TestRunSessionStore with corresponding id
     */
    private List<StepRunReportBuilder> getStepReportsFromStore(String testRunId) {
        return sessionStoreContext.getExecutionContext(testRunId).getStepReports();
    }


    /**
     * Run current testreport and initialize the stepRunner if mandatory
     * @param stepRun contain step to run
     */
    private void runStep(StepRun stepRun) {
        try{
            String testRunId = stepRun.testRunId();
            stepRun.step().setProperties(proxifyProperty(stepRun.step().getProperties(), getContextFromStore(testRunId)));
            maestroEventProducer.executeStep(stepRun);
        } catch (Exception e) {
            logger.error("Error running step {}", stepRun.step().getName(), e);
            StepRunReportBuilder builder = new StepRunReportBuilder()
                    .setStepId(stepRun.step().getName())
                    .setType(stepRun.step().getType())
                    .setResult(StepResult.UNDEFINED)
                    .addUnexpectedError(getUnexpectedErrorBuilder(e));
            finishStep(stepRun, builder);
        }
    }

    /**
     * @param properties a list of property with a name and an associated value
     * @return a context containing inputs marked has content
     */
    private Context extractInputs(List<Property<?>> properties) {

        Context context = new Context();

        for (Property<?> property : properties) {
            context.addProperty(property.getName(), property);
        }

        return context;
    }

    /**
     * method used to convert PropertyRef to ResolvablePropertyRef
     *
     * @param properties a list of Property which can contains PropertyRef
     */
    private List<Property<?>> proxifyProperty(List<Property<?>> properties, Context context) {
        List<Property<?>> res = new ArrayList<>();
        for (Property<?> property : properties) {
            if (property instanceof PropertyRef<?> propertyRef) {
                res.add(new ResolvablePropertyRef<>(propertyRef, context));
            } else {
                res.add(property);
            }
        }
        return res;
    }


    private List<InputBuilder> convertPropertiesToItems(List<Property<?>> properties) {
        List<InputBuilder> items = new ArrayList<>();
        for (Property<?> property : properties) {
            if(property instanceof PropertyRef<?> propertyRef){
                InputBuilder item = new InputBuilder()
                        .setName(property.getName())
                        .setContent(("${"+propertyRef.getReference()+"}").getBytes(StandardCharsets.UTF_8));
                items.add(item);
            }
            else if(property.getValue() instanceof byte[] content) {
                InputBuilder item = new InputBuilder()
                        .setName(property.getName())
                        .setContent(content)
                        .setType(Input.BYTE_ARRAY_TYPE)
                        ;
                items.add(item);
            }
            else if(property.getValue() instanceof String value) {
                InputBuilder item = new InputBuilder()
                        .setName(property.getName())
                        .setContent(value.getBytes(StandardCharsets.UTF_8))
                        .setType(Input.TEXT_TYPE)
                        ;
                items.add(item);
            }
            else if(property.getValue() instanceof Boolean value) {
                InputBuilder item = new InputBuilder()
                        .setName(property.getName())
                        .setContent(new byte[]{(byte) (Boolean.TRUE.equals(value) ? 1 : 0)})
                        .setType(Input.BOOLEAN_TYPE);
                items.add(item);
            }
            else if(property.getValue() instanceof Number value) {
                InputBuilder item = new InputBuilder()
                        .setName(property.getName())
                        .setContent(value.toString().getBytes(StandardCharsets.UTF_8))
                        .setType(Input.NUMBER_TYPE)
                        ;
                items.add(item);
            }
        }
        return items;
    }
}
