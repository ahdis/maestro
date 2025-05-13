package net.ihe.gazelle.testreport;

import net.ihe.gazelle.lang.UnexpectedError;

import java.util.*;

import static net.ihe.gazelle.testreport.Result.UNDEFINED;


/**
 * Business rules <br>
 * required fields :
 * uuid -> generation in builder random uuid <br>
 * dateTime <br>
 * result <br>
 * testCounters <br>
 * at least 1 subReport or 1 testRunReport or 1 unexpectedError <br>
 *
 * Result computation to be done here: <br>
 * if 0 unexpectedError and 0 FAILED and 0 PASSED and 0 UNDEFINED -> UNDEFINED <br>
 * if 1 unexpectedError -> UNDEFINED <br>
 * if 1 UNDEFINED -> UNDEFINED <br>
 * if 0 unexpectedError and 0 UNDEFINED and at least 1 FAILED -> FAILED <br>
 * if 0 unexpectedError and 0 UNDEFINED and 0 FAILED and at least 1 PASSED -> PASSED <br>
 *
 * Counters computation: <br>
 * count all testRunReport result + all sub testreport counters <br>
 * count all unexpectedErrors in testRuns + root + subReport error counters, <br>
 *
 * Counters must be valid
 */

public class TestReport {

    public static final String REPORT_VERSION = "1.0";

    private String reportVersion;
    private String uuid;
    private Date dateTime;
    private TestService testService;
    private List<SystemUnderTest> systemsUnderTest;
    private Result result;
    private String testSuiteName;
    private TestCounters testCounters;
    private String note;
    private String urlToTestSuiteResult;
    private List<TestReport> subReports;
    private List<TestRunReport> testRunReports;
    private List<UnexpectedError> unexpectedErrors;

    public TestReport() {
        reportVersion = REPORT_VERSION;
        result = Result.UNDEFINED;
        systemsUnderTest = new ArrayList<>();
        subReports = new ArrayList<>();
        testRunReports = new ArrayList<>();
        unexpectedErrors = new ArrayList<>();
    }

    public TestReport(TestReport copy) {
        this();
        setReportVersion(copy.getReportVersion());
        setUuid(copy.getUuid());
        setDateTime(copy.getDateTime());
        setTestService(copy.getTestService());
        setSystemsUnderTest(copy.getSystemsUnderTest());
        setResult(copy.getResult());
        setTestSuiteName(copy.getTestSuiteName());
        setTestCounters(copy.getTestCounters());
        setNote(copy.getNote());
        setUrlToTestSuiteResult(copy.getUrlToTestSuiteResult());
        setSubReports(copy.getSubReports());
        setTestRunReports(copy.getTestRunReports());
        setUnexpectedErrors(copy.getUnexpectedErrors());
    }

    public String getReportVersion() {
        return reportVersion;
    }

    public TestReport setReportVersion(String reportVersion) {
        this.reportVersion = reportVersion;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public TestReport setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public TestReport setDateTime(Date dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public TestService getTestService() {
        return testService;
    }

    public TestReport setTestService(TestService testService) {
        this.testService = testService;
        return this;
    }

    public List<SystemUnderTest> getSystemsUnderTest() {
        return new ArrayList<>(systemsUnderTest);
    }

    public TestReport setSystemsUnderTest(List<SystemUnderTest> systemsUnderTest) {
        this.systemsUnderTest = new ArrayList<>(systemsUnderTest);
        return this;
    }

    public TestReport addSystemUnderTest(SystemUnderTest systemUnderTest) {
        systemsUnderTest.add(systemUnderTest);
        return this;
    }

    public TestReport addSystemsUnderTest(List<SystemUnderTest> systemsUnderTest) {
        this.systemsUnderTest.addAll(systemsUnderTest);
        return this;
    }

    public Result getResult() {
        return result;
    }

    public TestReport setResult(Result result) {
        this.result = result;
        return this;
    }

    public String getTestSuiteName() {
        return testSuiteName;
    }

    public TestReport setTestSuiteName(String testSuiteName) {
        this.testSuiteName = testSuiteName;
        return this;
    }

    public TestCounters getTestCounters() {
        return testCounters;
    }

    public TestReport setTestCounters(TestCounters testCounters) {
        this.testCounters = testCounters;
        return this;
    }

    public String getNote() {
        return note;
    }

    public TestReport setNote(String note) {
        this.note = note;
        return this;
    }

    public String getUrlToTestSuiteResult() {
        return urlToTestSuiteResult;
    }

    public TestReport setUrlToTestSuiteResult(String urlToTestSuiteResult) {
        this.urlToTestSuiteResult = urlToTestSuiteResult;
        return this;
    }

    public List<TestReport> getSubReports() {
        return new ArrayList<>(subReports);
    }

    public TestReport setSubReports(List<TestReport> subReports) {
        this.subReports = new ArrayList<>(subReports);
        return this;
    }

    public TestReport addSubReport(TestReport subReport) {
        subReports.add(subReport);
        return this;
    }

    public TestReport addSubReports(List<TestReport> subReports) {
        this.subReports.addAll(subReports);
        return this;
    }

    public List<TestRunReport> getTestRunReports() {
        return new ArrayList<>(testRunReports);
    }

    public TestReport setTestRunReports(List<TestRunReport> testRunReports) {
        this.testRunReports = new ArrayList<>(testRunReports);
        return this;
    }

    public TestReport addTestRunReport(TestRunReport testRunReport) {
        testRunReports.add(testRunReport);
        return this;
    }

    public TestReport addTestRunReports(List<TestRunReport> testRunReports) {
        this.testRunReports.addAll(testRunReports);
        return this;
    }

    public List<UnexpectedError> getUnexpectedErrors() {
        return new ArrayList<>(unexpectedErrors);
    }

    public TestReport setUnexpectedErrors(List<UnexpectedError> unexpectedErrors) {
        this.unexpectedErrors = new ArrayList<>(unexpectedErrors);
        return this;
    }

    public TestReport addUnexpectedError(UnexpectedError unexpectedError) {
        unexpectedErrors.add(unexpectedError);
        return this;
    }

    public TestReport addUnexpectedErrors(List<UnexpectedError> unexpectedErrors) {
        this.unexpectedErrors.addAll(unexpectedErrors);
        return this;
    }

    public void computeCounters() {
        this.testCounters = new TestCounters();
        if (this.subReports != null) {
            subReports.stream()
                    .map(TestReport::getTestCounters)
                    .filter(Objects::nonNull)
                    .forEach(testCounters::addNumbersFromSubCounters);
        }
        if (testRunReports != null) {
            testRunReports.forEach(this::incrementCounterForATestRun);
        }
        if (unexpectedErrors != null) {
            unexpectedErrors.forEach(error -> testCounters.incrementUnexpectedErrors());
        }
    }

    public Result computeResult() {
        List<Optional<Result>> results = new ArrayList<>();
        results.add(getErrorsResult());
        results.add(getTestRunsResult());
        results.add(getSubReportResult());
        result = results.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .max(Result::compareTo)
                .orElse(UNDEFINED);
        return result;
    }

    private Optional<Result> getErrorsResult() {
        return unexpectedErrors.isEmpty()
                ? Optional.empty()
                : Optional.of(UNDEFINED);
    }

    public Optional<Result> getTestRunsResult() {
        return testRunReports.isEmpty()
                ? Optional.empty()
                : testRunReports.stream()
                        .map(TestRunReport::getResult)
                        .filter(Objects::nonNull)
                        .max(Result::compareTo);
    }

    public Optional<Result> getSubReportResult() {
        return subReports.isEmpty()
                ? Optional.empty()
                : subReports.stream()
                        .map(TestReport::getResult)
                        .filter(Objects::nonNull)
                        .max(Result::compareTo);
    }

    public void incrementCounterForATestRun(TestRunReport testRunReport) {
        switch (testRunReport.getResult()) {
            case FAILED    -> testCounters.incrementFailed();
            case PASSED    -> testCounters.incrementPassed();
            case UNDEFINED -> {
                testCounters.incrementUndefined();
                if (!testRunReport.getUnexpectedErrors().isEmpty()) {
                    testCounters.incrementUnexpectedErrors(testRunReport.getUnexpectedErrors().size());
                }
            }
        }
    }

    public boolean isUuidValid() {
        return uuid != null && !uuid.isBlank();
    }

    public boolean isDateTimeValid() {
        return dateTime != null && !dateTime.after(new Date());
    }

    public boolean isResultValid() {
        TestReport tmp = new TestReport(this);
        tmp.computeResult();
        return tmp.getResult().equals(result);
    }

    public boolean isTestSuiteNameValid() {
        return testSuiteName == null || !testSuiteName.isBlank();
    }

    public boolean isTestCountersValid() {
        TestReport tmp = new TestReport(this);
        tmp.computeCounters();
        return tmp.getTestCounters().equals(testCounters);
    }

    public boolean isNoteValid() {
        return note == null || !note.isBlank();
    }

    public boolean isUrlToTestSuiteResultValid() {
        return urlToTestSuiteResult == null || !urlToTestSuiteResult.isBlank();
    }

    public boolean hasAtLeastOneResult() {
        return !(subReports.isEmpty() && testRunReports.isEmpty() && unexpectedErrors.isEmpty());
    }

    public boolean isValid() {
        return isUuidValid()
                && isDateTimeValid()
                && isResultValid()
                && isTestSuiteNameValid()
                && isNoteValid()
                && isUrlToTestSuiteResultValid()
                && hasAtLeastOneResult();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestReport that)) return false;
        return Objects.equals(reportVersion, that.reportVersion)
                && Objects.equals(uuid, that.uuid)
                && Objects.equals(dateTime, that.dateTime)
                && Objects.equals(testService, that.testService)
                && Objects.equals(systemsUnderTest, that.systemsUnderTest)
                && result == that.result
                && Objects.equals(testSuiteName, that.testSuiteName)
                && Objects.equals(testCounters, that.testCounters)
                && Objects.equals(note, that.note)
                && Objects.equals(urlToTestSuiteResult, that.urlToTestSuiteResult)
                && Objects.equals(subReports, that.subReports)
                && Objects.equals(testRunReports, that.testRunReports)
                && Objects.equals(unexpectedErrors, that.unexpectedErrors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportVersion, uuid, dateTime, testService, systemsUnderTest, result, testSuiteName, testCounters, note, urlToTestSuiteResult, subReports, testRunReports, unexpectedErrors);
    }
}
