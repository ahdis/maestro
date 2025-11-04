/*
 * Copyright 2025 Kereval.
 */
package net.ihe.gazelle.maestro.itb.step.business;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.itb.gateway.technical.dao.ItbSessionStoreImpl;
import net.ihe.gazelle.maestro.spi.business.StepRun;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ItbStepExecutorTest {

    @Test
    void execute_registersFutureInDB_withSessionId_fromHandler() {
        String sessionId = "SESSION-123";
        ItbHandler handler = new ItbHandler() {
            @Override
            public String handle(ItbStepDefinition step) { return sessionId; }
            @Override
            public boolean isAvailable() { return true; }
        };
        ItbStepExecutor runner = new ItbStepExecutor(handler);

        ItbStepDefinition step = new ItbStepDefinition("itb",
                new Property<>(ItbStepDefinition.TEST_CASE, "TC"),
                new Property<>(ItbStepDefinition.SYSTEM_ID, "SYS"),
                new Property<>(ItbStepDefinition.ACTOR_ID, "ACT"),
                List.of());
        StepRun stepRun = new StepRun("ts", "tr", step);

        CompletableFuture<?> future = runner.execute(stepRun);

        assertSame(future, ItbSessionStoreImpl.get(sessionId));
    }

    @Test
    void getSupportedStep_returnsType() {
        ItbStepExecutor runner = new ItbStepExecutor(new ItbHandler() {
            @Override
            public String handle(ItbStepDefinition step) { return "s"; }
            @Override
            public boolean isAvailable() { return true; }
        });
        assertEquals(ItbStepDefinition.TYPE, runner.getSupportedStep());
    }
}
