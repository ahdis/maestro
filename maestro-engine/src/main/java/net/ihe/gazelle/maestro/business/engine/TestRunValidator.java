package net.ihe.gazelle.maestro.business.engine;

import net.ihe.gazelle.maestro.api.business.testrun.DuplicatedStepsException;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.technical.ws.MissingArgumentException;

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
        if (testRun.getTest().getSteps() == null || testRun.getTest().getSteps().isEmpty()) {
            throw new MissingArgumentException("argument steps is null or empty");
        }
        allStepsUnique(testRun.getTest().getSteps());
    }

    private void allStepsUnique(List<Step> steps) {
        List<String> names = new ArrayList<>();
        for (Step step : steps) {
            if (!names.contains(step.getName())) {
                names.add(step.getName());
            }else {
                throw new DuplicatedStepsException("Several steps has the same name : " + step.getName() + " should be unique");
            }
        }
    }
}
