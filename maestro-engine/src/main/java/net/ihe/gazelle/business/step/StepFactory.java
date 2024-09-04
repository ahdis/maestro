package net.ihe.gazelle.business.step;

import net.ihe.gazelle.step.Step;

import java.util.Map;

public interface StepFactory {
    Step create(Map<String, String> properites, String name);
    String getSupportedStep();
}
