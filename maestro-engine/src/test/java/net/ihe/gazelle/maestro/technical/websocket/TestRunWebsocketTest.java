package net.ihe.gazelle.maestro.technical.websocket;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.net.URI;

@QuarkusTest
class TestRunWebsocketTest {

    private static final String WS_URI = "ws://localhost:8151/maestro/testRuns/";
    private static final long TIMEOUT_MS = 500;

    @Test
    void testInvalidJSONMessage() throws Exception {
        WebSocketTestClient client = new WebSocketTestClient();
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        try (Session session = container.connectToServer(client, new URI(WS_URI + "uniqueID"))) {
            String response = client.pollLastMessage(TIMEOUT_MS);
            Assertions.assertNotNull(response, "Aucune réponse reçue");
            client.sendMessage("Invalid JSON message");
            response = client.pollLastMessage(TIMEOUT_MS);
            Assertions.assertNull(response, "Aucune réponse reçue");
        }
    }

    @Test
    void testValidWSMessage() throws Exception {
        WebSocketTestClient client = new WebSocketTestClient();
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        String testId = "test123";
        try (Session session = container.connectToServer(
                client, new URI(WS_URI + testId))) {

            String connectionMessage = client.pollLastMessage(TIMEOUT_MS);
            Assertions.assertNotNull(connectionMessage);
            Assertions.assertTrue(connectionMessage.contains(testId));

            WSMessageDTO validMessage = new WSMessageDTO(testId, "test", "Test message");
            client.sendWSMessage(validMessage);
        }
    }
}