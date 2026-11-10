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

package net.ihe.gazelle.maestro.quarkus;

import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;
import net.ihe.gazelle.maestro.engine.business.context.TestSuiteSession;
import net.ihe.gazelle.maestro.quarkus.factory.TestRunObserverStore;
import net.ihe.gazelle.maestro.quarkus.factory.TestRunStore;
import net.ihe.gazelle.maestro.quarkus.factory.TestSuiteRunObserverStore;
import net.ihe.gazelle.maestro.quarkus.factory.TestSuiteStore;
import net.ihe.gazelle.maestro.quarkus.websocket.WebSocketSessionService;

public class MaestroSessionScheduler {

    private final SessionStore<TestRunSession> sessionStore;
    private final SessionStore<TestSuiteSession> testSuiteSessionStore;
    private final SessionStore<MaestroObserver> testRunObserverStore;
    private final SessionStore<MaestroObserver> testSuiteObserverStore;
    private final WebSocketSessionService webSocketSessionService;

    @Inject
    public MaestroSessionScheduler(
            @TestRunStore SessionStore<TestRunSession> sessionStore,
            @TestSuiteStore SessionStore<TestSuiteSession> testSuiteSessionStore,
            @TestRunObserverStore SessionStore<MaestroObserver> testRunObserverStore,
            @TestSuiteRunObserverStore SessionStore<MaestroObserver> testSuiteObserverStore,
            WebSocketSessionService webSocketSessionService
    ) {
        this.sessionStore = sessionStore;
        this.testSuiteSessionStore = testSuiteSessionStore;
        this.testRunObserverStore = testRunObserverStore;
        this.testSuiteObserverStore = testSuiteObserverStore;
        this.webSocketSessionService = webSocketSessionService;
    }

    void cleanupExpiredSessions() {
        sessionStore.removeExpiredSessions();
        testSuiteSessionStore.removeExpiredSessions();
        testRunObserverStore.removeExpiredSessions();
        testSuiteObserverStore.removeExpiredSessions();
        webSocketSessionService.removeExpiredSessions();
    }
}
