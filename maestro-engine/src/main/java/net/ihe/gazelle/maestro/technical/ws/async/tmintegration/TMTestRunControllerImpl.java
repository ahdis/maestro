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

package net.ihe.gazelle.maestro.technical.ws.async.tmintegration;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import net.ihe.gazelle.maestro.api.business.testrun.InvalidTestRunException;
import net.ihe.gazelle.maestro.technical.NotProvidedStep;
import net.ihe.gazelle.maestro.technical.jobs.SynchronousMaestro;
import net.ihe.gazelle.maestro.technical.ws.MissingArgumentException;
import net.ihe.gazelle.maestro.technical.ws.ParsingErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class TMTestRunControllerImpl implements TMTestRunController {

    private static final Logger LOG = LoggerFactory.getLogger(TMTestRunControllerImpl.class);

    private final TMTestRunMaestro tmTestRunMaestro;

    @Inject
    public TMTestRunControllerImpl(TMTestRunMaestro tmTestRunMaestro) {
        this.tmTestRunMaestro = tmTestRunMaestro;
    }

    @Override
    public Response run(JsonNode tmTestRunScript) {
        try {
            tmTestRunMaestro.startTestSet(tmTestRunScript);
            return Response.ok().build();
        } catch (WebApplicationException e) {
            return Response.status(Response.Status.GATEWAY_TIMEOUT.getStatusCode()).entity(SynchronousMaestro.TIMEOUT_MESSAGE).build();
        } catch (ParsingErrorException | MissingArgumentException e) {
            LOG.debug("Parsing error", e);
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("the request doesn't match expected syntax " + e).build();
        } catch (InvalidTestRunException | IllegalArgumentException e) {
            LOG.debug("TestRun format Exception", e);
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("the request contains a testRun with bad format" + e).build();
        } catch (NotProvidedStep e) {
            LOG.debug(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("Your script contains a non provided testreport" + e).build();
        } catch (Exception e) {
            LOG.error("Unknown error", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity("an error occurred during execution: " + e).build();
        }
    }
}
