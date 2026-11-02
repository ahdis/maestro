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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;

import java.util.List;

@ApplicationScoped
public class TestRunSessionDAO implements SessionStore<TestRunSession> {

    private final EntityManager entityManager;

    @Inject
    public TestRunSessionDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void putSession(String sessionId, TestRunSession session, long timeout) {
        TestRunSessionEntity testRunSessionEntity = entityManager.find(TestRunSessionEntity.class, sessionId);
        if (testRunSessionEntity == null) {
            entityManager.persist(new TestRunSessionEntity(sessionId, session));
        } else {
            testRunSessionEntity.setSession(session);
            entityManager.merge(testRunSessionEntity);
        }
        entityManager.flush();
    }

    @Override
    public boolean hasSession(String sessionId) {
        return entityManager.find(TestRunSessionEntity.class, sessionId) != null;
    }

    @Override
    @Transactional
    public void removeSession(String sessionId) {
        TestRunSessionEntity testRunSessionEntity = entityManager.find(TestRunSessionEntity.class, sessionId);
        if (testRunSessionEntity != null) {
            entityManager.remove(testRunSessionEntity);
            entityManager.flush();
        }
    }

    @Override
    public TestRunSession getSession(String sessionId) {
        TestRunSessionEntity testRunSessionEntity = entityManager.find(TestRunSessionEntity.class, sessionId);
        if (testRunSessionEntity != null) {
            return testRunSessionEntity.toDomain();
        }
        return null;
    }

    @Override
    public List<TestRunSession> getAllSessions() {
        return entityManager
                .createQuery("SELECT t FROM TestRunSessionEntity t", TestRunSessionEntity.class)
                .getResultList()
                .stream()
                .map(TestRunSessionEntity::toDomain)
                .toList();
    }
}
