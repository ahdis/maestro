package net.ihe.gazelle.technical;

import jakarta.enterprise.context.ApplicationScoped;
import net.ihe.gazelle.business.engine.SessionStoreContext;
import net.ihe.gazelle.context.SessionStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SessionStoreContextImpl implements SessionStoreContext {

    private static final Map<String, SessionStore> SESSION_CONTEXT = new ConcurrentHashMap<>();

    public SessionStoreContextImpl() {
        // For injection
    }

    @Override
    public Map<String, SessionStore> getExecutionContexts() {
        return SESSION_CONTEXT;
    }

    @Override
    public void addExecutionContext(String sessionId, SessionStore executionContext) {
        SESSION_CONTEXT.put(sessionId, executionContext);
    }

    @Override
    public void clearExecutionContext(String sessionId) {
        SESSION_CONTEXT.remove(sessionId);
    }
}
