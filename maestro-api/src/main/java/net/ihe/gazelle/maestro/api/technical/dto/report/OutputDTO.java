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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import net.ihe.gazelle.maestro.api.business.testreport.Output;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;

import java.util.Base64;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "content",
        "reference",
        "format"
})
@Deprecated(forRemoval = true)
public class OutputDTO implements DTO<Output> {

    @JsonIgnore
    private final Output output;

    public OutputDTO() {
        this(new Output());
    }

    public OutputDTO(Output output) {
        this.output = output;
    }

    @Override
    @JsonIgnore
    public Output getBusinessObject() {
        return output;
    }

    @JsonGetter(value = "name")
    public String getName() {
        return getBusinessObject().getName();
    }

    @JsonSetter(value = "name")
    public void setName(String name) {
        getBusinessObject().setName(name);
    }

    @JsonGetter(value = "content")
    public String getContent() {
        if (getBusinessObject().getContent() == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(getBusinessObject().getContent());
    }

    @JsonSetter(value = "content")
    public void setContent(String content) {
        getBusinessObject().setContent(Base64.getDecoder().decode(content));
    }

    @JsonGetter(value = "reference")
    public String getReference() {
        return getBusinessObject().getReference();
    }

    @JsonSetter(value = "reference")
    public void setReference(String reference) {
        getBusinessObject().setReference(reference);
    }

    @JsonGetter(value = "format")
    public String getFormat() {
        return getBusinessObject().getFormat();
    }

    @JsonSetter(value = "format")
    public void setFormat(String format) {
        getBusinessObject().setFormat(format);
    }
}
