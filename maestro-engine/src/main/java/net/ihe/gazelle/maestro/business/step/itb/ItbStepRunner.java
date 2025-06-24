package net.ihe.gazelle.maestro.business.step.itb;

import net.ihe.gazelle.maestro.api.business.testrun.runner.AsyncStepRunner;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.business.step.itb.gateway.dao.ItbTestExecutionDBInMemory;
import net.ihe.gazelle.maestro.technical.step.itb.ItbHandlerImpl;

public class ItbStepRunner extends AsyncStepRunner {

    private final ItbHandlerImpl handler;

    public ItbStepRunner(ItbHandlerImpl itbHandler) {
        this.handler = itbHandler;
    }

    @Override
    public void runAsync(StepRun stepRun) {
        String sessionId = handler.handle((ItbStep) stepRun.step());
        ItbTestExecutionDBInMemory.writeSessions(sessionId, stepRun);
    }

    @Override
    public final String getSupportedStep() {
        return ItbStep.TYPE;
    }

}
