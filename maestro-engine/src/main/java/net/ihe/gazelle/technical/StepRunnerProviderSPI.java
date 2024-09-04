package net.ihe.gazelle.technical;

import net.ihe.gazelle.business.engine.runner.StepRunnerProvider;
import net.ihe.gazelle.handler.HandlerProvider;
import net.ihe.gazelle.runner.Handler;
import net.ihe.gazelle.step.Step;
import net.ihe.gazelle.runner.StepRunner;
import net.ihe.gazelle.runner.StepRunnerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

public class StepRunnerProviderSPI implements StepRunnerProvider {

    private final Logger LOG = LoggerFactory.getLogger(StepRunnerProviderSPI.class);

    private HandlerProvider handlerProvider;

    public StepRunnerProviderSPI(HandlerProvider handlerProvider) {
        this.handlerProvider = handlerProvider;
    }

    /**
     * Will throw an IllegalArgumentException if it can't provide corresponding stepRunner
     * @param step The step we are want to run
     * @return a stepRunner corresponding to step
     */
    @Override
    public StepRunner create(Step step) {
        ServiceLoader<StepRunnerFactory> serviceLoader = ServiceLoader.load(StepRunnerFactory.class);
        Iterator<StepRunnerFactory> it = serviceLoader.iterator();
        while (it.hasNext()) {
            StepRunnerFactory runnerFactory = it.next();
            if (runnerFactory.getSupportedStep().equals(step.getClass().getCanonicalName())){
                return buildStepRunner(step, runnerFactory);
            }
        }
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Step class " + step.getClass() + " is not supported");
        LOG.error(illegalArgumentException.getMessage(), illegalArgumentException);
        throw illegalArgumentException;
    }

    private StepRunner buildStepRunner(Step step, StepRunnerFactory runnerFactory) {
        Set<Handler> handlers = new HashSet<>();
        runnerFactory.getRequiredServices(step).forEach(
                (serviceName, handlerType) -> {
                    handlers.add(handlerProvider.getHandler(handlerType, serviceName));
                }
        );
        return runnerFactory.createStepRunner(step, handlers);
    }
}
