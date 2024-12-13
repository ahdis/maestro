package net.ihe.gazelle.maestro.context;

import net.ihe.gazelle.maestro.TestSetRun;
import net.ihe.gazelle.testreport.TestRunReportBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * TestSetSessionStore is a structure used to store report builder from executed TestRun,
 * testSet's id and reference to this testSet.
 */
public class TestSetSessionStore {

    private final String setId;
    private final TestSetRun testSetRun;
    private final List<TestRunReportBuilder> testRunReportBuilders;

    public TestSetSessionStore(String setId, TestSetRun testRuns) {
        this.setId = setId;
        this.testSetRun = testRuns;
        this.testRunReportBuilders = new ArrayList<>();
    }

    public String getSetId() {
        return setId;
    }

    public TestSetRun getTestSetRun() {
        return testSetRun;
    }

    public void addTestRunReportBuilder(TestRunReportBuilder reportBuilder) {
        testRunReportBuilders.add(reportBuilder);
    }

    public List<TestRunReportBuilder> getTestRunReportBuilders() {
        return testRunReportBuilders;
    }
}
