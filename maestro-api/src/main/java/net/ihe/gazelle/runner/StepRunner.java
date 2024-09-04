package net.ihe.gazelle.runner;

import net.ihe.gazelle.context.Context;
import net.ihe.gazelle.observer.Publisher;
import net.ihe.gazelle.step.Step;

public interface StepRunner extends Publisher {
    void run(Step step, Context context);
    String getSupportedStep();
}
