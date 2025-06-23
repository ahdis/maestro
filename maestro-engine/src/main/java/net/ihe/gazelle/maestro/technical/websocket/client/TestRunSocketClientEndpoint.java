package net.ihe.gazelle.maestro.technical.websocket.client;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import net.ihe.gazelle.maestro.technical.websocket.WSMessageDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.jackson.ObjectMapperBuilder;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;

@ClientEndpoint
public class TestRunSocketClientEndpoint {

    private final TextSerDes serDes = new JacksonSerDes(new ObjectMapperBuilder().build());
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        // Do nothing here, as this client is not designed to handle incoming messages directly.
    }

    public void sendMessage(String message) {
        session.getAsyncRemote().sendText(message);
    }

    public void sendWSMessage(WSMessageDTO message) {
        try {
            String jsonMessage = serDes.serializeAsString(message);
            session.getAsyncRemote().sendText(jsonMessage);
        } catch (Exception e) {
            // Handle serialization error
        }
    }
}
