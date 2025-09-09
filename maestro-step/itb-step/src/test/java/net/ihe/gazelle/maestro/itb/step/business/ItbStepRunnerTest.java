/*
 * Copyright 2025 Kereval.
 */
package net.ihe.gazelle.maestro.itb.step.business;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.itb.step.business.dao.ItbTestExecutionDBInMemory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class ItbStepRunnerTest {

    @Test
    void run_registersFutureInDB_withSessionId_fromHandler() {
        String sessionId = "SESSION-123";
        ItbHandler handler = new ItbHandler() {
            @Override
            public String handle(ItbStep step) { return sessionId; }
            @Override
            public boolean isAvailable() { return true; }
        };
        ItbStepRunner runner = new ItbStepRunner(handler);

        ItbStep step = new ItbStep("itb",
                new Property<>(ItbStep.TEST_CASE, "TC"),
                new Property<>(ItbStep.SYSTEM_ID, "SYS"),
                new Property<>(ItbStep.ACTOR_ID, "ACT"),
                List.of());
        StepRun stepRun = new StepRun("ts", "tr", step);

        CompletableFuture<?> future = runner.run(stepRun);

        assertSame(future, ItbTestExecutionDBInMemory.getStepRunReportFuture(sessionId));
    }

    @Test
    void getSupportedStep_returnsType() {
        ItbStepRunner runner = new ItbStepRunner(new ItbHandler() {
            @Override
            public String handle(ItbStep step) { return "s"; }
            @Override
            public boolean isAvailable() { return true; }
        });
        assertEquals(ItbStep.TYPE, runner.getSupportedStep());
    }
}
