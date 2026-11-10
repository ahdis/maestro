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

package net.ihe.gazelle.maestro.engine.technical.dao;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * The purpose of this class is to being able to access any TestRunSession or TestSuiteSession from any Maestro
 * Engine instance. The asynchronous execution of the Maestro engine involves that the executed StepRun or TestRun can be
 * managed by another Maestro instance than the one that launches its execution.
 * So we want that only with its session id, we can figure out from which TestRunSession or TestSuiteSession
 * this SteRun/TestRun depends on.
 *
 * @param <T> The type of object cached in the store
 */
public class SessionStoreCache<T> implements SessionStore<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionStoreCache.class);

    private final Cache<String, CacheEntry<T>> sessions;

    /**
     * Creates a new {@code SessionStoreImpl} with an internal cache that manages session expirations.
     */
    public SessionStoreCache() {
        sessions = Caffeine.newBuilder()
                .expireAfter(new SessionExpiry())
                .build();
    }

    @Override
    public synchronized void putSession(String id, T session, Instant expirationDateTime) {
        sessions.put(id, new CacheEntry<>(session, Duration.between(Instant.now(), expirationDateTime)));
    }

    @Override
    public synchronized boolean hasSession(String id) {
        return sessions.getIfPresent(id) != null;
    }

    @Override
    public synchronized T getSession(String id) {
        if (id == null) {
            return null;
        }
        CacheEntry<T> entry = sessions.getIfPresent(id);
        return entry != null ? entry.value : null;
    }

    @Override
    public List<T> getAllSessions() {
        return sessions.asMap().values().stream()
                .map(entry -> entry.value)
                .toList();
    }

    @Override
    public synchronized void removeSession(String id) {
        sessions.invalidate(id);
    }

    @Override
    public synchronized void removeExpiredSessions() {
        // Nothing to remove
    }

    private final class SessionExpiry implements Expiry<String, CacheEntry<T>> {

        @Override
        public long expireAfterCreate(String key, CacheEntry<T> entry, long now) {
            return entry.duration.toNanos();
        }

        @Override
        public long expireAfterUpdate(String key, CacheEntry<T> entry, long now, long currentDuration) {
            return currentDuration;
        }

        @Override
        public long expireAfterRead(String key, CacheEntry<T> entry, long now, long currentDuration) {
            return currentDuration;
        }
    }

    private record CacheEntry<T>(T value, Duration duration) {}
}
