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

package net.ihe.gazelle.maestro.technical.websocket.server;

import jakarta.websocket.*;
import net.ihe.gazelle.maestro.technical.websocket.dto.WSMessageDTO;
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
