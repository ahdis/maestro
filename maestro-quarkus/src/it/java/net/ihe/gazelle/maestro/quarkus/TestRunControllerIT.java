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

package net.ihe.gazelle.maestro.quarkus;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.restassured.response.Response;
import jakarta.ws.rs.core.MediaType;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.technical.dto.report.TestReportDTO;
import net.ihe.gazelle.maestro.api.technical.dto.report.TestReportSummaryDTO;
import org.apache.http.HttpHeaders;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestProfile(TestRunControllerProfile.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@QuarkusTestResource(value = DatahouseTestResource.class, restrictToAnnotatedClass = true)
class TestRunControllerIT implements QuarkusTestProfile {

    @ConfigProperty(name = "gzl.it-port")
    int port;

    @BeforeAll
    void setUp() throws IOException {
        WireMockSingleton.startServer(port);
        WireMockSingleton.mockCallback();
        WireMockSingleton.mockServiceRegistry();
        WireMockSingleton.mockGetProfiles();
        WireMockSingleton.mockValidate();
        WireMockSingleton.mockKeycloak();
        WireMockSingleton.mockDatahouse();
    }

    @AfterAll
    void tearDown() {
        WireMockSingleton.stop();
    }

    @Test
    void synchronous_test_run_with_one_validation() throws IOException {
        String testRun = WireMockSingleton.getResourceAsString("/Testrun.json");
        Response response = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(testRun)
                .when().post("/testRuns/run")
                .then()
                .extract().response();
        assertNotNull(response);
        System.out.println(response.asString());
        TestReportSummary testReport = response.as(TestReportSummaryDTO.class).getDomain();
        assertTrue(testReport.isValid());
        assertEquals(Result.FAILED, testReport.getResult());
    }

    @Test
    void asynchronous_test_run_with_one_validations() throws IOException {
        String testRun = WireMockSingleton.getResourceAsString("/Testrun.json");
        String callback = "http://localhost:" + port + "/mock/gazelle/rest";
        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(testRun)
                .when().post("/testRuns/run?callback=" + callback)
                .then()
                .statusCode(202);

        TestReport testReport = WireMockSingleton.awaitTestReport();
        assertTrue(testReport.isValid());
        assertEquals(Result.FAILED, testReport.getResult());
    }

    @ParameterizedTest
    @MethodSource("invalidTestRunProvider")
    void should_fail_deserialization(String filePath, String expectedMessage) throws IOException {
        String testRun = WireMockSingleton.getResourceAsString(filePath);
        String response = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(testRun)
                .when().post("/testRuns/run")
                .then()
                .statusCode(400)
                .extract().response()
                .body().asString();

        assertTrue(response.contains(expectedMessage), "Expected message not found in response:\n" + response);
    }

    private static Stream<Arguments> invalidTestRunProvider() {
        return Stream.of(
                Arguments.of("/invalid_step_type.json", "Could not resolve type id"),
                Arguments.of("/invalid_property_type.json", "Unsupported property type"),
                Arguments.of("/invalid_property_name.json", "is not a validation step property"),
                Arguments.of("/invalid_structure.json", "Unable to parse TestRun structure")
        );
    }
}
