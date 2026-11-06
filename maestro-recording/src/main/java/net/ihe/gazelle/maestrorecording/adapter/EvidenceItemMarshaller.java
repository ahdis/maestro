/*
 * Copyright 2026 IHE International.
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

package net.ihe.gazelle.maestrorecording.adapter;

import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.technical.dto.property.PropertyDTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.marshalling.TextMarshaller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

final class EvidenceItemMarshaller implements TextMarshaller<List<Property>> {
    private final JacksonSerDes serDes = new JacksonSerDes();

    @Override
    public String marshallAsString(List<Property> evidence) {
        return serDes.serializeAsString(
                Map.of(
                        "evidences",
                        evidence.stream()
                                .map(SubTypingDTO::fromBusinessObject)
                                .map(PropertyDTO.class::cast)
                                .toList()));
    }

    @Override
    public byte[] marshall(List<Property> evidence) {
        return marshallAsString(evidence).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public List<Property> unmarshall(String payload) {
        throw new UnsupportedOperationException("Evidence recording is write-only");
    }

    @Override
    public List<Property> unmarshall(byte[] payload) {
        throw new UnsupportedOperationException("Evidence recording is write-only");
    }
}
