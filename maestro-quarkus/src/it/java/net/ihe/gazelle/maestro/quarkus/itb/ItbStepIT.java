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

/*@QuarkusTest
@TestProfile(ItbStepProfile.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItbStepIT {

    @ConfigProperty(name = "gzl.it.port")
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
        MockDatahouseServer.start(34501);
    }

    @AfterAll
    void tearDownAll() throws IOException {
        WireMockSingleton.stop();
        MockDatahouseServer.stop();
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


}*/
