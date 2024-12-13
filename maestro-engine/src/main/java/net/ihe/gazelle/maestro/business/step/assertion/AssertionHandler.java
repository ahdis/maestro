package net.ihe.gazelle.maestro.business.step.assertion;

import net.ihe.gazelle.maestro.runner.Handler;
import net.ihe.gazelle.testreport.StepRunReport;

/**
 * Assertion Step is not implemented. It exists just for test purpose
 */
public class AssertionHandler implements Handler {

    public AssertionHandler(){/*Empty constructor*/}

    public StepRunReport assertion(AssertionStep assertionStep) {
        return null;
    }

    @Override
    public boolean isAvailable(){
        return true;
    }
}
