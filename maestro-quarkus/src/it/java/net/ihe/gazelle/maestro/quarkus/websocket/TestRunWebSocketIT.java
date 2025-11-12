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

package net.ihe.gazelle.maestro.quarkus.websocket;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import net.ihe.gazelle.maestro.api.technical.dto.report.TestRunReportDTO;
import net.ihe.gazelle.maestro.quarkus.TestRunControllerProfile;
import net.ihe.gazelle.maestro.quarkus.WireMockSingleton;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.jackson.ObjectMapperBuilder;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@TestProfile(TestRunControllerProfile.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestRunWebSocketIT implements QuarkusTestProfile {

    private static final String WEBSOCKET_URL = Objects.requireNonNull(ConfigProvider.getConfig().getValue("gzl.websocket.url", String.class));
    private static final long TIMEOUT_MS = 500;
    private final TextSerDes serDes = new JacksonSerDes(new ObjectMapperBuilder().build());

    @ConfigProperty(name = "gzl.it.port")
    int port;

    @BeforeAll
    void setUp() throws IOException {
        WireMockSingleton.startServer(port);
        WireMockSingleton.mockCallback();
        WireMockSingleton.mockServiceRegistry();
        WireMockSingleton.mockGetProfiles();
        WireMockSingleton.mockValidate();
    }

    @AfterAll
    void tearDown() {
        WireMockSingleton.stop();
    }

    void testWebSocketTestRun() throws Exception {
        String testRun = WireMockSingleton.getResourceAsString("/Testrun.json");
        WebSocketTestClient client = new WebSocketTestClient();
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        try (Session ignored = container.connectToServer(client, new URI(WEBSOCKET_URL + "/testSet1?token=secret"))) {
            String response = client.pollLastMessage(TIMEOUT_MS);
            Assertions.assertNull(response, "No response received");
            client.sendMessage(testRun);

            response = client.pollLastMessage(TIMEOUT_MS);
            assertNotNull(response);
            TestRunReportDTO testReport = serDes.deserialize(response, TestRunReportDTO.class);
            assertEquals("testSet1", testReport.getRunId());

            response = client.pollLastMessage(TIMEOUT_MS);
            assertNotNull(response);
            testReport = serDes.deserialize(response, TestRunReportDTO.class);
            assertEquals("testSet1", testReport.getRunId());
        }
    }
}
