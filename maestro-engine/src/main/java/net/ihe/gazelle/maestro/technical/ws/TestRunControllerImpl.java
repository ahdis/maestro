/*
 * Copyright 2025 IHE International.
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

package net.ihe.gazelle.maestro.technical.ws;

import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testrun.InvalidTestRunException;
import net.ihe.gazelle.maestro.technical.NotProvidedStep;
import net.ihe.gazelle.maestro.api.business.testrun.UnsupportedPropertyTypeException;
import net.ihe.gazelle.maestro.technical.dto.report.TestReportDTO;
import net.ihe.gazelle.maestro.technical.dto.testrun.TestRunDTO;
import net.ihe.gazelle.maestro.technical.jobs.SynchronousMaestro;
import net.ihe.gazelle.maestro.technical.ws.async.AsynchronousMaestro;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class TestRunControllerImpl implements TestRunController {

    private static final Logger LOG = LoggerFactory.getLogger(TestRunControllerImpl.class);

    private final SynchronousMaestro synchronousMaestro;
    private final AsynchronousMaestro asynchronousMaestro;

    @Inject
    public TestRunControllerImpl(SynchronousMaestro synchronousMaestro, AsynchronousMaestro asynchronousMaestro) {
        this.synchronousMaestro = synchronousMaestro;
        this.asynchronousMaestro = asynchronousMaestro;
    }

    @Override
    public Response run(String callback, List<TestRunDTO> testRuns) {
        if (callback != null) {
            return runAsynchronously(callback, testRuns);
        } else {
            return runSynchronously(testRuns);
        }
    }

    private Response runSynchronously(List<TestRunDTO> testRuns) {
        try {
            TestReport testReport = synchronousMaestro.executeTestSet(
                    testRuns.stream()
                            .map(TestRunDTO::getDomain)
                            .toList()
            );
            return Response.ok(new TestReportDTO(testReport)).build();
        } catch (InvalidTestRunException | IllegalArgumentException e) {
            LOG.debug("TestRun format Exception", e);
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("The request contains a testRun with bad format" + e).build();
        } catch (NotProvidedStep e) {
            LOG.debug(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("Your script contains a non provided test report" + e).build();
        } catch (WebApplicationException e) {
            return Response.status(Response.Status.GATEWAY_TIMEOUT.getStatusCode()).entity(SynchronousMaestro.TIMEOUT_MESSAGE).build();
        }  catch (Exception e) {
            LOG.error("Unknown error", e);
            Thread.currentThread().interrupt();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity("an error occurred during execution: " + e).build();
        }
    }

    private Response runAsynchronously(String callback, List<TestRunDTO> testRuns) {
        try {
            asynchronousMaestro.executeTestSet(
                    callback,
                    testRuns.stream()
                            .map(TestRunDTO::getDomain)
                            .toList()
            );
            return Response.accepted().build();
        } catch (InvalidTestRunException | IllegalArgumentException e) {
            LOG.debug("TestRun format Exception", e);
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("The request contains a testRun with bad format" + e).build();
        } catch (NotProvidedStep e) {
            LOG.debug(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("Your script contains a non provided testreport" + e).build();
        } catch (WebApplicationException e) {
            return Response.status(Response.Status.GATEWAY_TIMEOUT.getStatusCode()).entity(SynchronousMaestro.TIMEOUT_MESSAGE).build();
        } catch (Exception e) {
            LOG.error("Unknown error", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity("an error occurred during execution: " + e).build();
        }
    }

    /**
     * This method is automatically triggered by Jakarta Jackson if the received JSON couldn't be deserialized into TestRunDTO object
     * @param exception The exception Jakarta Jackson thrown while deserializing received JSON
     * @return A Bad Request status code with error message from Jackson
     */
    @ServerExceptionMapper
    public Response handleJsonProcessingException(Exception exception) {
        String message;

        if (exception.getCause() != null && exception.getCause().getCause() != null) {
            if (exception.getCause().getCause() instanceof IllegalArgumentException illegalArgumentException) {
                message = illegalArgumentException.getMessage();
            } else if (exception.getCause().getCause() instanceof UnsupportedPropertyTypeException unsupportedPropertyTypeException) {
                message = unsupportedPropertyTypeException.getMessage();
            } else {
                LOG.error(exception.getMessage(), exception);
                message = "Unable to parse TestRun structure.";
            }
        } else {
            if (exception instanceof InvalidTypeIdException invalidTypeIdException) {
                message = invalidTypeIdException.getOriginalMessage();
            } else {
                LOG.error(exception.getMessage(), exception);
                message = "Unable to parse TestRun structure.";
            }
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(message)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
