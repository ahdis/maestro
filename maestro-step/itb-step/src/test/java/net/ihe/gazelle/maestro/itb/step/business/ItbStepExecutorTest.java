/*
 * Copyright 2025 Kereval.
 */
package net.ihe.gazelle.maestro.itb.step.business;

import net.ihe.gazelle.itb.gateway.business.ItbReportingService;
import net.ihe.gazelle.itb.gateway.business.reporting.ItbReporting;
import net.ihe.gazelle.itb.gateway.business.reporting.ItbResult;
import net.ihe.gazelle.itb.gateway.business.reporting.ItbSystem;
import net.ihe.gazelle.itb.gateway.business.reporting.ItbTestSession;
import net.ihe.gazelle.itb.gateway.technical.client.ItbClient;
import net.ihe.gazelle.itb.gateway.technical.dao.ItbSessionStoreImpl;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.spi.business.StepRun;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ItbStepExecutorTest {

    private static final String SESSION_ID = "SESSION-123";

    @Test
    void execute_registersFutureInDB_withSessionId_fromHandler() {
        ItbHandler handler = new ItbHandlerMock(SESSION_ID);
        ItbSessionStoreImpl store = new ItbSessionStoreImpl();
        ItbStepExecutor runner = new ItbStepExecutor(handler, store);
        StepRun stepRun = new StepRun(step(), List.of());
        ItbReportingService service =
              new ItbReportingService(new ItbClient("baseUrl", "itbApiKey"), store);

        CompletableFuture<StepRunReport> reportFuture =
              CompletableFuture.supplyAsync(() -> runner.execute(stepRun));

        await()
              .atMost(2, TimeUnit.SECONDS)
              .until(() -> store.get(SESSION_ID) != null);

        service.receiveReporting(getItbReporting());
        StepRunReport report = reportFuture.join();
        assertEquals(StepResult.PASSED, report.getResult());
    }


    private ItbReporting getItbReporting() {
        return new ItbReporting(
                new ItbSystem(64L,"shortName", "fullName"),
                new ItbTestSession("testSuite", "testCase", SESSION_ID),
                ITB_REPORT
        ).setResult(ItbResult.SUCCESS);
    }

    private static final String ITB_REPORT = """
            <ItbReport><n1/><n2/><n3/><n4/><n5/><n6/><n7/><status>SUCCESS</status></ItbReport>
            """;

    private Step step() {
        return new Step()
                .setName("itb")
                .setType(ItbStepDefinition.TYPE)
                .setProperties(List.of(
                        new StringProperty(ItbStepDefinition.ACTOR_ID, "actor"),
                        new StringProperty(ItbStepDefinition.TEST_CASE, "test1"),
                        new StringProperty("note", "hello world"),
                        new ByteArrayProperty("payload", "bytes".getBytes(StandardCharsets.UTF_8))
                ));
    }
}
