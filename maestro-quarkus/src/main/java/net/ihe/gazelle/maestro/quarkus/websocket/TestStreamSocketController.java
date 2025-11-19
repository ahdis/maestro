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
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import net.ihe.gazelle.maestro.api.business.Maestro;
import net.ihe.gazelle.maestro.quarkus.websocket.dto.MessageDTO;
import net.ihe.gazelle.maestro.quarkus.websocket.dto.StartTestRunDTO;
import net.ihe.gazelle.maestro.quarkus.websocket.dto.StartTestSuiteRunDTO;
import net.ihe.gazelle.maestro.quarkus.websocket.dto.UserInteractionCompletedDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@ServerEndpoint("/v1/test-stream/run")
public class TestStreamSocketController {

    private static final Logger LOG = LoggerFactory.getLogger(TestStreamSocketController.class.getName());

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final TextSerDes serDes = new JacksonSerDes();
    private final Maestro maestro;

    @Inject
    public TestStreamSocketController(Maestro maestro) {
        this.maestro = maestro;
    }

    @OnOpen
    public void onOpen(Session session) {
        LOG.debug("Test Stream Socket Connected: {}", session.getId());
        WebSocketSession maestroCaller = new WebSocketSession(session);
        sessions.put(session.getId(), maestroCaller);
    }

    @OnClose
    public void onClose(Session session) {
        LOG.debug("Test Stream Socket Disconnected: {}", session.getId());
        sessions.remove(session.getId());
    }

    @OnMessage
    public void onMessage(Session session, String messageString) {
        MessageDTO<?> message = serDes.deserialize(messageString, MessageDTO.class);
        WebSocketSession maestroCaller = sessions.get(session.getId());
        switch (message) {
            case StartTestRunDTO testRun -> maestro.executeTest(testRun.getBusinessObject(), testRun.isPersist(), maestroCaller);
            case StartTestSuiteRunDTO testSuiteRun -> maestro.executeTestSuite(testSuiteRun.getBusinessObject(), testSuiteRun.isPersist(), maestroCaller);
            case UserInteractionCompletedDTO userInteractionCompleted -> maestroCaller.completeInteraction(userInteractionCompleted.getBusinessObject());
            default -> throw new IllegalArgumentException("Unexpected message type: " + message.getClass().getName());
        }
    }
}


