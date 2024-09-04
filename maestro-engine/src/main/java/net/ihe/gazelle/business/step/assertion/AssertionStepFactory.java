package net.ihe.gazelle.business.step.assertion;

import net.ihe.gazelle.business.step.StepFactory;
import net.ihe.gazelle.step.Step;

import java.util.Map;

public class AssertionStepFactory implements StepFactory {
    @Override
    public Step create(Map<String, String> properites, String name) {
        return new AssertionStep(name, "","");
    }

    @Override
    public String getSupportedStep() {
        return "";
    }
}
