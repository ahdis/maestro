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

package net.ihe.gazelle.maestro.quarkus.dao;

import com.mongodb.client.*;
import com.mongodb.client.model.ReplaceOptions;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import net.ihe.gazelle.maestro.api.business.message.PutEvidence;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.GradingMethod;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;
import net.ihe.gazelle.maestro.quarkus.dto.TestRunSessionDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionPersistenceTest {
    private TestRunSession session() {
        var test = new net.ihe.gazelle.maestro.api.business.test.Test().setId("test")
                .setName("Test").setGradingMethod(GradingMethod.AUTOMATED)
                .setSteps(List.of(new Step().setName("first").setType("VALIDATION"),
                        new Step().setName("second").setType("VALIDATION")));
        return new TestRunSession(new TestRun().setTest(test).setAccessControlList(new AccessControlList().setPublic(false)).setInputs(List.of(new StringProperty("patient", "123"))))
                .setTestSessionId("qualification").setStepCursor(0)
                .setStepReports(List.of(new StepRunReport().setStepName("first").setType("VALIDATION")))
                .setEvidences(List.of(new PutEvidence().setSessionId("execution").setRunId(0).setStepName("first").setContent(new StringProperty("note", "reviewed"))));
    }

    private void assertResumable(TestRunSession restored) {
        assertEquals("qualification", restored.getTestSessionId());
        assertEquals("test", restored.getTestRun().getTest().getId());
        assertEquals("123", restored.getContext().getFirst().getValue());
        assertEquals("first", restored.currentStep().getName());
        assertEquals("first", restored.getStepReports().getFirst().getStepName());
        assertEquals("reviewed", restored.getEvidences().getFirst().getContent().getValue());
        assertTrue(restored.hasNextStep());
        assertEquals("second", restored.nextStep().getName());
        assertFalse(restored.hasNextStep());
    }

    @Test
    void sessionJsonAndEntityPreserveResumePositionContextAndEvidence() {
        var serializer = new JacksonSerDes();
        var original = session();
        String json = serializer.serializeAsString(new TestRunSessionDTO(original));
        assertResumable(serializer.deserialize(json, TestRunSessionDTO.class).getBusinessObject());
        var entity = new TestRunSessionEntity("execution", original);
        assertEquals("execution", entity.getSessionId());
        assertResumable(entity.toDomain());
        entity.setSessionId("copy").setSession(session());
        assertEquals("copy", entity.getSessionId());
        assertResumable(entity.getSession());
        assertNotNull(new TestRunSessionEntity());
    }

    @Test
    void relationalStoreCreatesUpdatesReadsAndDeletesSessions() {
        EntityManager manager = mock(EntityManager.class);
        var store = new TestRunSessionDAO(manager);
        var original = session();
        assertFalse(store.hasSession("execution"));
        assertNull(store.getSession("execution"));
        store.removeSession("execution");
        verify(manager, never()).remove(any());
        store.putSession("execution", original, 1000);
        var capture = ArgumentCaptor.forClass(TestRunSessionEntity.class);
        verify(manager).persist(capture.capture());
        var entity = capture.getValue();
        when(manager.find(TestRunSessionEntity.class, "execution")).thenReturn(entity);
        assertTrue(store.hasSession("execution"));
        assertResumable(store.getSession("execution"));
        store.putSession("execution", session().setTestSessionId("updated"), 1000);
        verify(manager).merge(entity);
        assertEquals("updated", entity.toDomain().getTestSessionId());
        TypedQuery<TestRunSessionEntity> query = mock(TypedQuery.class);
        when(manager.createQuery(anyString(), eq(TestRunSessionEntity.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(entity));
        assertEquals("updated", store.getAllSessions().getFirst().getTestSessionId());
        store.removeSession("execution");
        verify(manager).remove(entity);
        verify(manager, times(3)).flush();
    }

    @Test
    void mongoStoreUpsertsAndRestoresSessionsAndRejectsMissingPayload() {
        MongoClient client = mock(MongoClient.class);
        MongoDatabase database = mock(MongoDatabase.class);
        MongoCollection<Document> collection = mock(MongoCollection.class);
        FindIterable<Document> found = mock(FindIterable.class);
        when(client.getDatabase("maestro")).thenReturn(database);
        when(database.getCollection("test_run_session")).thenReturn(collection);
        when(collection.find(any(Bson.class))).thenReturn(found);
        when(collection.find()).thenReturn(found);
        when(found.projection(any(Bson.class))).thenReturn(found);
        when(found.limit(1)).thenReturn(found);
        var store = new MongoTestRunSessionStore(client, "maestro");
        assertNull(store.getSession("missing"));
        assertFalse(store.hasSession("missing"));
        store.putSession("execution", session(), 1000);
        var document = ArgumentCaptor.forClass(Document.class);
        var options = ArgumentCaptor.forClass(ReplaceOptions.class);
        verify(collection).replaceOne(any(Bson.class), document.capture(), options.capture());
        assertTrue(options.getValue().isUpsert());
        assertEquals("execution", document.getValue().getString("_id"));
        when(found.first()).thenReturn(document.getValue());
        assertTrue(store.hasSession("execution"));
        assertResumable(store.getSession("execution"));
        MongoCursor<Document> cursor = mock(MongoCursor.class);
        when(found.iterator()).thenReturn(cursor);
        when(cursor.hasNext()).thenReturn(true, false);
        when(cursor.next()).thenReturn(document.getValue());
        assertResumable(store.getAllSessions().getFirst());
        store.removeSession("execution");
        verify(collection).deleteOne(any(Bson.class));
        when(found.first()).thenReturn(new Document("_id", "broken"));
        assertThrows(IllegalStateException.class, () -> store.getSession("broken"));
        assertThrows(NullPointerException.class, () -> store.getSession(null));
        assertThrows(NullPointerException.class, () -> store.hasSession(null));
        assertThrows(NullPointerException.class, () -> store.removeSession(null));
        var validSession = session();
        assertThrows(NullPointerException.class, () -> store.putSession(null, validSession, 0));
        assertThrows(NullPointerException.class, () -> store.putSession("id", null, 0));
    }
}
