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

package net.ihe.gazelle.maestro.api.technical.dto.report;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.lang.UnexpectedError;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "message",
        "cause"
})
public class UnexpectedErrorDTO implements DTO<UnexpectedError> {

    @JsonIgnore
    private final UnexpectedError unexpectedError;

    public UnexpectedErrorDTO() {
        this(new UnexpectedError());
    }

    public UnexpectedErrorDTO(UnexpectedError unexpectedError) {
        this.unexpectedError = unexpectedError;
    }

    @Override
    @JsonIgnore
    public UnexpectedError getBusinessObject() {
        return unexpectedError;
    }

    @JsonGetter(value = "name")
    public String getName() {
        return getBusinessObject().getName();
    }

    @JsonSetter(value = "name")
    public void setName(String name) {
        getBusinessObject().setName(name);
    }

    @JsonGetter(value = "message")
    public String getMessage() {
        return getBusinessObject().getMessage();
    }

    @JsonSetter(value = "message")
    public void setMessage(String message) {
        getBusinessObject().setMessage(message);
    }

    @JsonGetter(value = "cause")
    public UnexpectedErrorDTO getCause() {
        if (getBusinessObject().getCause() != null) {
            return new UnexpectedErrorDTO(getBusinessObject().getCause());
        }
        return null;
    }

    @JsonSetter(value = "cause")
    public void setCause(UnexpectedErrorDTO cause) {
        if (cause != null) {
            getBusinessObject().setCause(cause.getBusinessObject());
        }
    }
}
