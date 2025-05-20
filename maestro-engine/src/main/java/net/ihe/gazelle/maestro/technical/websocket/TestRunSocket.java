package net.ihe.gazelle.maestro.technical.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/testRuns/{id}")
@ApplicationScoped
public class TestRunSocket {

    private static final Logger LOG = LoggerFactory.getLogger(TestRunSocket.class.getName());
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String testSetId) {
        broadcast(String.format("TestSet %s is running", testSetId));
        sessions.put(testSetId, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("id") String testSetId) {
        sessions.remove(testSetId);
        broadcast(String.format("TestSet %s is finished", testSetId));
    }

    @OnError
    public void onError(Session session, @PathParam("id") String testSetId, Throwable throwable) {
        sessions.remove(testSetId);
        broadcast(String.format("TestSet %s ended on error: %s", testSetId, throwable.getMessage()));
    }

    @OnMessage
    public void onMessage(String message, @PathParam("id") String testSetId) {
        broadcast(">> " + testSetId + ": " + message);
    }

    private void broadcast(String message) {
        sessions.values().forEach(s ->
                s.getAsyncRemote().sendObject(message, result -> {
                    if (result.getException() != null) {
                        LOG.error("Unable to send message: {}", result.getException().getMessage());
                    }
                })
        );
    }
}
