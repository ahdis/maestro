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

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.ihe.gazelle.maestro.api.technical.dto.report.TestReportDTO;
import net.ihe.gazelle.maestro.api.technical.dto.testrun.TestRunDTO;
import net.ihe.gazelle.maestro.api.technical.dto.testrun.TestSuiteRunDTO;
import net.ihe.gazelle.sso.server.technical.filter.ProtectedResource;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import static net.ihe.gazelle.maestro.quarkus.ws.Constants.TEST_RUN;
import static net.ihe.gazelle.maestro.quarkus.ws.Constants.TEST_SUITE_RUN;

@Path("/v1")
public interface TestRunController {

    /**
     * This endpoint can run synchronously or asynchronously depending on the presence of a callback URI.
     * <p>
     * If the callback query parameter is not set, this will run synchronously and directly send the {@code TestReport}
     * as a response.
     * </p>
     * <p>
     * If the callback query parameter is set, this will run asynchronously and will send the {@code TestReport} to the
     * specified callback URI later on.
     * </p>
     * <p>
     * Different responses can be sent depending on what happens during execution:
     * </p>
     * <ul>
     *     <li><b>Execution went well</b> : 200 + TestReport in body if callback is not present and run is synchronous</li>
     *     <li><b>Execution went well</b> : 202 If callback is present and run is asynchronous </li>
     *     <li><b>Request couldn't be Parsed</b> : 400 + ParsingErrorException or MissingArgumentException</li>
     *     <li><b>TestRun isn't valid</b> : 400 + InvalidTestRunException</li>
     *     <li><b>Request try to execute an unsupported Step</b> : 400 + UnknownStepType</li>
     *     <li><b>Unknown Error occurred</b> : 500 + Exception</li>
     *     <li><b>Timeout exception</b> : 504 + message explaining why timeout occurred</li>
     * </ul>
     *
     * @param testSuiteRun A list of TestRun
     * @param callback   The callback URI to send back the Test report for asynchronous run
     *
     * @return A response with a status code and a body containing TestReport or an exception
     */
    @POST
    @Path("/test-suite/run")
    @ProtectedResource
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Test Run API")
    @SecurityRequirement(name = "Keycloak")
    @Operation(
            summary = "Run Gazelle test suite",
            description = """
                       This API is used to execute a Gazelle test suite with the maestro engine.
                       If the callback query parameter is not set, this will run synchronously and directly send the Test report as a response.
                       If the callback query parameter is set, this will run asynchronously and will send the Test report to the specified callback URI later on.
                    """
    )
    @APIResponse(
            responseCode = "200",
            description = "Test suite has been executed, Contains the Test report with all detailed steps and results.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = TestReportDTO.class)
            )
    )
    @APIResponse(
            responseCode = "202",
            description = "Test run accepted"
    )
    @APIResponse(
            responseCode = "400",
            description = "The test run is malformed"
    )
    @APIResponse(
          responseCode = "401",
          description = "Unauthorized to execute a test suite run"
    )
    @APIResponse(
            responseCode = "500",
            description = "Unexpected Error"
    )
    @APIResponse(
            responseCode = "504",
            description = "Synchronous run has timed out"
    )
    Response executeTestSuite(
            @RequestBody(
                    description = "The test suite to run.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = TestSuiteRunDTO.class),
                            example = TEST_SUITE_RUN
                    )
            ) TestSuiteRunDTO testSuiteRun,
            @Parameter(
                    in = ParameterIn.QUERY,
                    name = "persist",
                    description = "Choose whether to persist the test report or not"
            ) @QueryParam("persist") @DefaultValue("true") boolean persist,
            @Parameter(
                    in = ParameterIn.QUERY,
                    name = "callback",
                    description = "Callback URL for asynchronous test run. For such callback to be secured, the client should generate a unique and strong sessionId included into the callback URL.",
                    example = "http://localhost/gazelle/report?session=10"
            ) @QueryParam("callback") String callback
    );


    @POST
    @Path("/test/run")
    @ProtectedResource
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Test Run API")
    @SecurityRequirement(name = "Keycloak")
    @Operation(
            summary = "Run Gazelle test",
            description = """
                       This API is used to execute a Gazelle test suite with the maestro engine.
                       If the callback query parameter is not set, this will run synchronously and directly send the Test report as a response.
                       If the callback query parameter is set, this will run asynchronously and will send the Test report to the specified callback URI later on.
                    """
    )
    @APIResponse(
            responseCode = "200",
            description = "Test has been executed, Contains the Test report with all detailed steps and results.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = TestReportDTO.class)
            )
    )
    @APIResponse(
            responseCode = "202",
            description = "Test run accepted"
    )
    @APIResponse(
            responseCode = "400",
            description = "The test run is malformed"
    )
    @APIResponse(
          responseCode = "401",
          description = "Unauthorized to execute a test run"
    )
    @APIResponse(
            responseCode = "500",
            description = "Unexpected Error"
    )
    @APIResponse(
            responseCode = "504",
            description = "Synchronous run has timed out"
    )
    Response executeTest(
            @RequestBody(
                    description = "The test to run.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = TestRunDTO.class),
                            example = TEST_RUN
                    )
            ) TestRunDTO testRun,
            @Parameter(
                    in = ParameterIn.QUERY,
                    name = "persist",
                    description = "Choose whether to persist the test report or not"
            ) @QueryParam("persist") @DefaultValue("true") boolean persist,
            @Parameter(
                    in = ParameterIn.QUERY,
                    name = "callback",
                    description = "Callback URL for asynchronous test run. For such callback to be secured, the client should generate a unique and strong sessionId included into the callback URL.",
                    example = "http://localhost/gazelle/report?session=10"
            ) @QueryParam("callback") String callback
    );

}
