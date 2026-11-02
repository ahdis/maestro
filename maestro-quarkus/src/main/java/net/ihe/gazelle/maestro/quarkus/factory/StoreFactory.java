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

package net.ihe.gazelle.maestro.quarkus.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;
import net.ihe.gazelle.maestro.engine.business.context.TestSuiteSession;
import technical.dao.SessionStoreCache;


/**
 * Factory class responsible for producing various {@link SessionStore} instances.
 */
@ApplicationScoped
public class StoreFactory {

    /**
     * Produces a {@link SessionStore} for {@link TestSuiteSession} instances.
     *
     * @return a new {@link SessionStoreCache} instance for test suite sessions
     */
    @ApplicationScoped
    @Produces
    @TestSuiteStore
    public SessionStore<TestSuiteSession> getTestSuiteSessionStore() {
        return new SessionStoreCache<>();
    }

    /**
     * Produces a {@link SessionStore} for {@link TestRunSession} instances.
     *
     * @return a new {@link SessionStoreCache} instance for test run sessions
     */
    @ApplicationScoped
    @Produces
    @TestRunStore
    public SessionStore<TestRunSession> getTestRunSessionStore() {
        return new SessionStoreCache<>();

    }

    /**
     * Produces a {@link SessionStore} for {@link MaestroObserver} instances
     * associated with individual test runs.
     *
     * @return a new {@link SessionStoreCache} instance for test run observers
     */
    @ApplicationScoped
    @Produces
    @TestRunObserverStore
    public SessionStore<MaestroObserver> getTestRunObserverStore() {
        return new SessionStoreCache<>();
    }

    /**
     * Produces a {@link SessionStore} for {@link MaestroObserver} instances
     * associated with test suite runs.
     *
     * @return a new {@link SessionStoreCache} instance for test suite run observers
     */
    @ApplicationScoped
    @Produces
    @TestSuiteRunObserverStore
    public SessionStore<MaestroObserver> getTestSuiteRunObserverStore() {
        return new SessionStoreCache<>();
    }

}
