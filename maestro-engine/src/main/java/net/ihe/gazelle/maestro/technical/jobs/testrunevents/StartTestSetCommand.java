package net.ihe.gazelle.maestro.technical.jobs.testrunevents;

import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;

/**
 * A message to launch test set execution
 */
public class StartTestSetCommand implements TestRunEvent{

    private final TestSetRun testSetRun;

    public StartTestSetCommand(TestSetRun testSetRun) {
        this.testSetRun = testSetRun;
    }

    public TestSetRun getTestSetRun() {
        return testSetRun;
    }
}
