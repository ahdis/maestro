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

import jakarta.persistence.*;
import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;
import net.ihe.gazelle.maestro.quarkus.dto.TestRunSessionDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;

import java.time.Instant;

@Entity
@Table(name = "test_run_session")
public class TestRunSessionEntity {

    @Id
    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "expirationDateTime")
    private Instant expirationDateTime;

    @Lob
    @Column(name = "session", columnDefinition = "TEXT")
    private String session;

    /**
     * Default constructor
     */
    public TestRunSessionEntity() {
        // for JPA
    }

    public TestRunSessionEntity(String sessionId, Instant expirationDateTime, TestRunSession testRunSession) {
       this.sessionId = sessionId;
       this.expirationDateTime = expirationDateTime;
       this.session = new JacksonSerDes().serializeAsString(new TestRunSessionDTO(testRunSession));
    }

    public String getSessionId() {
        return sessionId;
    }

    public TestRunSessionEntity setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public Instant getExpirationDateTime() {
        return expirationDateTime;
    }

    public TestRunSessionEntity setExpirationDateTime(Instant expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
        return this;
    }

    public TestRunSession getSession() {
        return toDomain();
    }

    public TestRunSessionEntity setSession(TestRunSession session) {
        this.session = new JacksonSerDes().serializeAsString(new TestRunSessionDTO(session));
        return this;
    }

    public TestRunSession toDomain() {
        return new JacksonSerDes().deserialize(session, TestRunSessionDTO.class).getBusinessObject();
    }
}
