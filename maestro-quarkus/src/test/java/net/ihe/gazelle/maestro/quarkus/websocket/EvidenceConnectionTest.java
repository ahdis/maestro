/*
 * Copyright 2026 IHE International.
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;

import net.ihe.gazelle.maestro.api.business.Maestro;
import net.ihe.gazelle.maestro.api.business.MaestroSessionService;
import net.ihe.gazelle.maestro.api.business.message.TestRunStarted;
import net.ihe.gazelle.maestro.engine.business.TestReportRecordingException;
import net.ihe.gazelle.security.business.Authz;
import net.ihe.gazelle.security.business.GazelleIdentity;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

class EvidenceConnectionTest {
    @Test
    void recordingFailuresCloseWithAValidWebsocketStatus() throws Exception {
        Session socket = mock(Session.class);
        Mockito.when(socket.getId()).thenReturn("socket");
        WebSocketSessionService sessions = mock(WebSocketSessionService.class);
        Mockito.when(sessions.getSession("socket"))
                .thenThrow(new TestReportRecordingException("Evidence storage unavailable"));
        TestStreamSocketController controller =
                new TestStreamSocketController(
                        mock(GazelleIdentity.class),
                        mock(Authz.class),
                        mock(Maestro.class),
                        sessions,
                        mock(MaestroSessionService.class));
        controller.onMessage(
                socket,
                """
                {"type":"PUT_EVIDENCE","runId":1,"stepName":"step","content":{
                  "type":"STRING","name":"proof","value":"content"}}
                """);
        Mockito.verify(socket)
                .close(
                        ArgumentMatchers.argThat(
                                reason ->
                                        reason.getCloseCode()
                                                == CloseReason.CloseCodes.UNEXPECTED_CONDITION));
    }

    @Test
    void evidenceIsBoundToTheConnectionThatStartedItsExecution() {
        Session socket = mock(Session.class, RETURNS_DEEP_STUBS);
        WebSocketSession owner = new WebSocketSession(socket);
        WebSocketSession other = new WebSocketSession(socket);
        assertThrows(
                IllegalArgumentException.class, () -> owner.assertEvidenceSession("execution"));
        owner.onTestRunStarted(new TestRunStarted("execution", "test"));
        assertDoesNotThrow(() -> owner.assertEvidenceSession("execution"));
        assertThrows(
                IllegalArgumentException.class, () -> other.assertEvidenceSession("execution"));
        assertThrows(IllegalArgumentException.class, () -> owner.assertEvidenceSession(null));
    }
}
