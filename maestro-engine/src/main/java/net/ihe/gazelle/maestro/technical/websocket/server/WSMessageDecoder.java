package net.ihe.gazelle.maestro.technical.websocket.server;

import jakarta.websocket.*;
import net.ihe.gazelle.maestro.technical.websocket.WSMessageDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.jackson.ObjectMapperBuilder;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;

public class WSMessageDecoder implements Decoder.Text<WSMessageDTO> {

    private final TextSerDes serDes = new JacksonSerDes(new ObjectMapperBuilder().build());


    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    @Override
    public WSMessageDTO decode(String s) {
        return serDes.deserialize(s, WSMessageDTO.class);
    }

    @Override
    public boolean willDecode(String s) {
        try {
            serDes.deserialize(s, WSMessageDTO.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
