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

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;
import net.ihe.gazelle.maestro.engine.business.context.TestSuiteSession;
import net.ihe.gazelle.maestro.engine.technical.dao.SessionStoreCache;
import net.ihe.gazelle.maestro.quarkus.dao.TestRunSessionDAO;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Factory class responsible for producing various {@link SessionStore} instances.
 */
@ApplicationScoped
public class StoreFactory {

    private final AgroalDataSource dataSource;
    private final TestRunSessionDAO testRunSessionDAO;

    /**
     * Constructs a new instance of the {@code StoreFactory} class.
     *
     * @param dataSource the {@link AgroalDataSource} used for managing database connections
     * @param testRunSessionDAO the {@link EntityManager} used for managing database transactions
     */
    @Inject
    public StoreFactory(AgroalDataSource dataSource, TestRunSessionDAO testRunSessionDAO) {
        this.dataSource = dataSource;
        this.testRunSessionDAO = testRunSessionDAO;
    }

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
        if (isDatabaseAvailable()) {
            return testRunSessionDAO;
        } else {
            return new SessionStoreCache<>();
        }
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

    private boolean isDatabaseAvailable() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (SQLException | RuntimeException _) {
            return false;
        }
    }
}
