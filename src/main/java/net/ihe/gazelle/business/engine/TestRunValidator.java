package net.ihe.gazelle.business.engine;

import net.ihe.gazelle.business.exception.DuplicatedStepsException;
import net.ihe.gazelle.business.exception.MissingArgumentException;
import net.ihe.gazelle.business.step.Step;
import net.ihe.gazelle.business.test.TestRun;

import java.util.ArrayList;
import java.util.List;

public class TestRunValidator {
    public void validate(TestRun testRun) throws MissingArgumentException {
        if (testRun == null) {
            throw new MissingArgumentException("argument testRun is null");
        }
        if (testRun.getTest() == null) {
            throw new MissingArgumentException("argument test is null");
        }
        if (testRun.getTest().getSteps() == null) {
            throw new MissingArgumentException("argument steps is null");
        }
        allStepsUnique(testRun.getTest().getSteps());
    }

    private void allStepsUnique(List<Step> steps) {
        List<String> names = new ArrayList<>();
        for (Step step : steps) {
            if (!names.contains(step.getName())) {
                names.add(step.getName());
            }else {
                throw new DuplicatedStepsException(step.getName() + "should be unique");
            }
        }
    }
}
