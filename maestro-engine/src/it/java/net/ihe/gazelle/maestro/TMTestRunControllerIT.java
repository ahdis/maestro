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

package net.ihe.gazelle.maestro;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.InputStream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestProfile(TMTestRunControllerProfile.class)
class TMTestRunControllerIT implements QuarkusTestProfile {

    @ConfigProperty(name = "gzl.it-port")
    int port;

    @BeforeEach
    public void setUp() throws IOException {
        WireMockSingleton.startServer(port);
        WireMockSingleton.mockCallback();
        WireMockSingleton.mockServiceRegistry();
        WireMockSingleton.mockGetProfiles();
    }

    @AfterEach
    public void tearDown() {
        WireMockSingleton.stop();
    }

    @Test
    void runPASSEDTestFromTM() throws IOException {
        WireMockSingleton.mockValidate("/tmintegration/passed_response.json");

        String tmRequestBody = assertDoesNotThrow(() -> getResourceAsString("tmintegration/script.json"));
        tmRequestBody = tmRequestBody.replace(":port", ":" + port);

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(tmRequestBody)
                .when().post("/v0/run")
                .then().statusCode(200);

        TestReport testReport =  WireMockSingleton.awaitTestReport();
        assertTrue(testReport.isValid());
        assertEquals(Result.PASSED, testReport.getResult());
    }

    @Test
    void runFAILEDTestFromTM() throws IOException {
        WireMockSingleton.mockValidate("/tmintegration/failed_response.json");

        String tmRequestBody = assertDoesNotThrow(() -> getResourceAsString("tmintegration/script.json"));
        tmRequestBody = tmRequestBody.replace(":port", ":" + port);

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(tmRequestBody)
                .when().post("/v0/run")
                .then().statusCode(200);

        TestReport testReport = WireMockSingleton.awaitTestReport();
        assertTrue(testReport.isValid());
        assertEquals(Result.FAILED, testReport.getResult());
    }

    public static String getResourceAsString(String resourcePath) throws IOException {
        try(InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            assert stream != null;
            return new String(stream.readAllBytes());
        }
    }
}
