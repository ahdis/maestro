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

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MaestroImpl implements Maestro {

    private final MaestroEventProducer maestroEventProducer;
    private final SessionStoreContext sessionStoreContext;
    private final TestReportRecordService testReportRecordService;
    private static final String GAZELLE_ENGINE = "Gazelle engine";
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
        boolean isValid = true;

        try {
            new TestRunValidator().validate(testRun);
        } catch (Exception e) {
            if (testRun == null) {
                maestroEventProducer.emitErrorTestRunEvent(e);
            } else {
                sendUndefinedTestReport(testRun, e);
            }
            isValid = false;
        }
        if (isValid) {
            net.ihe.gazelle.maestro.api.business.testrun.Test test = testRun.getTest();
            List<Property<?>> properties = testRun.getInputs();
            // create context
            Context context = extractInputs(properties);

            String testRunId = UUID.randomUUID().toString();
            String testSetId = testRun.getTestSetId();

            putInStore(testRunId, testRun, context);

            if (test != null) {
                Step step = test.getSteps().getFirst();

                // iterate steps starting with the first; on error, record step error and continue
                try {
                    runStep(new StepRun(testSetId, testRunId, step));
                } catch (Exception e) {
                    StepRun stepRun = new StepRun(testSetId, testRunId, step);
                    StepRunReportBuilder builder = new StepRunReportBuilder()
                            .setStepId(step.getName())
                            .setType(step.getType())
                            .setResult(StepResult.UNDEFINED)
                            .addUnexpectedError(getUnexpectedErrorBuilder(e));
                    finishStep(stepRun, builder);
                }
            }
        }
    }

    @Override
    public void run(TestSetRun testSetRun) {
        String testSetId = testSetRun.getTestSetId();
        if (testSetId == null) {
            maestroEventProducer.emitErrorTestRunEvent(new InvalidTestSetException("testSetId is null"));
        }
        boolean isValid = true;
        try {
            TestSetValidator testSetValidator = new TestSetValidator();
            testSetValidator.validate(testSetRun);
        } catch (IllegalArgumentException e) {
            isValid = false;
            UnexpectedErrorBuilder unexpectedError = new UnexpectedErrorBuilder().fromThrowable(e);
            TestReportBuilder testReportBuilder = buildGenericTestReport(testSetRun);
            TestReport testReport = testReportBuilder
                    .addUnexpectedError(unexpectedError)
                    .build();
            recordAndReturnTestReport(testReport, testSetRun);
        }
        if (isValid) {
            sessionStoreContext.addTestSetContext(testSetId, testSetRun);
            TestRun first = testSetRun.getTestRuns().getFirst();
            run(first.setTestSetId(testSetId));
        }
    }

    @Override
    public void finishTest(TestRun testRun, TestRunReportBuilder testRunReportBuilder) {
        TestSetSessionStore testSetSessionStore = sessionStoreContext.getTestSetContexts(testRun.getTestSetId());
        testSetSessionStore.addTestRunReportBuilder(testRunReportBuilder);
        TestSetRun testSetRun = testSetSessionStore.getTestSetRun();
        if (testSetRun.getTestRuns().getLast().equals(testRun)) {
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
            runNextTestRun(testRun, testSetRun);
        }
    }



    @Override
    public void finishStep(StepRun stepRun, StepRunReportBuilder stepRunReportBuilder) {
        String testRunId = stepRun.testRunId();
        TestRun testRun = getTestRunFromStore(testRunId);
        List<InputBuilder> inputs = convertPropertiesToItems(testRun.getInputs());
        addStepReportToStore(testRunId, stepRunReportBuilder);
        if (stepRun.step().isNameEquals(testRun.getTest().getSteps().getLast())) {
            TestRunReportBuilder testRunReportBuilder = new TestRunReportBuilder()
                    .addStepRunReports(getStepReportsFromStore(testRunId))
                    .setDateTime(Date.from(Instant.now()))
                    .setInputs(inputs)
                    .setTest(new TestBuilder()
                            .setId(testRun.getTest().getId())
                            .setName(testRun.getTest().getName()));
            maestroEventProducer.returnTestRunReport(testRun, testRunReportBuilder);
        } else {
            runNextStep(stepRun);
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

    /**
     * Method call at the end of a TestRun execution to run next TestRun.
     * <p>
     *     - If testRun is not in testSetRun or is last element, send a report with UnexpectedError InvalidTestSetException
     * </p>
     * @param testRun previously executed TestRun
     * @param testSetRun testSetRun containing testRun
     */
    private void runNextTestRun(TestRun testRun, TestSetRun testSetRun) {
        TestSetSessionStore testSetSessionStore = sessionStoreContext.getTestSetContexts(testRun.getTestSetId());
        List<TestRun> testRuns = testSetSessionStore.getTestSetRun().getTestRuns();
        boolean found = false;
        for (int i = 0; (i < testRuns.size() - 1 && !found); i++) {
            if (testRuns.get(i).equals(testRun)) {
                run(testRuns.get(i + 1).setTestSetId(testRun.getTestSetId()));
                found = true;
            }
        }
        if (!found) {
            Exception e = new InvalidTestSetException("TestRun " + testRun.getTest().getName() +
                    " not found in testSet " + testRun.getTestSetId() + " or is last element of set");
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
        TestRunSessionStore testRunSessionStore = new TestRunSessionStore(context, testRun, new ArrayList<>());
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
     * Run the following step in test steps list
     *
     * @param stepRun previous step
     * @throws InvalidTestRunException if step isn't contains in test step list or if it's the last element of the list
     */
    private void runNextStep(StepRun stepRun) {
        net.ihe.gazelle.maestro.api.business.testrun.Test test = getTestRunFromStore(stepRun.testRunId()).getTest();
        Step previousStep = stepRun.step();
        List<Step> steps = test.getSteps();
        boolean found = false;
        for (int i = 0; (i < steps.size()) && !found; i++) {
            if (steps.get(i).isNameEquals(previousStep)) {
                Step stepToRun = steps.get(i + 1);
                runStep(new StepRun(stepRun.testSetId(), stepRun.testRunId(), stepToRun));
                found = true;
            }
        }
        if (!found) {
            throw new InvalidTestRunException("Step " + previousStep.getName() + " not found in test " + test.getName());
        }
    }


    /**
     * Run current testreport and initialize the stepRunner if mandatory
     * @param stepRun contain step to run
     */
    private void runStep(StepRun stepRun) {
        String testRunId = stepRun.testRunId();
        stepRun.step().setProperties(proxifyProperty(stepRun.step().getProperties(), getContextFromStore(testRunId)));
        maestroEventProducer.executeStep(stepRun);
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

    /**
     * Method called when an exception is raised during iteration and execution must be stopped.
     * @param testRun The TestRun where problem occurred
     * @param e raised exception
     */
    private void sendUndefinedTestReport(TestRun testRun, Exception e) {
        String id = "";
        String name = "";
        net.ihe.gazelle.maestro.api.business.testrun.Test testRunTest = testRun.getTest();
        if (testRunTest != null) {
            id = testRunTest.getId();
            name = testRunTest.getName();
        }
        TestSetSessionStore testSetSessionStore = sessionStoreContext.getTestSetContexts(testRun.getTestSetId());
        TestSetRun testSetRun = testSetSessionStore.getTestSetRun();
        TestReportBuilder testReportBuilder = buildGenericTestReport(testSetRun);
        TestReport testReport = testReportBuilder
                .addTestRunReport(new TestRunReportBuilder()
                        .addUnexpectedError(new UnexpectedErrorBuilder().fromThrowable(e))
                        .setTest(new TestBuilder()
                                .setId(id)
                                .setName(name)
                        )
                )
                .build();
        recordAndReturnTestReport(testReport, testSetRun);
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
