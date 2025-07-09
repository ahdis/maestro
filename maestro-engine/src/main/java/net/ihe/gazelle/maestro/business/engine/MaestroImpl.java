package net.ihe.gazelle.maestro.business.engine;

import net.ihe.gazelle.maestro.api.business.testreport.*;
import net.ihe.gazelle.maestro.api.business.testrun.*;
import net.ihe.gazelle.maestro.api.business.testrun.context.Context;
import net.ihe.gazelle.maestro.api.business.testrun.context.TestRunSessionStore;
import net.ihe.gazelle.maestro.api.business.testrun.context.TestSetSessionStore;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MaestroImpl implements Maestro {

    MaestroProducer maestroProducer;
    SessionStoreContext sessionStoreContext;

    public MaestroImpl(MaestroProducer maestroProducer, SessionStoreContext sessionStoreContext) {
        this.maestroProducer = maestroProducer;
        this.sessionStoreContext = sessionStoreContext;
    }

    @Override
    public void run(TestRun testRun) {
        boolean isValid = true;

        try {
            new TestRunValidator().validate(testRun);
        } catch (Exception e) {
            if (testRun == null) {
                maestroProducer.emitErrorTestRunEvent(e);
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

                // iterate of steps
                runStep(new StepRun(testSetId, testRunId, step));
            }
        }
    }

    @Override
    public void run(TestSetRun testSetRun) {
        String testSetId = testSetRun.testSetId();
        if (testSetId == null) {
            maestroProducer.emitErrorTestRunEvent(new InvalidTestSetException("testSetId is null"));
        }
        boolean isValid = true;
        try {
            TestSetValidator testSetValidator = new TestSetValidator();
            testSetValidator.validate(testSetRun);
        } catch (IllegalArgumentException e) {
            isValid = false;
            TestReport testReport = new TestReportBuilder()
                    .addUnexpectedError(new UnexpectedErrorBuilder().fromTrowable(e))
                    .build();
            maestroProducer.returnTestSetReport(testSetRun, testReport);
        }
        if (isValid) {
            sessionStoreContext.addTestSetContext(testSetId, testSetRun);
            TestRun first = testSetRun.testRuns().getFirst();
            run(first.setTestSetId(testSetId));
        }
    }

    @Override
    public void finishTest(TestRun testRun, TestRunReportBuilder testRunReportBuilder) {
        TestSetSessionStore testSetSessionStore = sessionStoreContext.getTestSetContexts(testRun.getTestSetId());
        testSetSessionStore.addTestRunReportBuilder(testRunReportBuilder);
        TestSetRun testSetRun = testSetSessionStore.getTestSetRun();
        if (testSetRun.testRuns().getLast().equals(testRun)) {
            TestReport testReport;
            try {
                testReport = new TestReportBuilder()
                        .setDateTime(Date.from(Instant.now()))
                        .setTestRunReports(testSetSessionStore.getTestRunReportBuilders())
                        .build();
            } catch (Exception e) {
                testReport = new TestReportBuilder()
                        .addUnexpectedError(new UnexpectedErrorBuilder().fromTrowable(e))
                        .build();
            }
            String sessionId = testRun.getTestSetId();
            sessionStoreContext.clearExecutionContext(sessionId);
            sessionStoreContext.clearTestSetContext(sessionId);
            maestroProducer.returnTestSetReport(testSetRun, testReport);
        } else {
            runNextTestRun(testRun, testSetRun);
        }
    }

    @Override
    public void finishStep(StepRun stepRun, StepRunReportBuilder stepRunReportBuilder) {
        String testRunId = stepRun.testRunId();
        TestRun testRun = getTestRunFromStore(testRunId);
        addStepReportToStore(testRunId, stepRunReportBuilder);
        if (stepRun.step().isNameEquals(testRun.getTest().getSteps().getLast())) {
            TestRunReportBuilder testRunReportBuilder = new TestRunReportBuilder()
                    .addStepRunReports(getStepReportsFromStore(testRunId))
                    .setDateTime(Date.from(Instant.now()))
                    .setTest(new TestBuilder()
                            .setId(testRun.getTest().getId())
                            .setName(testRun.getTest().getName()));
            maestroProducer.returnTestRunReport(testRun, testRunReportBuilder);
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
        List<TestRun> testRuns = testSetSessionStore.getTestSetRun().testRuns();
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
            TestReport testReport = new TestReportBuilder()
                    .setDateTime(Date.from(Instant.now()))
                    .setTestRunReports(testSetSessionStore.getTestRunReportBuilders())
                    .addUnexpectedError(getUnexpectedErrorBuilder(e))
                    .build();
            String sessionId = testRun.getTestSetId();
            sessionStoreContext.clearExecutionContext(sessionId);
            sessionStoreContext.clearTestSetContext(sessionId);
            maestroProducer.returnTestSetReport(testSetRun, testReport);
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
        maestroProducer.executeStep(stepRun);
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

        TestReport testReport = new TestReportBuilder()
                .addTestRunReport(new TestRunReportBuilder()
                        .addUnexpectedError(new UnexpectedErrorBuilder().fromTrowable(e))
                        .setTest(new TestBuilder()
                                .setId(id)
                                .setName(name)
                        )
                )
                .build();

        TestSetSessionStore testSetSessionStore = sessionStoreContext.getTestSetContexts(testRun.getTestSetId());
        TestSetRun testSetRun = testSetSessionStore.getTestSetRun();

        maestroProducer.returnTestSetReport(testSetRun, testReport);
    }
}
