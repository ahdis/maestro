package net.ihe.gazelle.business.step.assertion;

import net.ihe.gazelle.business.engine.Context;
import net.ihe.gazelle.business.engine.runner.StepRunner;
import net.ihe.gazelle.business.observer.Subscriber;
import net.ihe.gazelle.business.step.Step;

import java.util.ArrayList;
import java.util.List;

public class AssertionStepRunner implements StepRunner {

    List<Subscriber> subscribers = new ArrayList<>();
    AssertionHandler handler;

    protected AssertionStepRunner(AssertionHandler assertionHandler) {
        handler = assertionHandler;
    }

    @Override
    public void run(Step step, Context context) {
        notifySubscribers(handler.assertion((AssertionStep) step, context));
    }

    @Override
    public String getSupportedStep() {
        return AssertionStep.class.getCanonicalName();
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        if (!subscribers.contains(subscriber)) {
            subscribers.add(subscriber);
        }
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    private void notifySubscribers(Context context) {
        for (Subscriber subscriber : subscribers) {
            subscriber.update(context.getStep(), context);
        }
    }
}
