/*
 * Copyright 2025 IHE International.
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

package net.ihe.gazelle.maestro.business.engine;

import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;
import net.ihe.gazelle.maestro.api.business.testrun.context.TestRunSessionStore;
import net.ihe.gazelle.maestro.api.business.testrun.context.TestSetSessionStore;

/**
 * <p>
 * Structure used to store context of a session
 * It contains 2 maps :
 * <p>
 *     - <b>SESSION_CONTEXT</b> containing context of a session, the set of TestRunSessionStore
 * {@link TestRunSessionStore}
 * </p>
 * <p>
 *     - <b>TEST_SET_CONTEXT</b> containing context of TestSet, the set of TestSetSessionStore
 * {@link TestSetSessionStore}
 * </p>
 * </p>
 *
 */
public interface SessionStoreContext {

    TestRunSessionStore getExecutionContext(String runId);

    void addExecutionContext(String sessionId, TestRunSessionStore executionContext);

    /**
     * Remove a TestRunSessionStore in SESSION_CONTEXT
     * @param sessionId id of TestRunSessionStore to remove
     */
    void clearExecutionContext(String sessionId);

    TestSetSessionStore getTestSetContexts(String setId);

    void addTestSetContext(String sessionId, TestSetRun testSetRun);

    /**
     * Remove a TestSetSessionStore in SESSION_CONTEXT
     * @param sessionId id of TestSetSessionStore to remove
     */
    void clearTestSetContext(String sessionId);
}
