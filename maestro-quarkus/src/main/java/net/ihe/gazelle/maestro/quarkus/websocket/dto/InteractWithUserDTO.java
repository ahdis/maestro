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

package net.ihe.gazelle.maestro.quarkus.websocket.dto;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.message.InteractWithUser;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.api.technical.dto.test.SupportedInputDTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeName(InteractWithUserDTO.TYPE)
@JsonPropertyOrder({
        "interactionTitle",
        "message",
        "timeout",
        "supportedInputs"
})
public class InteractWithUserDTO extends MessageDTO<InteractWithUser> {

    @JsonIgnore
    public static final String TYPE = "INTERACT_WITH_USER";

    public InteractWithUserDTO() {
        super(new InteractWithUser());
    }

    public InteractWithUserDTO(InteractWithUser interactWithUser) {
        super(interactWithUser);
    }

    @JsonGetter("interactionTitle")
    public String getInteractionTitle() {
        return getBusinessObject().getInteractionTitle();
    }

    @JsonSetter("interactionTitle")
    public void setInteractionTitle(String interactionTitle) {
        getBusinessObject().setInteractionTitle(interactionTitle);
    }

    @JsonGetter("message")
    public String getMessage() {
        return getBusinessObject().getMessage();
    }

    @JsonSetter("message")
    public void setMessage(String message) {
        getBusinessObject().setMessage(message);
    }

    @JsonGetter("timeout")
    public Long getTimeout() {
        return getBusinessObject().getTimeout();
    }

    @JsonSetter("timeout")
    public void setTimeout(Long timeout) {
        getBusinessObject().setTimeout(timeout);
    }

    @SuppressWarnings("rawtypes")
    @JsonProperty("supportedInputs")
    public List<SupportedInputDTO> getSupportedInputs() {
        return message.getSupportedInputs().stream()
                .map(SubTypingDTO::fromBusinessObject)
                .map(SupportedInputDTO.class::cast)
                .toList();
    }

    @SuppressWarnings("rawtypes")
    public void setSupportedInputs(List<SupportedInputDTO> supportedInputs) {
        message.addSupportedInputs(
                supportedInputs != null ?
                        supportedInputs.stream()
                                .map(DTO::getBusinessObject)
                                .map(SupportedInput.class::cast)
                                .toList() :
                        null
        );
    }

    @Override
    @JsonIgnore
    public Class<InteractWithUser> getBusinessObjectClass() {
        return InteractWithUser.class;
    }
}
