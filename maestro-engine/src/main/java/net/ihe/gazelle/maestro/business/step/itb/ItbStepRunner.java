package net.ihe.gazelle.maestro.business.step.itb;

import net.ihe.gazelle.maestro.api.business.testrun.runner.AsyncStepRunner;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.business.step.itb.gateway.dao.TestExecutionDAOStatic;

public class ItbStepRunner extends AsyncStepRunner {

    private final ItbHandler handler;

    public ItbStepRunner(ItbHandler itbHandler) {
        this.handler = itbHandler;
    }

    @Override
    public void runAsync(StepRun stepRun) {
        String sessionId = handler.handle((ItbStep) stepRun.step());
        TestExecutionDAOStatic.writeSessions(sessionId, stepRun);
    }

    @Override
    public final String getSupportedStep() {
        return ItbStep.TYPE;
    }

}
