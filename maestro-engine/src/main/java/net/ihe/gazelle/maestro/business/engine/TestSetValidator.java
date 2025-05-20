package net.ihe.gazelle.maestro.business.engine;

import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;

import java.util.List;

public class TestSetValidator {

    public void validate(TestSetRun testSetRun) throws IllegalArgumentException{
        List<TestRun> testRuns = testSetRun.testRuns();
        for (TestRun testRun : testRuns) {
            if(testRun == null) {
                throw new IllegalArgumentException("TestSet " + testSetRun.testSetId() + " contains at least one null testRun");
            }
        }
    }

}
