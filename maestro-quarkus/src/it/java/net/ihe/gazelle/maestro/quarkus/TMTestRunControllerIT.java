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
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.business.testreport.validator.TestReportSummaryValidator;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.InputStream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestProfile(TMTestRunControllerProfile.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TMTestRunControllerIT implements QuarkusTestProfile {

   @ConfigProperty(name = "gzl.it.port")
   int port;

   TestReportSummaryValidator summaryValidator = new TestReportSummaryValidator();

   @BeforeAll
   void beforeAll() throws IOException {
      MockDatahouseServer.start(34501);
      WireMockSingleton.startServer(port);
      WireMockSingleton.mockCallback();
      WireMockSingleton.mockServiceRegistry();
      WireMockSingleton.mockGetProfiles();
   }

   @BeforeEach
   void beforeEach() throws IOException {
      WireMockSingleton.mockCallback();
      WireMockSingleton.mockServiceRegistry();
      WireMockSingleton.mockGetProfiles();
      WireMockSingleton.mockKeycloak();
   }

   @AfterEach
   void afterEach() {
      WireMockSingleton.getWireMockServer().resetAll();
   }

   @AfterAll
   void afterAll() throws IOException {
      MockDatahouseServer.stop();
      WireMockSingleton.stop();
   }

   @Test
   void executeTestSuitePASSEDTestFromTM() throws IOException {
      WireMockSingleton.mockValidate("/tmintegration/passed_response.json");

      String tmRequestBody = assertDoesNotThrow(() -> getResourceAsString("tmintegration/script.json"));
      tmRequestBody = tmRequestBody.replace(":port", ":" + port);

      given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .body(tmRequestBody)
            .when().post("/v0/run")
            .then().statusCode(200);

      TestReportSummary reportSummary = WireMockSingleton.awaitTestReport();
      summaryValidator.assertValid(reportSummary);
      assertEquals(Result.PASSED, reportSummary.getResult());
   }

   @Test
   void executeTestSuiteFAILEDTestFromTM() throws IOException {
      WireMockSingleton.mockValidate("/tmintegration/failed_response.json");

      String tmRequestBody = assertDoesNotThrow(() -> getResourceAsString("tmintegration/script.json"));
      tmRequestBody = tmRequestBody.replace(":port", ":" + port);

      given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .body(tmRequestBody)
            .when().post("/v0/run")
            .then().statusCode(200);

      TestReportSummary reportSummary = WireMockSingleton.awaitTestReport();
      summaryValidator.assertValid(reportSummary);
      assertEquals(Result.FAILED, reportSummary.getResult());
   }

   public static String getResourceAsString(String resourcePath) throws IOException {
      try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
         assert stream != null;
         return new String(stream.readAllBytes());
      }
   }
}
