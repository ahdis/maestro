package net.ihe.gazelle.business.engine;

import net.ihe.gazelle.context.SessionStore;

import java.util.Map;

public interface SessionStoreContext {

    Map<String, SessionStore> getExecutionContexts();
    void addExecutionContext(String sessionId, SessionStore executionContext);
    void clearExecutionContext(String sessionId);
}
