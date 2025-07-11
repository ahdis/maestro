/*
 * Copyright 2025 Kereval.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestro.technical.websocket.client;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import net.ihe.gazelle.maestro.technical.websocket.dto.WSMessageDTO;
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
