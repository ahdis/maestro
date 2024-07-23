package net.ihe.gazelle.business.engine.runner;

import net.ihe.gazelle.business.step.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.ServiceLoader;

public class StepRunnerProvider {

    private final Logger LOG = LoggerFactory.getLogger(StepRunnerProvider.class);
    /**
     * Will throw an IllegalArgumentException if it can't provide corresponding stepRunner
     * @param step The step we are want to run
     * @return a stepRunner corresponding to step
     */
    public StepRunner create(Step step) {
        ServiceLoader<StepRunnerFactory> serviceLoader = ServiceLoader.load(StepRunnerFactory.class);
        Iterator<StepRunnerFactory> it = serviceLoader.iterator();
        while (it.hasNext()) {
            StepRunnerFactory runnerFactory = it.next();
            if (runnerFactory.getSupportedStep().equals(step.getClass().getCanonicalName())){
                return runnerFactory.createStepRunner(step);
            }
        }
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Step class " + step.getClass() + " is not supported");
        LOG.error(illegalArgumentException.getMessage(), illegalArgumentException);
        throw illegalArgumentException;
    }
}
