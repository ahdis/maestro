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

package net.ihe.gazelle.maestro.quarkus.itb;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.response.Response;
import jakarta.ws.rs.core.MediaType;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.technical.dto.report.TestReportSummaryDTO;
import net.ihe.gazelle.maestro.quarkus.WireMockSingleton;
import org.apache.http.HttpHeaders;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestProfile(ItbStepProfile.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItbStepIT {

    @ConfigProperty(name = "gzl.it-port")
    int port;

    @ConfigProperty(name = "quarkus.http.test-port")
    int maestroPort;

    @BeforeAll
    void setUp() throws IOException {
        WireMockSingleton.startServer(port);
        WireMockSingleton.mockCallback();
        WireMockSingleton.mockServiceRegistry();
        ItbStepWireMock.mockItbTestExecution(maestroPort);
        ItbStepWireMock.mockItbTestLogs();
        ItbStepWireMock.mockItbPdfReport();
        WireMockSingleton.mockKeycloak();
        // Start MockDatahouseServer statically on port 8080
        net.ihe.gazelle.maestro.quarkus.MockDatahouseServer.start(8080);
    }

    @AfterAll
    void tearDownAll() throws IOException {
        net.ihe.gazelle.maestro.quarkus.MockDatahouseServer.stop();
    }

    @Test
    void synchronous_itb_test_run_with_success_result() throws IOException {
        String testRun = WireMockSingleton.getResourceAsString("/itb/ItbTestRunSuccess.json");
        Response response = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(testRun)
                .when().post("/testRuns/run")
                .then()
                .extract().response();

        assertNotNull(response);
        TestReportSummary testReport = response.as(TestReportSummaryDTO.class).getDomain();
        assertTrue(testReport.isValid());
        assertEquals(Result.PASSED, testReport.getResult());
    }


    @Test
    void itb_callback_with_invalid_session_id() throws IOException {
        String itbCallback = WireMockSingleton.getResourceAsString("/itb/ItbCallbackInvalidSession.json");
        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(itbCallback)
                .when().post("/itb/report")
                .then()
                .statusCode(404);
    }


}
