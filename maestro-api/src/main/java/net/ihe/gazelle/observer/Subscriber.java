package net.ihe.gazelle.observer;

import net.ihe.gazelle.context.Context;
import net.ihe.gazelle.step.Step;

public interface Subscriber {
    /**
     * Method called by publisher when notifying
     * @param context context to propagate
     */
    void update(Step step, Context context);
}
