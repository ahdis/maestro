/*
 * Copyright 2025 Kereval.
 */
package net.ihe.gazelle.maestro.itb.step.technical.gateway.ws;

import jakarta.ws.rs.core.Response;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.itb.gateway.technical.ws.ItbReportCallbackController;
import net.ihe.gazelle.itb.gateway.technical.dao.ItbSessionStoreImpl;
import net.ihe.gazelle.itb.gateway.business.reporting.ItbReporting;
import net.ihe.gazelle.itb.gateway.business.reporting.ItbTestSession;
import net.ihe.gazelle.itb.gateway.business.ItbReportingService;
import net.ihe.gazelle.itb.gateway.business.CatchItbReportServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItbReportCallbackControllerTest {

    @Mock
    ItbReportingService itbReportingService;

    private static ItbReporting dtoWithSession(String sessionId) {
        ItbTestSession session = new ItbTestSession();
        session.setTestSessionIdentifier(sessionId);
        session.setTestCaseIdentifier("TC");
        ItbReporting dto = new ItbReporting();
        dto.setTestSession(session);
        dto.setTestReport("<report><a/><b/><c/><d/><e/><f/><g>SUCCESS</g></report>");
        return dto;
    }

    @Test
    void triggerReportGeneration_returns404_whenSessionNotFound() {
        ItbReportCallbackController controller = new ItbReportCallbackController(itbReportingService);
        Response response = controller.triggerReportGeneration(dtoWithSession("missing"));
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void triggerReportGeneration_completesFuture_and_returns200_onSuccess() {
        String sessionId = "SESSION-1";
        CompletableFuture<StepRunReportBuilder> future = new CompletableFuture<>();
        ItbSessionStoreImpl.add(sessionId, future);

        StepRunReportBuilder builder = new StepRunReportBuilder().setType("ITB");
        when(itbReportingService.buildItbReport(any())).thenReturn(builder);

        ItbReportCallbackController controller = new ItbReportCallbackController(itbReportingService);
        Response response = controller.triggerReportGeneration(dtoWithSession(sessionId));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(future.isDone());
        assertNull(ItbSessionStoreImpl.get(sessionId));
    }

    @Test
    void triggerReportGeneration_returns400_whenServiceThrowsCatchException() {
        String sessionId = "SESSION-2";
        ItbSessionStoreImpl.add(sessionId, new CompletableFuture<>());
        when(itbReportingService.buildItbReport(any())).thenThrow(new CatchItbReportServiceException("bad"));

        ItbReportCallbackController controller = new ItbReportCallbackController(itbReportingService);
        Response response = controller.triggerReportGeneration(dtoWithSession(sessionId));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }
}

