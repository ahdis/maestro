/*
 * Copyright 2025-2026 IHE International.
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

package net.ihe.gazelle.maestro.api.technical.dto;

import com.fasterxml.jackson.databind.JsonNode;
import net.ihe.gazelle.maestro.api.technical.dto.message.StartTestRunDTO;
import net.ihe.gazelle.maestro.api.business.message.StepFailureDecision;
import net.ihe.gazelle.maestro.api.business.message.StepFailureDecisionRequested;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.technical.dto.message.MessageDTO;
import net.ihe.gazelle.maestro.api.technical.dto.message.StepFailureDecisionDTO;
import net.ihe.gazelle.maestro.api.technical.dto.message.StepFailureDecisionRequestedDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class StepFailureDecisionDTOTest {
    private final JacksonSerDes serDes = new JacksonSerDes();

    @Test
    void promptsOnlyWhenTheClientOptsIn() {
        StartTestRunDTO run = new StartTestRunDTO();
        assertEquals(false, run.isPromptOnStepFailure());
        run.setPromptOnStepFailure(true);
        assertEquals(true, run.getBusinessObject().isPromptOnStepFailure());
    }

    @Test
    void roundTripsRequestAndAuthenticatedAnswerWithTheSameStepAndRun() {
        var request = new StepFailureDecisionRequested("execution", 1, "Second", StepResult.UNDEFINED);
        var sent = new StepFailureDecisionRequestedDTO(request);
        var read = serDes.deserialize(serDes.serializeAsString(sent), MessageDTO.class);
        assertEquals(request, assertInstanceOf(StepFailureDecisionRequestedDTO.class, read).getBusinessObject());

        var answer = new StepFailureDecision("execution", 1, false);
        var reply = new StepFailureDecisionDTO(answer);
        reply.setAuthorization("test-authorization");
        String wire = serDes.serializeAsString(reply);
        assertEquals("test-authorization", serDes.deserialize(wire, JsonNode.class).path("authorization").asText());
        var received = assertInstanceOf(StepFailureDecisionDTO.class,
                serDes.deserialize(wire, MessageDTO.class));
        assertEquals(answer, received.getBusinessObject());
    }
}
