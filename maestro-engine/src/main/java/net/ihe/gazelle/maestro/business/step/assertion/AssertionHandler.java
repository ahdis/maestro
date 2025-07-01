package net.ihe.gazelle.maestro.business.step.assertion;

import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;

/**
 * Assertion Step is not implemented. It exists just for test purpose
 */
public class AssertionHandler implements Handler {

    public AssertionHandler() {
        /*Empty constructor*/
    }

    public boolean assertion(AssertionStep assertionStep) {
        return assertionStep != null;
    }

    @Override
    public boolean isAvailable(){
        return true;
    }
}
