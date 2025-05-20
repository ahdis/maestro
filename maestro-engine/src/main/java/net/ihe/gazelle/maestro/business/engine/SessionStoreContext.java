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
