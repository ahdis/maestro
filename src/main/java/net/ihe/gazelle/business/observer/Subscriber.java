package net.ihe.gazelle.business.observer;

import net.ihe.gazelle.business.engine.Context;
import net.ihe.gazelle.business.step.Step;

public interface Subscriber {
    /**
     * Method called by publisher when notifying
     * @param context context to propagate
     */
    void update(Step step, Context context);
}
