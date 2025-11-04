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

import net.ihe.gazelle.itb.gateway.technical.dao.ItbSessionStoreImpl;
import net.ihe.gazelle.maestro.quarkus.WireMockSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.awaitility.Awaitility.await;

public class ItbStepWireMock {

    private static final Logger logger = LoggerFactory.getLogger(ItbStepWireMock.class.getName());



    private ItbStepWireMock() {
        // Utility class
    }

    public static void mockItbTestExecution(int maestroPort) {
        // Mock the ITB test execution endpoint
        WireMockSingleton.startServer(0).stubFor(post(urlPathEqualTo("/api/rest/tests/start"))
                .willReturn(ok().withBody("{\"createdSessions\":[{\"session\":\"test-session-123\"}]}"))
        );

        // Simulate ITB callback after a short delay
        simulateItbCallback(maestroPort, "test-session-123");
    }

    public static void mockItbTestLogs() throws IOException {
        String logs = WireMockSingleton.getResourceAsString("/itb/test-logs.txt");
        WireMockSingleton.startServer(0).stubFor(get(urlPathEqualTo("/test/test-session-123/logs"))
                .willReturn(ok().withBody(logs))
        );
    }

    public static void mockItbPdfReport() {
        byte[] pdfContent = "Mock PDF content".getBytes();
        WireMockSingleton.startServer(0).stubFor(get(urlPathEqualTo("/test/test-session-123/pdf"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/pdf")
                        .withBody(pdfContent))
        );
    }

    private static void simulateItbCallback(int maestroPort, String sessionId) {
        // Schedule a callback to be sent after the test execution is started
        CompletableFuture.runAsync(() -> {
            try {
                // Wait a bit to ensure the test execution has started
                await()
                        .atMost(5, TimeUnit.SECONDS)
                        .until(() -> ItbSessionStoreImpl.get(sessionId) != null);

                // Get the callback payload
                String callbackPayload = WireMockSingleton.getResourceAsString("/itb/ItbCallbackSuccess.json");

                // Make the actual HTTP call to trigger the callback to the real Maestro endpoint
                makeCallbackRequest(callbackPayload, maestroPort);

            } catch (Exception e) {
                throw new RuntimeException("Failed to simulate ITB callback", e);
            }
        });
    }

    private static void makeCallbackRequest(String payload, int maestroPort) {
        try {
            // Use Java's HTTP client to make the callback request to the actual Maestro endpoint
            logger.info("payload: {}", payload);
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            String url = "http://localhost:" + maestroPort + "/maestro/itb/report";
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.error("Callback request to {} failed with status code: {} and body {}", url, response.statusCode(), response.body());
            } else {
                logger.info("Callback request successful: {}", response.body());
            }
        } catch (Exception e) {
            logger.error("Error making callback request", e);
            throw new RuntimeException("Failed to make callback request", e);
        }
    }
}
