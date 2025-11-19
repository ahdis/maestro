/*
 * Copyright 2025 Kereval.
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

package net.ihe.gazelle.itb.gateway.technical.dao;

import net.ihe.gazelle.itb.gateway.business.ItbSessionStore;
import net.ihe.gazelle.itb.gateway.business.reporting.ItbReporting;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ItbSessionStoreImpl implements ItbSessionStore {


    private static final Map<String, CompletableFuture<ItbReporting>> REPORT_FUTURES = new HashMap<>();

    public ItbSessionStoreImpl() {
        // Private constructor to prevent instantiation
    }

    @Override
    public CompletableFuture<ItbReporting> get(String sessionId) {
        return REPORT_FUTURES.get(sessionId);
    }

    @Override
    public void add(String sessionId, CompletableFuture<ItbReporting> future) {
        REPORT_FUTURES.put(sessionId, future);
    }

    @Override
    public void remove(String sessionId) {
        REPORT_FUTURES.remove(sessionId);
    }

}
