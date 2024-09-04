package net.ihe.gazelle.business.step.assertion;

import net.ihe.gazelle.context.Context;
import net.ihe.gazelle.runner.Handler;

public class AssertionHandler implements Handler {

    public AssertionHandler(){/*Empty constructor*/}

    public Context assertion(AssertionStep assertionStep, Context context) {
        if(isAvailable(assertionStep.getAssertionService())){
            context.setReport(assertionStep.getName(), "Assertion Report " + assertionStep.getName());
        }
        return context;
    }

    @Override
    public boolean isAvailable(String assertionService){
        return true;
    }
}
