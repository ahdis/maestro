package net.ihe.gazelle.maestro.technical;

import jakarta.enterprise.context.ApplicationScoped;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;
import net.ihe.gazelle.maestro.api.business.testrun.context.TestRunSessionStore;
import net.ihe.gazelle.maestro.api.business.testrun.context.TestSetSessionStore;
import net.ihe.gazelle.maestro.business.engine.SessionStoreContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The purpose of this class is to being able to access any TestRunSessionStore or TestSetSessionStore from any Maestro
 * Engine instance. The asynchronous execution of Maestro engine involve that the executed StepRun or TestRun can be
 * manage by another Maestro instance than the one which launch its execution.
 * So we want that only with its session id, we can figure out from which TestRunSessionStore or TestSetSessionStore
 * this SteRun/TestRun depend on.
 */
@ApplicationScoped
public class SessionStoreContextImpl implements SessionStoreContext {

    private static final Map<String, TestRunSessionStore> SESSION_CONTEXT = new ConcurrentHashMap<>();
    private static final Map<String, TestSetSessionStore> TEST_SET_CONTEXT = new ConcurrentHashMap<>();

    public SessionStoreContextImpl() {
        // For injection
    }

    @Override
    public TestRunSessionStore getExecutionContext(String runId) {
        return SESSION_CONTEXT.get(runId);
    }

    @Override
    public void addExecutionContext(String sessionId, TestRunSessionStore executionContext) {
        SESSION_CONTEXT.put(sessionId, executionContext);
    }

    @Override
    public void clearExecutionContext(String sessionId) {
        SESSION_CONTEXT.remove(sessionId);
    }

    @Override
    public TestSetSessionStore getTestSetContexts(String setId) {
        return TEST_SET_CONTEXT.get(setId);
    }

    @Override
    public void addTestSetContext(String sessionId, TestSetRun testSetRun) {
        TEST_SET_CONTEXT.put(sessionId, new TestSetSessionStore(sessionId, testSetRun));
    }

    @Override
    public void clearTestSetContext(String sessionId) {
        TEST_SET_CONTEXT.remove(sessionId);
    }

}
