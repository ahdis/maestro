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

package net.ihe.gazelle.maestro.quarkus.ws;

import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.ihe.gazelle.lang.TimeoutRuntimeException;
import net.ihe.gazelle.maestro.api.business.InvalidTestRunException;
import net.ihe.gazelle.maestro.api.business.Maestro;
import net.ihe.gazelle.maestro.api.business.SynchronousMaestro;
import net.ihe.gazelle.maestro.api.business.UnsupportedPropertyTypeException;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.api.technical.dto.report.TestReportSummaryDTO;
import net.ihe.gazelle.maestro.api.technical.dto.testrun.TestRunDTO;
import net.ihe.gazelle.maestro.api.technical.dto.testrun.TestSuiteRunDTO;
import net.ihe.gazelle.maestro.quarkus.UnknownStepType;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

@ApplicationScoped
public class TestRunControllerImpl implements TestRunController {

   private static final Logger LOG = LoggerFactory.getLogger(TestRunControllerImpl.class);

   private final SynchronousMaestro synchronousMaestro;
   private final Maestro maestro;

   @Inject
   public TestRunControllerImpl(SynchronousMaestro synchronousMaestro, Maestro maestro) {
      this.synchronousMaestro = synchronousMaestro;
      this.maestro = maestro;
   }

   @Override
   public Response executeTestSuite(TestSuiteRunDTO testSuiteRunDTO, String callback) {
      TestSuiteRun testSuiteRun = testSuiteRunDTO.getBusinessObject();
      if (callback != null) {
         return handleErrors(() -> runAsynchronously(testSuiteRun, callback));
      } else {
         return handleErrors(() -> runSynchronously(testSuiteRun));
      }
   }

    @Override
    public Response executeTest(TestRunDTO testRun, String callback) {
        return null;
    }

    private Response handleErrors(Supplier<Response> serviceCall) {
      try {
         return serviceCall.get();
      } catch (InvalidTestRunException | IllegalArgumentException e) {
         LOG.debug("TestRun format Exception", e);
         return Response.status(Response.Status.BAD_REQUEST.getStatusCode())
               .entity("The request contains a testRun with bad format" + e).build();
      } catch (UnknownStepType e) {
         LOG.debug(e.getMessage());
         return Response.status(Response.Status.BAD_REQUEST.getStatusCode())
               .entity(MessageFormat.format("Unknown step type: {0}", e.getMessage())).build();
      } catch (TimeoutRuntimeException | WebApplicationException e) {
         return Response.status(Response.Status.GATEWAY_TIMEOUT.getStatusCode())
               .entity(SynchronousMaestro.TIMEOUT_MESSAGE).build();
      } catch (Exception e) {
         LOG.error("Unknown error", e);
         Thread.currentThread().interrupt();
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
               .entity("an error occurred during execution: " + e).build();
      }
   }

   private Response runSynchronously(TestSuiteRun testSuiteRun) {
      try {
         TestReportSummary reportSummary = synchronousMaestro.executeTestSuite(testSuiteRun);
         return Response.ok(new TestReportSummaryDTO(reportSummary)).build();
      } catch (TimeoutException e) {
         throw new TimeoutRuntimeException(e);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         throw new RuntimeException(e);
      }
   }

   private Response runAsynchronously(TestSuiteRun testSuiteRun, String callback) {
       maestro.executeTestSuite(testSuiteRun, new ApacheTestReportClient(callback));
      return Response.accepted().build();
   }

   /**
    * This method is automatically triggered by Jakarta Jackson if the received JSON couldn't be deserialized into
    * TestRunDTO object
    *
    * @param exception The exception Jakarta Jackson thrown while deserializing received JSON
    *
    * @return A Bad Request status code with error message from Jackson
    */
   @ServerExceptionMapper
   public Response handleJsonProcessingException(Exception exception) {
      String message;

      if (exception.getCause() != null && exception.getCause().getCause() != null) {
         if (exception.getCause().getCause() instanceof IllegalArgumentException illegalArgumentException) {
            message = illegalArgumentException.getMessage();
         } else if (exception.getCause()
               .getCause() instanceof UnsupportedPropertyTypeException unsupportedPropertyTypeException) {
            message = unsupportedPropertyTypeException.getMessage();
         } else {
            LOG.error(exception.getMessage(), exception);
            message = "Unable to parse TestSuite structure.";
         }
      } else {
         if (exception instanceof InvalidTypeIdException invalidTypeIdException) {
            message = invalidTypeIdException.getOriginalMessage();
         } else {
            LOG.error(exception.getMessage(), exception);
            message = "Unable to parse TestSuite structure.";
         }
      }

      return Response.status(Response.Status.BAD_REQUEST)
            .entity(message)
            .type(MediaType.APPLICATION_JSON)
            .build();
   }
}
