package net.ihe.gazelle.maestro.api.business.testrun.context;

import net.ihe.gazelle.maestro.api.business.testreport.TestRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public List<TestRunReportBuilder> getTestRunReportBuilders() {
        return new ArrayList<>(testRunReportBuilders);
    }

    public void addTestRunReportBuilder(TestRunReportBuilder reportBuilder) {
        testRunReportBuilders.add(reportBuilder);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TestSetSessionStore store)) return false;
        return Objects.equals(setId, store.setId)
                && Objects.equals(testSetRun, store.testSetRun)
                && Objects.equals(testRunReportBuilders, store.testRunReportBuilders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(setId, testSetRun, testRunReportBuilders);
    }
}
