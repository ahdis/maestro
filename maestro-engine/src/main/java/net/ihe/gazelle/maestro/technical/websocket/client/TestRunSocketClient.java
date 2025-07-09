/*
 * Copyright 2025 IHE International.
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

package net.ihe.gazelle.maestro.technical.websocket.client;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.maestro.technical.dto.report.StepRunReportDTO;
import net.ihe.gazelle.maestro.technical.dto.report.TestRunReportDTO;
import net.ihe.gazelle.maestro.technical.websocket.WSMessageDTO;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal Client for sending test run messages over WebSocket.
 * This client connects to the WebSocket server and sends messages related to test step completion and test run completion.
 */
public class TestRunSocketClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunSocketClient.class);
    private static final String webSocketUrl = Objects.requireNonNull(ConfigProvider.getConfig().getValue("gzl.websocket.url", String.class));
    private static final String webSocketToken = ConfigProvider.getConfig().getValue("gzl.websocket.token", String.class);
    private static final String webSocketInternalUrl = webSocketUrl + "/internal";
    private static final int CONNECT_TIMEOUT_SECONDS = 5;

    private final TestRunSocketClientEndpoint client = new TestRunSocketClientEndpoint();
    private Session session;

    public void sendTestStepCompleted(String testSetId, StepRunReport stepRunReport) {
        WSMessageDTO<StepRunReportDTO> message = new WSMessageDTO<>(testSetId, "testStepCompleted", new StepRunReportDTO(stepRunReport));
        sendMessage(message);
    }

    public void sendTestRunCompleted(String testSetId, TestRunReport testRunReport) {
        WSMessageDTO<TestRunReportDTO> message = new WSMessageDTO<>(testSetId, "testRunCompleted", new TestRunReportDTO(testRunReport));
        sendMessage(message);
    }

    private void sendMessage(WSMessageDTO<?> message) {
        try {
            if (session == null || !session.isOpen()) {
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                container.setDefaultMaxSessionIdleTimeout(TimeUnit.SECONDS.toMillis(CONNECT_TIMEOUT_SECONDS));
                String completeUri = webSocketToken != null && !webSocketToken.isBlank() ? webSocketInternalUrl + "?token=" + webSocketToken : webSocketInternalUrl;
                session = container.connectToServer(client, new URI(completeUri));
            }

            client.sendWSMessage(message);
        } catch (Exception e) {
            LOGGER.error("Failed to send websocket message to {}: {}", webSocketInternalUrl, e.getMessage());
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