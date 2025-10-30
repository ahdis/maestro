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

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.restassured.response.Response;
import jakarta.ws.rs.core.MediaType;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.business.testreport.validator.TestReportSummaryValidator;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@TestProfile(TestRunControllerProfile.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestRunControllerIT implements QuarkusTestProfile {

    @ConfigProperty(name = "gzl.it.port")
    int port;

    TestReportSummaryValidator summaryValidator = new TestReportSummaryValidator();

    @BeforeAll
    void setUp() throws IOException {
        WireMockSingleton.startServer(port);
        WireMockSingleton.mockCallback();
        WireMockSingleton.mockServiceRegistry();
        WireMockSingleton.mockGetProfiles();
        WireMockSingleton.mockEvsProfiles();
        WireMockSingleton.mockValidate();
        WireMockSingleton.mockKeycloak();
        MockDatahouseServer.start(34501);
    }

    @AfterAll
    void tearDown() {
        WireMockSingleton.stop();
        try {
            MockDatahouseServer.stop();
        } catch (IOException e) {
            // log and ignore in teardown
            e.printStackTrace();
        }
    }

    @Test
    void synchronous_test_executeTestSuite_with_one_validation() throws IOException {
        String testRun = WireMockSingleton.getResourceAsString("/Testrun.json");
        Response response = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(testRun)
                .when().post("/testRuns/run")
                .then()
                .extract().response();
        assertNotNull(response);
        System.out.println(response.asString());
        TestReportSummary testReport = response.as(TestReportSummaryDTO.class).getBusinessObject();
        summaryValidator.assertValid(testReport);
        assertEquals(Result.FAILED, testReport.getResult());
    }

    @Test
    void asynchronous_test_executeTestSuite_with_one_validations() throws IOException {
        String testRun = WireMockSingleton.getResourceAsString("/Testrun.json");
        String callback = "http://localhost:" + port + "/mock/gazelle/rest";
        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(testRun)
                .when().post("/testRuns/run?callback=" + callback)
                .then()
                .statusCode(202);

        TestReportSummary testReport = WireMockSingleton.awaitTestReport();
        summaryValidator.assertValid(testReport);
        assertEquals(Result.FAILED, testReport.getResult());
    }

    @ParameterizedTest
    @MethodSource("invalidTestExecuteTestSuiteProvider")
    void should_fail_deserialization(String filePath, String expectedMessage) throws IOException {
        String testRun = WireMockSingleton.getResourceAsString(filePath);
        String response = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(testRun)
                .when().post("/testRuns/run")
                .then()
                .statusCode(400)
                .extract().response()
                .body().asString();

        assertThat(response, containsString(expectedMessage));
    }

    private static Stream<Arguments> invalidTestExecuteTestSuiteProvider() {
        return Stream.of(
                Arguments.of("/invalid_step_type.json", "Could not resolve type id"),
                Arguments.of("/invalid_property_type.json", "Unsupported property type"),
                Arguments.of("/invalid_property_name.json", "Property invalid is neither a validation step property nor a content to validate"),
                Arguments.of("/invalid_structure.json", "Unable to parse TestSuite structure")
        );
    }
}
