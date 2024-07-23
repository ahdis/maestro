package net.ihe.gazelle.business.engine.runner;

import net.ihe.gazelle.business.engine.Context;
import net.ihe.gazelle.business.observer.Publisher;
import net.ihe.gazelle.business.step.Step;

public interface StepRunner extends Publisher {
    void run(Step step, Context context);
    String getSupportedStep();
}
