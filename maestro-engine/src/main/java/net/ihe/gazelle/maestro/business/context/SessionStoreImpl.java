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

package net.ihe.gazelle.maestro.business.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The purpose of this class is to being able to access any TestRunSession or TestSuiteSession from any Maestro
 * Engine instance. The asynchronous execution of Maestro engine involve that the executed StepRun or TestRun can be
 * manage by another Maestro instance than the one which launch its execution.
 * So we want that only with its session id, we can figure out from which TestRunSession or TestSuiteSession
 * this SteRun/TestRun depend on.
 */
public class SessionStoreImpl implements SessionStore {

    private static final Map<String, TestRunSession> TEST_RUN_CONTEXT = new ConcurrentHashMap<>();
    private static final Map<String, TestSuiteSession> TEST_SUITE_CONTEXT = new ConcurrentHashMap<>();

    public SessionStoreImpl() {
        // For injection
    }

    @Override
    public TestRunSession getTestRun(String runId) {
        return TEST_RUN_CONTEXT.get(runId);
    }

    @Override
    public void addTestRun(String runId, TestRunSession testRunSession) {
        TEST_RUN_CONTEXT.put(runId, testRunSession);
    }

    @Override
    public void removeTestRun(String runId) {
        TEST_RUN_CONTEXT.remove(runId);
    }

    @Override
    public TestSuiteSession getTestSuite(String setId) {
        return TEST_SUITE_CONTEXT.get(setId);
    }

    @Override
    public void addTestSuite(String testSuiteId, TestSuiteSession testSuiteSession) {
        TEST_SUITE_CONTEXT.put(testSuiteId, testSuiteSession);
    }

    @Override
    public void removeTestSuite(String testSuiteId) {
        TEST_SUITE_CONTEXT.remove(testSuiteId);
    }

}
