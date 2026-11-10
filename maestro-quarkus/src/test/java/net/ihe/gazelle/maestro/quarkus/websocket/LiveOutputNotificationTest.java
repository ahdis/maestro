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

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

class LiveOutputNotificationTest {
    @ParameterizedTest
    @CsvSource({"false,false,false", "false,true,false", "true,false,false", "true,true,true"})
    void requiresBothDeploymentEnablementAndClientOptIn(
            boolean enabled, boolean optedIn, boolean expected) {
        Session session = mock(Session.class);
        RemoteEndpoint.Async remote = mock(RemoteEndpoint.Async.class);
        when(session.getId()).thenReturn("socket");
        when(session.getAsyncRemote()).thenReturn(remote);
        when(session.getRequestParameterMap())
                .thenReturn(optedIn ? Map.of("liveOutputs", List.of("true")) : Map.of());
        WebSocketSessionService sessions = mock(WebSocketSessionService.class);
        var controller = new TestStreamSocketController(null, null, null, sessions, null);
        controller.liveOutputsEnabled = enabled;
        controller.onOpen(session);
        var caller = ArgumentCaptor.forClass(WebSocketSession.class);
        verify(sessions).addSession(eq("socket"), caller.capture());
        var report =
                new StepRunReport()
                        .setStepName("Validate")
                        .addOutput(
                                new ByteArrayItemProperty("report", null)
                                        .setReference("/items/report-1"));
        caller.getValue()
                .onOutputsRecorded(
                        new StepRunFinished("session", "test", "Validate", null, report));
        if (expected) {
            verify(remote).sendText(anyString());
        } else {
            verifyNoInteractions(remote);
        }
    }

    @Test
    void publishesOnlyAvailableOutputsWithoutRepeatingStepCompletion() throws Exception {
        Session session = mock(Session.class);
        RemoteEndpoint.Async remote = mock(RemoteEndpoint.Async.class);
        when(session.getAsyncRemote()).thenReturn(remote);
        var output =
                new ByteArrayItemProperty("VALIDATION_REPORT", null)
                        .setReference("/items/report-1")
                        .setItemType("VALIDATION_REPORT");
        var report = new StepRunReport().setStepName("Validate").addOutput(output);
        new WebSocketSession(session, true)
                .onOutputsRecorded(
                        new StepRunFinished("session", "test", "Validate", null, report));
        ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
        verify(remote).sendText(json.capture());
        var payload = new ObjectMapper().readTree(json.getValue());
        assertEquals("STEP_OUTPUTS_AVAILABLE", payload.path("type").asText());
        assertEquals("Validate", payload.path("stepName").asText());
        assertEquals("/items/report-1", payload.path("outputs").get(0).path("reference").asText());
        assertFalse(payload.has("result"));
        verify(session, never()).close();
    }

    @Test
    void keepsNewNotificationsDisabledForExistingDeployments() {
        Session session = mock(Session.class);
        new WebSocketSession(session).onOutputsRecorded(new StepRunFinished());
        verifyNoInteractions(session);
    }
}
