package net.ihe.gazelle.business.step.validation;

import net.ihe.gazelle.context.Context;
import net.ihe.gazelle.observer.Publisher;
import net.ihe.gazelle.observer.Subscriber;
import net.ihe.gazelle.business.step.validation.api.ValidationRequestMapper;
import net.ihe.gazelle.step.Step;
import net.ihe.gazelle.runner.StepRunner;
import net.ihe.gazelle.validation.api.domain.report.structure.ValidationReport;
import net.ihe.gazelle.validation.api.domain.request.structure.ValidationRequest;

import java.util.ArrayList;
import java.util.List;

public class ValidationStepRunner implements StepRunner, Subscriber, Publisher {

    private final List<Subscriber> subscribers = new ArrayList<>();
    private ValidationHandler validationHandler;

    public ValidationStepRunner(ValidationHandler validationHandler) {
        this.validationHandler = validationHandler;
    }

    @Override
    public void run(Step step, Context context) {
        ValidationRequest validationRequest =
                ValidationRequestMapper.createValidationRequest((ValidationStep) step, context);
        ValidationReport validationReport = validationHandler.validate(validationRequest);
        context.put(step.getName() + ".REPORT", validationReport);
        notifySubscribers(context);
    }

    @Override
    public final String getSupportedStep() {
        return ValidationStep.class.getCanonicalName();
    }

    @Override
    public void update(Step step, Context context) {
        notifySubscribers(context);
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        if(!subscribers.contains(subscriber)) {
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
