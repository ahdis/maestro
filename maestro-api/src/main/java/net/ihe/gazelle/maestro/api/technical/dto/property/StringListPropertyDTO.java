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

package net.ihe.gazelle.maestro.api.technical.dto.property;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.api.business.property.StringListProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Schema(
        name = "StringListProperty",
        description = "A property that supports a list of string value."
)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeName(StringListPropertyDTO.TYPE)
public class StringListPropertyDTO extends PropertyDTO<StringListProperty>  {

    @JsonIgnore
    public static final String TYPE = "STRING_LIST";

    public StringListPropertyDTO() {
        super(new StringListProperty());
    }

    public StringListPropertyDTO(StringListProperty property) {
        super(property);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @SuppressWarnings("unchecked")
    @Schema(
            name = "value",
            description = "The Supported input list defined by the property."
    )
    @JsonProperty(VALUE)
    public List<String> getValue() {
        if (property.getValue() instanceof List<?> values) {
            return values.stream()
                    .map(String.class::cast)
                    .toList();
        }
        return new ArrayList<>();
    }

    public void setValue(List<String> value) {
        property.setValue((Serializable) value);
    }

    @Override
    public Class<StringListProperty> getBusinessObjectClass() {
        return StringListProperty.class;
    }
}
