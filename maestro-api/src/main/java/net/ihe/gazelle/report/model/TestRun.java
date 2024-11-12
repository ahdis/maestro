package net.ihe.gazelle.report.model;

import java.util.*;

/**
 * Business rules
 * mandatory fields : dateTime, result, test
 *
 */
public class TestRun {

    private String runId;
    private Date dateTime;
    private Result result;
    private Test test;
    private List<Item> inputs;
    private List<Item> outputs;
    private List<StepRun> stepRuns;
    private String urlToTestRun;
    private List<UnexpectedError> unexpectedErrors;

    public TestRun() {
        result = Result.UNDEFINED;
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        stepRuns = new ArrayList<>();
        unexpectedErrors = new ArrayList<>();
    }

    public TestRun(TestRun copy) {
        this();
        this.runId = copy.getRunId();
        this.dateTime = copy.getDateTime();
        this.result = copy.getResult();
        this.test = copy.getTest();
        this.inputs = new ArrayList<>(copy.getInputs());
        this.outputs = new ArrayList<>(copy.getOutputs());
        this.stepRuns = new ArrayList<>(copy.getStepRuns());
        this.urlToTestRun = copy.getUrlToTestRun();
        this.unexpectedErrors = new ArrayList<>(copy.getUnexpectedErrors());
    }

    public String getRunId() {
        return runId;
    }

    public TestRun setRunId(String runId) {
        this.runId = runId;
        return this;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public TestRun setDateTime(Date dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public Test getTest() {
        return test;
    }

    public TestRun setTest(Test test) {
        this.test = test;
        return this;
    }

    public List<Item> getInputs() {
        return new ArrayList<>(inputs);
    }

    public TestRun setInputs(List<Item> inputs) {
        this.inputs = new ArrayList<>(inputs);
        return this;
    }

    public TestRun addInput(Item input) {
        inputs.add(input);
        return this;
    }

    public TestRun addInputs(List<Item> inputs) {
        this.inputs.addAll(inputs);
        return this;
    }

    public List<Item> getOutputs() {
        return new ArrayList<>(outputs);
    }

    public TestRun setOutputs(List<Item> outputs) {
        this.outputs = new ArrayList<>(outputs);
        return this;
    }

    public TestRun addOutput(Item output) {
        outputs.add(output);
        return this;
    }

    public TestRun addOutputs(List<Item> outputs) {
        this.outputs.addAll(outputs);
        return this;
    }

    public Result getResult() {
        return result;
    }

    public TestRun setResult(Result result) {
        this.result = result;
        return this;
    }

    public List<StepRun> getStepRuns() {
        return new ArrayList<>(stepRuns);
    }

    public TestRun setStepRuns(List<StepRun> stepRuns) {
        this.stepRuns = new ArrayList<>(stepRuns);
        return this;
    }

    public TestRun addStepRun(StepRun stepRun) {
        stepRuns.add(stepRun);
        return this;
    }

    public TestRun addStepRuns(List<StepRun> stepRuns) {
        this.stepRuns.addAll(stepRuns);
        return this;
    }

    public String getUrlToTestRun() {
        return urlToTestRun;
    }

    public TestRun setUrlToTestRun(String urlToTestRun) {
        this.urlToTestRun = urlToTestRun;
        return this;
    }

    public List<UnexpectedError> getUnexpectedErrors() {
        return new ArrayList<>(unexpectedErrors);
    }

    public TestRun setUnexpectedErrors(List<UnexpectedError> unexpectedErrors) {
        this.unexpectedErrors = new ArrayList<>(unexpectedErrors);
        return this;
    }

    public TestRun addUnexpectedError(UnexpectedError unexpectedError) {
        unexpectedErrors.add(unexpectedError);
        return this;
    }

    public TestRun addUnexpectedErrors(List<UnexpectedError> unexpectedErrors) {
        this.unexpectedErrors.addAll(unexpectedErrors);
        return this;
    }

    public Result computeResult() {
        if (!unexpectedErrors.isEmpty()) {
            result = Result.UNDEFINED;
        } else {
            Optional<StepResult> globalStepResult = stepRuns.stream()
                    .map(StepRun::getResult)
                    .filter(Objects::nonNull)
                    .max(StepResult::compareTo);

            if (globalStepResult.isPresent()) {
                switch (globalStepResult.get()) {
                    case PASSED -> result = Result.PASSED;
                    case FAILED -> result = Result.FAILED;
                    case DONE, UNDEFINED -> result = Result.UNDEFINED;
                }
            }
        }
        return result;
    }

    public boolean isDateTimeValid() {
        return dateTime != null && !dateTime.after(new Date());
    }

    public boolean isResultValid() {
        TestRun tmp = new TestRun(this);
        tmp.computeResult();
        return tmp.getResult().equals(result);
    }

    public boolean isTestValid() {
        return test != null;
    }

    public boolean isUrlToTestRunValid() {
        return urlToTestRun == null || !urlToTestRun.isBlank();
    }

    public boolean isValid() {
        return isDateTimeValid()
                && isResultValid()
                && isTestValid()
                && isUrlToTestRunValid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestRun testRun)) return false;
        return Objects.equals(runId, testRun.runId)
                && Objects.equals(dateTime, testRun.dateTime)
                && result == testRun.result
                && Objects.equals(test, testRun.test)
                && Objects.equals(inputs, testRun.inputs)
                && Objects.equals(outputs, testRun.outputs)
                && Objects.equals(stepRuns, testRun.stepRuns)
                && Objects.equals(urlToTestRun, testRun.urlToTestRun)
                && Objects.equals(unexpectedErrors, testRun.unexpectedErrors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId, dateTime, result, test, inputs, outputs, stepRuns, urlToTestRun, unexpectedErrors);
    }
}
