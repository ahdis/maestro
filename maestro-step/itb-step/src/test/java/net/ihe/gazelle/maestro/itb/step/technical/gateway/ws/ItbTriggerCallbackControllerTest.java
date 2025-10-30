/*
 * Copyright 2025 Kereval.
 */
package net.ihe.gazelle.maestro.itb.step.technical.gateway.ws;

import jakarta.ws.rs.core.Response;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.itb.step.business.dao.ItbTestExecutionDBInMemory;
import net.ihe.gazelle.maestro.itb.step.technical.gateway.dto.ItbTestSessionDTO;
import net.ihe.gazelle.maestro.itb.step.technical.gateway.dto.ItbTriggerDTO;
import net.ihe.gazelle.maestro.itb.step.technical.gateway.service.catchitbreport.CatchItbReportService;
import net.ihe.gazelle.maestro.itb.step.technical.gateway.service.catchitbreport.CatchItbReportServiceException;
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
class ItbTriggerCallbackControllerTest {

    @Mock
    CatchItbReportService catchItbReportService;

    private static ItbTriggerDTO dtoWithSession(String sessionId) {
        ItbTestSessionDTO session = new ItbTestSessionDTO();
        session.setTestSessionIdentifier(sessionId);
        session.setTestCaseIdentifier("TC");
        ItbTriggerDTO dto = new ItbTriggerDTO();
        dto.setTestSession(session);
        dto.setTestReport("<report><a/><b/><c/><d/><e/><f/><g>SUCCESS</g></report>");
        return dto;
    }

    @Test
    void triggerReportGeneration_returns404_whenSessionNotFound() {
        ItbTriggerCallbackController controller = new ItbTriggerCallbackController(catchItbReportService);
        Response response = controller.triggerReportGeneration(dtoWithSession("missing"));
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void triggerReportGeneration_completesFuture_and_returns200_onSuccess() {
        String sessionId = "SESSION-1";
        CompletableFuture<StepRunReportBuilder> future = new CompletableFuture<>();
        ItbTestExecutionDBInMemory.addStepRunReportFuture(sessionId, future);

        StepRunReportBuilder builder = new StepRunReportBuilder().setStepId("s").setType("ITB");
        when(catchItbReportService.buildItbReport(any())).thenReturn(builder);

        ItbTriggerCallbackController controller = new ItbTriggerCallbackController(catchItbReportService);
        Response response = controller.triggerReportGeneration(dtoWithSession(sessionId));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(future.isDone());
        assertNull(ItbTestExecutionDBInMemory.getStepRunReportFuture(sessionId));
    }

    @Test
    void triggerReportGeneration_returns400_whenServiceThrowsCatchException() {
        String sessionId = "SESSION-2";
        ItbTestExecutionDBInMemory.addStepRunReportFuture(sessionId, new CompletableFuture<>());
        when(catchItbReportService.buildItbReport(any())).thenThrow(new CatchItbReportServiceException("bad"));

        ItbTriggerCallbackController controller = new ItbTriggerCallbackController(catchItbReportService);
        Response response = controller.triggerReportGeneration(dtoWithSession(sessionId));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }
}

