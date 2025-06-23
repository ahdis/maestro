package net.ihe.gazelle.maestro.technical.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.jackson.ObjectMapperBuilder;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/testRuns/{id}")
@ApplicationScoped
public class TestRunSocket {

    private static final Logger LOG = LoggerFactory.getLogger(TestRunSocket.class.getName());
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final TextSerDes serDes = new JacksonSerDes(new ObjectMapperBuilder().build());

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String testSetId) {
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
        if (testSetId.equals("internal")) {
            sendToUniqueClient(message);
        } else {
            broadcast(message);
        }
    }

    private void broadcast(String message) {
        LOG.info("Broadcasting message: {}", message);
        sessions.values().forEach(s ->
                s.getAsyncRemote().sendObject(message, result -> {
                    if (result.getException() != null) {
                        LOG.error("Unable to send message: {}", result.getException().getMessage());
                    }
                })
        );
    }

    private void sendToUniqueClient(String message) {
        String testSetId = serDes.deserialize(message, WSMessageDTO.class).getTestRunId();
        LOG.info("Sending message to unique client: {}", testSetId);
        sessions.get(testSetId).getAsyncRemote().sendObject(message, result -> {
            if (result.getException() != null) {
                LOG.error("Unable to send message to {}: {}", testSetId, result.getException().getMessage());
            }
        });
    }
}
