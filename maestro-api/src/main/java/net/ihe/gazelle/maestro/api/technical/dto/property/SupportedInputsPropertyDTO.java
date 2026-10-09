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
import net.ihe.gazelle.maestro.api.business.property.SupportedInputsProperty;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.api.technical.dto.test.SupportedInputDTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Schema(
      name = "SupportedInputsProperty",
      description = "A property that supports Supported inputs value."
)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeName(SupportedInputsPropertyDTO.TYPE)
public class SupportedInputsPropertyDTO extends PropertyDTO<SupportedInputsProperty> {

   @JsonIgnore
   public static final String TYPE = "SUPPORTED_INPUTS";

   public SupportedInputsPropertyDTO() {
      super(new SupportedInputsProperty());
   }

   public SupportedInputsPropertyDTO(SupportedInputsProperty property) {
      super(property);
   }

   @Override
   public String getType() {
      return TYPE;
   }

   @SuppressWarnings("rawtypes")
   @Schema(
         name = "value",
         description = "The Supported input list defined by the property."
   )
   @JsonProperty(VALUE)
   public List<SupportedInputDTO> getValue() {
      if (property.getValue() instanceof List<?> inputs) {
         return inputs.stream()
               .map(SupportedInput.class::cast)
               .map(SubTypingDTO::fromBusinessObject)
               .map(SupportedInputDTO.class::cast)
               .toList();
      }
      return new ArrayList<>();
   }

   @SuppressWarnings("rawtypes")
   public void setValue(List<SupportedInputDTO> value) {
      property.setValue((Serializable) value.stream()
            .map(SupportedInputDTO::getBusinessObject)
            .toList());
   }

   @Override
   @JsonIgnore
   public Class<SupportedInputsProperty> getBusinessObjectClass() {
      return SupportedInputsProperty.class;
   }
}
