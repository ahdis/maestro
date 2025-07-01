package net.ihe.gazelle.maestro.technical.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.technical.dto.testrun.TestRunDTO;
import net.ihe.gazelle.maestro.technical.ws.async.WebSocketMaestroService;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.jackson.ObjectMapperBuilder;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/testRuns/{testSetId}")
@ApplicationScoped
public class TestRunSocket {

    private static final Logger LOG = LoggerFactory.getLogger(TestRunSocket.class.getName());
    public static final String INTERNAL_TEST_RUN_ID = "internal";
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final TextSerDes serDes = new JacksonSerDes(new ObjectMapperBuilder().build());
    private final WebSocketMaestroService maestroService;

    @Inject
    public TestRunSocket(WebSocketMaestroService maestroService) {
        this.maestroService = maestroService;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("testSetId") String testSetId) {
        if (!Objects.equals(testSetId, INTERNAL_TEST_RUN_ID))
            sessions.put(testSetId, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("testSetId") String testSetId) {
        sessions.remove(testSetId);
    }

    @OnError
    public void onError(Session session, @PathParam("testSetId") String testSetId, Throwable throwable) {
        sessions.remove(testSetId);
        LOG.error("Test set {} ended on error: {} ", testSetId, throwable.getMessage());
    }

    @OnMessage
    public void onMessage(String message, @PathParam("testSetId") String testSetId) {
        // Ping for test purpose
        if (message.equals("ping")) {
            sendMessageToClient("pong", testSetId);
        } else if (testSetId.equals(INTERNAL_TEST_RUN_ID)) {
            sendMessageToClient(message);
        } else {
            triggerTestRuns(testSetId, message);
        }
    }

    private void triggerTestRuns(String testSetId, String message) {
        LOG.info("Triggering test runs for all clients");
        WSMessageDTO<List<TestRunDTO>> wsMessageDTO = serDes.deserialize(message, WSMessageDTO.class);
        List<TestRunDTO> testRunsDTO =  wsMessageDTO.getContent();
        List<TestRun> testRuns = testRunsDTO.stream().map(TestRunDTO::getDomain).toList();
        maestroService.executeTestSet(testSetId, testRuns);
    }

    private void sendMessageToClient(String message) {
        String testSetId = serDes.deserialize(message, WSMessageDTO.class).getTestRunId();
        LOG.info("Sending message to unique client: {}", testSetId);
        Session session = sessions.get(testSetId);
        if (session != null) {
            session.getAsyncRemote()
                    .sendObject(message, result -> logPotentialException(testSetId, result));
        }
    }

    private void sendMessageToClient(String message, String testSetId) {
        Session session = sessions.get(testSetId);
        session.getAsyncRemote()
                .sendObject(message, result -> logPotentialException(testSetId, result));
    }

    private void broadcast(String message) {
        LOG.info("Broadcasting message: {}", message);
        sessions.values().forEach(s ->
                s.getAsyncRemote().sendObject(message, result -> logPotentialException(s.getId(), result))
        );
    }

    private static void logPotentialException(String testSetId, SendResult result) {
        if (result.getException() != null) {
            LOG.error("Unable to send message to {}: {}", testSetId, result.getException().getMessage());
        }
    }
}
