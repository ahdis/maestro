/*
 * Copyright 2025 Kereval.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestro.business.step.itb.gateway.ws;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.business.step.itb.gateway.dao.ItbTestExecutionDBInMemory;
import net.ihe.gazelle.maestro.business.step.itb.gateway.dto.ItbTriggerDTO;
import net.ihe.gazelle.maestro.business.step.itb.gateway.service.catchitbreport.CatchItbReportService;
import net.ihe.gazelle.maestro.business.step.itb.gateway.service.catchitbreport.CatchItbReportServiceException;

/**
 * Adapter to push test report to TM
 */
@Path("/itb")
public class ItbTriggerCallbackController {

    private final CatchItbReportService catchItbReportService;

    @Inject
    public ItbTriggerCallbackController(CatchItbReportService catchItbReportService) {
        this.catchItbReportService = catchItbReportService;
    }

    /**
     * Method to trigger report generation
     */
    @POST
    @Path("/report")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response triggerReportGeneration(ItbTriggerDTO itbTriggerDTO) {
        try {
            String sessionID = itbTriggerDTO.getTestSession().getTestSessionIdentifier();
            StepRun stepRun = ItbTestExecutionDBInMemory.getStepRun(sessionID);
            if (stepRun == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Test session with ID " + sessionID + " not found.").build();
            }
            StepRunReportBuilder stepRunReportBuilder = catchItbReportService.buildItbReport(itbTriggerDTO);
            catchItbReportService.pushReport(stepRun, stepRunReportBuilder);
            ItbTestExecutionDBInMemory.deleteSession(sessionID);
            return Response.ok().build();
        } catch (CatchItbReportServiceException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Bad format for test report. Error message : " + e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while manipulating test sessions. Error message : " + e.getMessage()).build();
        }
    }
}
