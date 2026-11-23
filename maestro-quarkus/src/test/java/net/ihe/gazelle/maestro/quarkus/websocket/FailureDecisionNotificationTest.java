/*
 * Copyright 2025-2026 IHE International.
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

import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.SendHandler;
import jakarta.websocket.SendResult;
import jakarta.websocket.Session;
import net.ihe.gazelle.maestro.api.business.message.StepFailureDecision;
import net.ihe.gazelle.maestro.api.business.message.StepFailureDecisionRequested;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FailureDecisionNotificationTest {
    private final Session socket = mock(Session.class);
    private final RemoteEndpoint.Async remote = mock(RemoteEndpoint.Async.class);
    private final WebSocketSession caller = new WebSocketSession(socket);
    private final StepFailureDecisionRequested request =
            new StepFailureDecisionRequested("run", 2, "Check", StepResult.FAILED);

    FailureDecisionNotificationTest() {
        when(socket.getAsyncRemote()).thenReturn(remote);
    }

    @Test
    void acceptsOnlyTheFirstAnswerMatchingRunAndStep() {
        var pending = caller.decideAfterStepFailure(request);
        assertSame(pending, caller.decideAfterStepFailure(request));
        var callback = ArgumentCaptor.forClass(SendHandler.class);
        verify(remote).sendText(anyString(), callback.capture());
        callback.getValue().onResult(new SendResult());
        caller.completeFailureDecision(new StepFailureDecision("other-run", 2, true));
        caller.completeFailureDecision(new StepFailureDecision("run", 1, true));
        assertFalse(pending.isDone());
        caller.completeFailureDecision(new StepFailureDecision("run", 2, true));
        caller.completeFailureDecision(new StepFailureDecision("run", 2, false));
        assertTrue(pending.join());
    }

    @Test
    void routesAnIncomingStopToTheRegisteredSocket() throws Exception {
        var pending = caller.decideAfterStepFailure(request);
        var sessions = mock(WebSocketSessionService.class);
        when(socket.getId()).thenReturn("socket");
        when(sessions.getSession("socket")).thenReturn(caller);
        var controller = new TestStreamSocketController(null, null, null, null, sessions, null);
        controller.onMessage(socket, """
                {"type":"STEP_FAILURE_DECISION","maestroSessionId":"run","stepIndex":2,"continueExecution":false}
                """);
        assertFalse(pending.getNow(true));
    }

    @Test
    void stopsWhenNotificationCannotBeDelivered() {
        var pending = caller.decideAfterStepFailure(request);
        var callback = ArgumentCaptor.forClass(SendHandler.class);
        verify(remote).sendText(anyString(), callback.capture());
        callback.getValue().onResult(new SendResult(new IllegalStateException("Connection closed")));
        assertFalse(pending.join());
    }

    @Test
    void stopsWhenWebSocketCloses() {
        var pending = caller.decideAfterStepFailure(request);
        caller.closeFailureDecisions();
        assertFalse(pending.join());
    }
}
