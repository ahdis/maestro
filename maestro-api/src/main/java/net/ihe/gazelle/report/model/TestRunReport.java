package net.ihe.gazelle.report.model;

import java.util.*;

/**
 * Business rules <br>
 * mandatory fields : dateTime, result, test
 *
 */
public class TestRunReport {

    private String runId;
    private Date dateTime;
    private Result result;
    private Test test;
    private List<Item> inputs;
    private List<Item> outputs;
    private List<StepRunReport> stepRunReports;
    private String urlToTestRun;
    private List<UnexpectedError> unexpectedErrors;

    public TestRunReport() {
        result = Result.UNDEFINED;
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        stepRunReports = new ArrayList<>();
        unexpectedErrors = new ArrayList<>();
    }

    public TestRunReport(TestRunReport copy) {
        this();
        this.runId = copy.getRunId();
        this.dateTime = copy.getDateTime();
        this.result = copy.getResult();
        this.test = copy.getTest();
        this.inputs = new ArrayList<>(copy.getInputs());
        this.outputs = new ArrayList<>(copy.getOutputs());
        this.stepRunReports = new ArrayList<>(copy.getStepRunReports());
        this.urlToTestRun = copy.getUrlToTestRun();
        this.unexpectedErrors = new ArrayList<>(copy.getUnexpectedErrors());
    }

    public String getRunId() {
        return runId;
    }

    public TestRunReport setRunId(String runId) {
        this.runId = runId;
        return this;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public TestRunReport setDateTime(Date dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public Test getTest() {
        return test;
    }

    public TestRunReport setTest(Test test) {
        this.test = test;
        return this;
    }

    public List<Item> getInputs() {
        return new ArrayList<>(inputs);
    }

    public TestRunReport setInputs(List<Item> inputs) {
        this.inputs = new ArrayList<>(inputs);
        return this;
    }

    public TestRunReport addInput(Item input) {
        inputs.add(input);
        return this;
    }

    public TestRunReport addInputs(List<Item> inputs) {
        this.inputs.addAll(inputs);
        return this;
    }

    public List<Item> getOutputs() {
        return new ArrayList<>(outputs);
    }

    public TestRunReport setOutputs(List<Item> outputs) {
        this.outputs = new ArrayList<>(outputs);
        return this;
    }

    public TestRunReport addOutput(Item output) {
        outputs.add(output);
        return this;
    }

    public TestRunReport addOutputs(List<Item> outputs) {
        this.outputs.addAll(outputs);
        return this;
    }

    public Result getResult() {
        return result;
    }

    public TestRunReport setResult(Result result) {
        this.result = result;
        return this;
    }

    public List<StepRunReport> getStepRunReports() {
        return new ArrayList<>(stepRunReports);
    }

    public TestRunReport setStepRunReports(List<StepRunReport> stepRunReports) {
        this.stepRunReports = new ArrayList<>(stepRunReports);
        return this;
    }

    public TestRunReport addStepRunReport(StepRunReport stepRunReport) {
        stepRunReports.add(stepRunReport);
        return this;
    }

    public TestRunReport addStepRunReports(List<StepRunReport> stepRunReports) {
        this.stepRunReports.addAll(stepRunReports);
        return this;
    }

    public String getUrlToTestRun() {
        return urlToTestRun;
    }

    public TestRunReport setUrlToTestRun(String urlToTestRun) {
        this.urlToTestRun = urlToTestRun;
        return this;
    }

    public List<UnexpectedError> getUnexpectedErrors() {
        return new ArrayList<>(unexpectedErrors);
    }

    public TestRunReport setUnexpectedErrors(List<UnexpectedError> unexpectedErrors) {
        this.unexpectedErrors = new ArrayList<>(unexpectedErrors);
        return this;
    }

    public TestRunReport addUnexpectedError(UnexpectedError unexpectedError) {
        unexpectedErrors.add(unexpectedError);
        return this;
    }

    public TestRunReport addUnexpectedErrors(List<UnexpectedError> unexpectedErrors) {
        this.unexpectedErrors.addAll(unexpectedErrors);
        return this;
    }

    public Result computeResult() {
        if (!unexpectedErrors.isEmpty()) {
            result = Result.UNDEFINED;
        } else {
            Optional<StepResult> globalStepResult = stepRunReports.stream()
                    .map(StepRunReport::getResult)
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
        TestRunReport tmp = new TestRunReport(this);
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
        if (!(o instanceof TestRunReport testRunReport)) return false;
        return Objects.equals(runId, testRunReport.runId)
                && Objects.equals(dateTime, testRunReport.dateTime)
                && result == testRunReport.result
                && Objects.equals(test, testRunReport.test)
                && Objects.equals(inputs, testRunReport.inputs)
                && Objects.equals(outputs, testRunReport.outputs)
                && Objects.equals(stepRunReports, testRunReport.stepRunReports)
                && Objects.equals(urlToTestRun, testRunReport.urlToTestRun)
                && Objects.equals(unexpectedErrors, testRunReport.unexpectedErrors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId, dateTime, result, test, inputs, outputs, stepRunReports, urlToTestRun, unexpectedErrors);
    }
}
