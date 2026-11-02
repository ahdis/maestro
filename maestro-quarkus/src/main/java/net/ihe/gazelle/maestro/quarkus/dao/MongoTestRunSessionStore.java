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

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;

import jakarta.enterprise.context.ApplicationScoped;
import net.ihe.gazelle.maestro.quarkus.dto.TestRunSessionDTO;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.include;

//@ApplicationScoped
public class MongoTestRunSessionStore implements SessionStore<TestRunSession> {

    private static final String COLLECTION_NAME = "test_run_session";

    private final MongoCollection<Document> collection;
    private final JacksonSerDes serDes;

    //@Inject
    public MongoTestRunSessionStore(
            MongoClient mongoClient,
            @ConfigProperty(
                    name = "maestro.mongodb.database",
                    defaultValue = "maestro"
            ) String databaseName) {

        this.collection = mongoClient
                .getDatabase(databaseName)
                .getCollection(COLLECTION_NAME);

        this.serDes = new JacksonSerDes();
    }

    /**
     * Persists a session in MongoDB.
     * If a document with the given session ID already exists,
     * its payload is replaced.
     *
     * @param sessionId the unique session identifier
     * @param session the session to persist
     * @param timeout the session timeout, currently unused
     */
    @Override
    public void putSession(String sessionId, TestRunSession session, long timeout) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        Objects.requireNonNull(session, "session must not be null");
        Document document = toMongoDocument(sessionId, session);
        collection.replaceOne(
                eq("_id", sessionId),
                document,
                new ReplaceOptions().upsert(true)
        );
    }

    /**
     * Checks whether a session exists.
     *
     * @param sessionId the unique session identifier
     * @return true if the session exists
     */
    @Override
    public boolean hasSession(String sessionId) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        return collection
                .find(eq("_id", sessionId))
                .projection(include("_id"))
                .limit(1)
                .first() != null;
    }

    /**
     * Removes a session from MongoDB.
     * Removing a session that does not exist has no effect.
     *
     * @param sessionId the unique session identifier
     */
    @Override
    public void removeSession(String sessionId) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        collection.deleteOne(eq("_id", sessionId));
    }

    /**
     * Retrieves a session by its identifier.
     *
     * @param sessionId the unique session identifier
     * @return the deserialized session, or null if it does not exist
     */
    @Override
    public TestRunSession getSession(String sessionId) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        Document document = collection
                .find(eq("_id", sessionId))
                .first();
        if (document == null) {
            return null;
        }
        return toDomain(document);
    }

    /**
     * Retrieves all persisted sessions.
     *
     * @return all sessions stored in the collection
     */
    @Override
    public List<TestRunSession> getAllSessions() {
        List<TestRunSession> sessions = new ArrayList<>();
        for (Document document : collection.find()) {
            sessions.add(toDomain(document));
        }
        return sessions;
    }

    /**
     * Converts a domain session into a MongoDB document.
     * The DTO is serialized to JSON using the existing serialization
     * mechanism. Document.parse converts the JSON payload to BSON.
     */
    private Document toMongoDocument(String sessionId, TestRunSession session) {
        String json = serDes.serializeAsString(new TestRunSessionDTO(session));
        Document payload = Document.parse(json);
        return new Document("_id", sessionId).append("payload", payload);
    }

    /**
     * Converts a MongoDB document back into the domain model.
     */
    private TestRunSession toDomain(Document document) {
        Document payload = document.get("payload", Document.class);
        if (payload == null) {
            throw new IllegalStateException("Missing payload for session " + document.getString("_id"));
        }
        return serDes.deserialize(payload.toJson(), TestRunSessionDTO.class).getBusinessObject();
    }
}
