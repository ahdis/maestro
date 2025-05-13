package net.ihe.gazelle.maestro.technical.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/testRuns/{id}")
@ApplicationScoped
public class TestRunSocket {

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String testSetId) {
        broadcast("TestSet " + testSetId + " is running");
        sessions.put(testSetId, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("id") String testSetId) {
        sessions.remove(testSetId);
        broadcast("TestSet " + testSetId + " is finished");
    }

    @OnError
    public void onError(Session session, @PathParam("id") String testSetId, Throwable throwable) {
        sessions.remove(testSetId);
        broadcast("TestSet " + testSetId + " ended on error: " + throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("id") String testSetId) {
        broadcast(">> " + testSetId + ": " + message);
    }

    private void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }
}
