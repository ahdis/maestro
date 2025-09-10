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
import net.ihe.gazelle.maestro.api.business.testrun.InvalidTestRunException;
import net.ihe.gazelle.maestro.api.technical.dto.report.TestReportDTO;
import net.ihe.gazelle.maestro.api.technical.dto.testrun.TestRunDTO;
import net.ihe.gazelle.maestro.business.MissingArgumentException;
import net.ihe.gazelle.maestro.quarkus.NotProvidedStep;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/testRuns")
public interface TestRunController {

    /**
     * This endpoint can run synchronously or asynchronously depending on the presence of a callback URI.
     * <p>
     * If the callback query parameter is not set, this will run synchronously and directly send the
     * {@code TestReport} as a response.
     * </p>
     * <p>
     * If the callback query parameter is set, this will run asynchronously and will send the
     * {@code TestReport} to the specified callback URI later on.
     * </p>
     * <p>
     * Different responses can be sent depending on what happens during execution:
     * </p>
     *
     * <ul>
     *   <li><b>Execution successful</b>: <code>200</code> + {@code TestReport} in body (if callback is not present and run is synchronous)</li>
     *   <li><b>Execution successful</b>: <code>202</code> (if callback is present and run is asynchronous)</li>
     *   <li><b>Request couldn't be parsed</b>: <code>400</code> + {@link ParsingErrorException} or {@link MissingArgumentException}</li>
     *   <li><b>TestRun isn't valid</b>: <code>400</code> + {@link InvalidTestRunException}</li>
     *   <li><b>Unsupported step execution</b>: <code>400</code> + {@link NotProvidedStep}</li>
     *   <li><b>Unknown error occurred</b>: <code>500</code> + {@link Exception}</li>
     *   <li><b>Timeout exception</b>: <code>504</code> + message explaining why timeout occurred</li>
     * </ul>
     *
     * @param callback the callback URI to send back the test report for asynchronous runs
     * @param testRuns a list of {@code TestRun} objects
     * @return a response with a status code and a body containing {@code TestReport} or an exception
     */
    @POST
    @Path("/run")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Maestro API")
    @Operation(
            summary = "Run Gazelle tests",
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
            responseCode = "500",
            description = "Unexpected Error"
    )
    @APIResponse(
            responseCode = "504",
            description = "Synchronous run has timed out"
    )
    Response run(
            @Parameter(
                    in = ParameterIn.QUERY,
                    name = "callback",
                    description = "Callback URL for asynchronous test run. For such callback to be secured, the client should generate a unique and strong sessionId included into the callback URL.",
                    example = "http://localhost/gazelle/?session=10"
            ) @QueryParam("callback") String callback,
            @RequestBody(
                    description = "The tests to run.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = TestRunDTO[].class),
                            example = TEST_RUN
                    )
            ) List<TestRunDTO> testRuns
    );

    String TEST_RUN = """
            [
              {
                "testSetId": "testSet1",
                "test": {
                  "id": "1",
                  "name": "name1",
                  "steps": [
                    {
                      "type": "VALIDATION",
                      "name": "step1",
                      "properties": [
                        {
                          "name": "validationService",
                          "value": "matchbox",
                          "type": "string"
                        },
                        {
                          "name": "validationProfile",
                          "value": "parameters",
                          "type": "string"
                        },
                        {
                          "name": "contentToValidate",
                          "value": "${inputFile1}",
                          "type": "byteArray"
                        }
                      ]
                    }
                  ]
                },
                "inputs": [
                  {
                    "name": "inputFile1",
                    "value": "ihfeorhqifrghqpervgh",
                    "type": "byteArray"
                  }
                ]
              }
            ]
            """;
}
