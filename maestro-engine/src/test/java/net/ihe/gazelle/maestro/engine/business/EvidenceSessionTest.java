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

package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.message.DeleteEvidence;
import net.ihe.gazelle.maestro.api.business.message.PutEvidence;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EvidenceSessionTest {
    @Test
    void sameNamedEvidenceReplacesWithinItsRunAndStepAndDeletesCleanly() {
        TestRunSession session = new TestRunSession();
        session.addEvidence(evidence(1, "first", "old"));
        session.addEvidence(evidence(1, "second", "other step"));
        session.addEvidence(evidence(2, "first", "other run"));
        session.addEvidence(evidence(1, "first", "new"));
        assertEquals(3, session.getEvidences().size());
        assertEquals("new", session.getEvidences().getLast().getContent().getValue());
        session.removeEvidence(
                new DeleteEvidence().setRunId(1).setStepName("first").setEvidenceName("proof"));
        assertEquals(2, session.getEvidences().size());
        session.closeEvidence();
        PutEvidence late = evidence(1, "first", "late");
        DeleteEvidence deletion =
                new DeleteEvidence().setRunId(1).setStepName("second").setEvidenceName("proof");
        assertThrows(TestReportRecordingException.class, () -> session.addEvidence(late));
        assertThrows(TestReportRecordingException.class, () -> session.removeEvidence(deletion));
    }

    @Test
    @SuppressWarnings("unchecked")
    void recordsDetachedValueBeforeAcceptingAndPublishingIt() {
        SessionStore<TestRunSession> store = mock(SessionStore.class);
        MaestroFacade facade = mock(MaestroFacade.class);
        MaestroObserver observer = mock(MaestroObserver.class);
        TestRunSession session = new TestRunSession();
        when(store.getSession("session")).thenReturn(session);
        when(facade.evidenceObserver("session")).thenReturn(Optional.of(observer));
        ByteArrayProperty raw = new ByteArrayProperty("proof", new byte[] {1});
        ByteArrayItemProperty reference =
                new ByteArrayItemProperty(raw).setReference("/items/owner/attachments/file");
        reference.setValue(null);
        when(observer.recordEvidence(any())).thenReturn(reference);
        PutEvidence incoming = evidence(1, "step", "unused").setContent(raw);
        new MaestroSessionServiceImpl(store, facade).putEvidence(incoming);
        assertSame(raw, incoming.getContent());
        assertArrayEquals(new byte[] {1}, raw.getValue());
        assertSame(reference, session.getEvidences().getFirst().getContent());
        verify(observer).onEvidenceRecorded(session.getEvidences().getFirst());
    }

    @Test
    @SuppressWarnings("unchecked")
    void failedUploadDoesNotReplacePreviouslyAcceptedEvidence() {
        SessionStore<TestRunSession> store = mock(SessionStore.class);
        MaestroFacade facade = mock(MaestroFacade.class);
        MaestroObserver observer = mock(MaestroObserver.class);
        TestRunSession session = new TestRunSession().addEvidence(evidence(1, "step", "previous"));
        when(store.getSession("session")).thenReturn(session);
        when(facade.evidenceObserver("session")).thenReturn(Optional.of(observer));
        when(observer.recordEvidence(any()))
                .thenThrow(new TestReportRecordingException("Storage failed"));
        var service = new MaestroSessionServiceImpl(store, facade);
        PutEvidence replacement = evidence(1, "step", "replacement");
        assertThrows(TestReportRecordingException.class, () -> service.putEvidence(replacement));
        assertEquals("previous", session.getEvidences().getFirst().getContent().getValue());
        verify(observer, never()).onEvidenceRecorded(any());
    }

    private PutEvidence evidence(int run, String step, String value) {
        return new PutEvidence()
                .setSessionId("session")
                .setRunId(run)
                .setStepName(step)
                .setContent(new StringProperty("proof", value));
    }
}
