package net.ihe.gazelle.maestro.technical.websocket;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class TestRunWebsocketTest {

    private static final String WEBSOCKET_URL = Objects.requireNonNull(ConfigProvider.getConfig().getValue("gzl.websocket.url", String.class));
    private static final long TIMEOUT_MS = 500;

    @Test
    void testPingPongMessage() throws Exception {
        WebSocketTestClient client = new WebSocketTestClient();
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        try (Session ignored = container.connectToServer(client, new URI(WEBSOCKET_URL + "/uniqueID"))) {
            String response = client.pollLastMessage(TIMEOUT_MS);
            Assertions.assertNull(response, "No response received");
            client.sendMessage("ping");
            response = client.pollLastMessage(TIMEOUT_MS);
            assertEquals("pong", response);
        }
    }

    @Test
    void testValidWSMessage() throws Exception {
        WebSocketTestClient client = new WebSocketTestClient();
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        String testId = "/test123";
        try (Session ignored = container.connectToServer(
                client, new URI(WEBSOCKET_URL + testId))) {

            WSMessageDTO validMessage = new WSMessageDTO(testId, "test", "Test message");
            client.sendWSMessage(validMessage);

            // TODO add assertions here but no message is expected back here
        }
    }
}