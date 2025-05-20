package net.ihe.gazelle.maestro.technical.jobs.testrunevents;


import net.ihe.gazelle.maestro.api.business.testrun.TestRun;

/**
 * A message to start TestRun Execution
 */
public class StartTestRunCommand implements TestRunEvent {

    private final TestRun testRun;

    public StartTestRunCommand(TestRun testRun) {
        this.testRun = testRun;
    }

    public TestRun getTestRun() {
        return testRun;
    }

}
