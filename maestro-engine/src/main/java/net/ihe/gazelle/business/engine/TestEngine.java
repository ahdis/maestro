package net.ihe.gazelle.business.engine;

import net.ihe.gazelle.business.exception.InvalidTestRunException;
import net.ihe.gazelle.business.exception.MissingArgumentException;
import net.ihe.gazelle.observer.Subscriber;
import net.ihe.gazelle.business.test.Test;
import net.ihe.gazelle.business.test.TestRun;
import net.ihe.gazelle.context.Context;
import net.ihe.gazelle.dao.Input;
import net.ihe.gazelle.business.engine.runner.StepRunnerProvider;
import net.ihe.gazelle.step.Step;
import net.ihe.gazelle.step_runner.StepRunner;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestEngine implements Subscriber {

    StepRunnerProvider stepRunnerFactory = new StepRunnerProvider();
    Map<String, Object> reports = new ConcurrentHashMap<>();
    StepRunner stepRunner = null;
    Test test = null;

    public Map<String, Object> getReports() {
        return reports;
    }

    /**
     * Main method called by controller
     * Create a new context and run first step of testRun if it's valid
     * @param testRun the testRun containing automated steps to execute
     * @throws IllegalArgumentException if testRun isn't valid
     */
    public void run(TestRun testRun) throws MissingArgumentException {

        // Add validation of testRun structure
        try{
            new TestRunValidator().validate(testRun);
        } catch (Exception e){
            throw new InvalidTestRunException(e);
        }

        test = testRun.getTest();
        List<Input> inputs = testRun.getInputs();
        // create context
        Context context = extractInputs(inputs);
        context.setTestRun(testRun);
        // iterate of steps
        context.setStep(test.getSteps().getFirst());
        runStep(context, test.getSteps().getFirst());
    }

    @Override
    public void update(Step step, Context context) {
        List<Step> steps = test.getSteps();
        if(steps.getLast().equals(step)){
            extractReports(context);
        }else {
            for (int i = 0; i < steps.size(); i++) {
                if (steps.get(i).equals(step)) {
                    context.setStep(steps.get(i + 1));
                    runStep(context, steps.get(i + 1));
                    i=steps.size();
                }
            }
        }
    }

    /**
     * @param context containing reports we are extracting
     */
    private void extractReports(Context context){
        reports.clear();
        for (Step step : test.getSteps()) {
            reports.put(step.getName(), step.getReport(context));
        }
    }

    /**
     * Run current step and initialize the stepRunner if mandatory
     * @param context context to propagate
     */
    private void runStep(Context context, Step step){
        if (stepRunner == null || !stepRunner.getSupportedStep().equals(step.getClass().getCanonicalName())) {
            stepRunner = stepRunnerFactory.create(step);
            stepRunner.subscribe(this);
        }
        stepRunner.run(step, context);
    }

    /**
     * @param inputs a list of name and associated value
     * @return a context containing inputs marked has content
     */
    private Context extractInputs(List<Input> inputs) {
        Context context = new Context(new ConcurrentHashMap<>());

        for (Input input : inputs) {
            context.put(input.getName(), input.getValue());
        }

        return context;
    }
}
