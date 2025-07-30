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

package net.ihe.gazelle.maestro.quarkus.websocket.client;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.maestro.api.technical.dto.report.StepRunReportDTO;
import net.ihe.gazelle.maestro.api.technical.dto.report.TestRunReportDTO;
import net.ihe.gazelle.maestro.quarkus.websocket.WSMessageDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.jackson.ObjectMapperBuilder;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Internal Client for sending test run messages over WebSocket.
 * This client connects to the WebSocket server and sends messages related to test step completion and test run completion.
 */
public class TestRunSocketClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunSocketClient.class);
    private static final String WEB_SOCKET_URL = Objects.requireNonNull(ConfigProvider.getConfig().getValue("gzl.websocket.url", String.class));
    private static final String WEB_SOCKET_TOKEN = ConfigProvider.getConfig().getValue("gzl.websocket.token", String.class);
    private static final String WEB_SOCKET_INTERNAL_URL = WEB_SOCKET_URL + "/internal";
    private static final int CONNECT_TIMEOUT_SECONDS = 5;
    private final TextSerDes serDes = new JacksonSerDes(new ObjectMapperBuilder().build());

    private final TestRunSocketClientEndpoint client = new TestRunSocketClientEndpoint();
    private Session session;

    public void sendTestStepCompleted(String testSetId, StepRunReport stepRunReport) {
        String content = serDes.serializeAsString(new StepRunReportDTO(stepRunReport));
        WSMessageDTO message = new WSMessageDTO(testSetId, "testStepCompleted", content);
        sendMessage(message);
    }

    public void sendTestRunCompleted(String testSetId, TestRunReport testRunReport) {
        String content = serDes.serializeAsString(new TestRunReportDTO(testRunReport));
        WSMessageDTO message = new WSMessageDTO(testSetId, "testRunCompleted", content);
        sendMessage(message);
    }

    private void sendMessage(WSMessageDTO message) {
        try {
            if (session == null || !session.isOpen()) {
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                container.setDefaultMaxSessionIdleTimeout(TimeUnit.SECONDS.toMillis(CONNECT_TIMEOUT_SECONDS));
                String completeUri = WEB_SOCKET_TOKEN != null && !WEB_SOCKET_TOKEN.isBlank() ? WEB_SOCKET_INTERNAL_URL + "?token=" + WEB_SOCKET_TOKEN : WEB_SOCKET_INTERNAL_URL;
                session = container.connectToServer(client, new URI(completeUri));
            }

            client.sendWSMessage(message);
        } catch (Exception e) {
            LOGGER.error("Failed to send websocket message to {}: {}", WEB_SOCKET_INTERNAL_URL, e.getMessage());
        }
    }

    public void close() {
        if (session != null && session.isOpen()) {
            try {
                session.close();
                LOGGER.info("WebSocket connection closed");
            } catch (Exception e) {
                LOGGER.error("Error closing WebSocket connection", e);
            }
        }
    }
}