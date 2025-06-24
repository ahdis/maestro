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
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/testRuns/{id}")
@ApplicationScoped
public class TestRunSocket {

    private static final Logger LOG = LoggerFactory.getLogger(TestRunSocket.class.getName());
    public static final String INTERNAL_TEST_RUN_ID = "internal";
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final TextSerDes serDes = new JacksonSerDes(new ObjectMapperBuilder().build());

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String testSetId) {
        if (!Objects.equals(testSetId, INTERNAL_TEST_RUN_ID))
            sessions.put(testSetId, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("id") String testSetId) {
        sessions.remove(testSetId);
    }

    @OnError
    public void onError(Session session, @PathParam("id") String testSetId, Throwable throwable) {
        sessions.remove(testSetId);
        LOG.error("Test set {} ended on error: {} ", testSetId, throwable.getMessage());
    }

    @OnMessage
    public void onMessage(String message, @PathParam("id") String testSetId) {
        // Ping for test purpose
        if (message.equals("ping")) {
            sendMessageToClient("pong", testSetId);
        } else if (testSetId.equals(INTERNAL_TEST_RUN_ID)) {
            sendMessageToClient(message);
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

    private void sendMessageToClient(String message) {
        String testSetId = serDes.deserialize(message, WSMessageDTO.class).getTestRunId();
        LOG.info("Sending message to unique client: {}", testSetId);
        sessions.get(testSetId).getAsyncRemote().sendObject(message, result -> {
            if (result.getException() != null) {
                LOG.error("Unable to send message to {}: {}", testSetId, result.getException().getMessage());
            }
        });
    }

    private void sendMessageToClient(String message, String testSetId) {
        sessions.get(testSetId).getAsyncRemote().sendObject(message, result -> {
            if (result.getException() != null) {
                LOG.error("Unable to send message to {}: {}", testSetId, result.getException().getMessage());
            }
        });
    }
}
