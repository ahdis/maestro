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

package net.ihe.gazelle.maestro.quarkus.dto;

import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;
import net.ihe.gazelle.maestro.quarkus.utils.ObjectFactory;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestRunSessionDTOTest {

    @Test
    void testExpirationTimestamp() {
        TextSerDes serDes = new JacksonSerDes();
        TestRunSession testRunSession = new TestRunSession()
                .setTestRun(ObjectFactory.createTestRun())
                .setStepCursor(0)
                .setPromptOnStepFailure(true)
                .setExpirationTimestamp(Instant.now().plusMillis(100));
        String message = serDes.serializeAsString(new TestRunSessionDTO(testRunSession));
        TestRunSession serialized = serDes.deserialize(message, TestRunSessionDTO.class).getBusinessObject();
        assertEquals(testRunSession.isPromptOnStepFailure(), serialized.isPromptOnStepFailure());
        assertEquals(testRunSession.getExpirationTimestamp(), serialized.getExpirationTimestamp());
    }
}
