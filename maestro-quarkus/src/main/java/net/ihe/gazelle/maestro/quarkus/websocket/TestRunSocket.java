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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.technical.dto.testrun.TestRunDTO;
import net.ihe.gazelle.maestro.quarkus.ws.async.WebSocketMaestroService;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.jackson.ObjectMapperBuilder;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@ServerEndpoint("/testRuns/{testSetId}")
@ApplicationScoped
public class TestRunSocket {

    private static final Logger LOG = LoggerFactory.getLogger(TestRunSocket.class.getName());
    public static final String INTERNAL_TEST_RUN_ID = "internal";
    private final TextSerDes serDes = new JacksonSerDes(new ObjectMapperBuilder().build());
    private final WebSocketMaestroService maestroService;
    private final String websocketToken;

    @Inject
    public TestRunSocket(WebSocketMaestroService maestroService, @ConfigProperty(name = "gzl.websocket.token") String websocketToken) {
        this.maestroService = maestroService;
        this.websocketToken = websocketToken;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("testSetId") String testSetId) throws IOException {
        List<String> tokenParams = session.getRequestParameterMap().get("token");
        if (!providedTokenIsValid(tokenParams))
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Invalid token provided"));
        if (!INTERNAL_TEST_RUN_ID.equals(testSetId))
            SessionsStorageInMemory.addSession(testSetId, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("testSetId") String testSetId) {
        SessionsStorageInMemory.removeSession(testSetId);
    }

    @OnError
    public void onError(Session session, @PathParam("testSetId") String testSetId, Throwable throwable) {
        SessionsStorageInMemory.removeSession(testSetId);
        LOG.error("Test set {} ended on error: {} ", testSetId, throwable.getMessage());
    }

    @OnMessage
    public void onMessage(String message, @PathParam("testSetId") String testSetId) {
        // Ping for test purpose
        if (message.equals("ping")) {
            sendPongToClient(testSetId);
        } else if (testSetId.equals(INTERNAL_TEST_RUN_ID)) {
            sendMessageToClient(message);
        } else {
            triggerTestRuns(testSetId, message);
        }
    }

    private void triggerTestRuns(String testSetId, String message) {
        LOG.info("Triggering test runs for client {} with message: {}",testSetId, message);
        try {
            TestRunDTO[] testRunDTOS = serDes.deserialize(message, TestRunDTO[].class);
            List<TestRun> testRuns = Arrays.stream(testRunDTOS).map(TestRunDTO::getDomain).toList();
            maestroService.executeTestSet(testSetId, testRuns);
        } catch (Exception e) {
            LOG.error("Unable to trigger testRun for message {}: {}", testSetId, e.getMessage());
        }
    }

    private void sendMessageToClient(String message) {
        String testSetId = serDes.deserialize(message, WSMessageDTO.class).getTestSetId();
        LOG.info("Sending message for testSetId : {}", testSetId);
        Session session = SessionsStorageInMemory.getSession(testSetId);
        if (session != null) {
            session.getAsyncRemote()
                    .sendObject(message, result -> logPotentialException(testSetId, result));
        }
    }

    private boolean providedTokenIsValid(List<String> paramValues) {
        if (paramValues == null || paramValues.isEmpty()) {
            return websocketToken == null || websocketToken.isBlank();
        }
        String token = paramValues.getFirst();
        if (token != null && !token.isEmpty()) {
            return token.equals(websocketToken);
        }

        return true;
    }

    private void sendPongToClient( String testSetId) {
        Session session = SessionsStorageInMemory.getSession(testSetId);
        session.getAsyncRemote()
                .sendObject("pong", result -> logPotentialException(testSetId, result));
    }

    private static void logPotentialException(String testSetId, SendResult result) {
        if (result.getException() != null) {
            LOG.error("Unable to send message to {}: {}", testSetId, result.getException().getMessage());
        }
    }
}
