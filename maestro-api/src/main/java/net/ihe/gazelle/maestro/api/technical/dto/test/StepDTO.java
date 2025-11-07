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

package net.ihe.gazelle.maestro.api.technical.dto.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.technical.dto.property.PropertyDTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "name", "type", "properties"})
public class StepDTO implements DTO<Step> {

   private final Step step;

   public StepDTO() {
      this(new Step());
   }

   public StepDTO(Step step) {
      this.step = step;
   }

   @JsonProperty("id")
   public String getId() {
      return step.getId();
   }

   public void setId(String id) {
      step.setId(id);
   }

   @JsonProperty("name")
   public String getName() {
      return step.getName();
   }

   public void setName(String name) {
      step.setName(name);
   }

   @JsonProperty("type")
   public String getType() {
      return step.getType();
   }

   public void setType(String type) {
      step.setType(type);
   }

   @JsonProperty("properties")
   public List<PropertyDTO> getProperties() {
      return step.getProperties().stream()
            .map(SubTypingDTO::fromBusinessObject)
            .map(PropertyDTO.class::cast).toList();
   }

   public void setProperties(List<PropertyDTO> propertyDTOs) {
      step.setProperties(
            propertyDTOs != null ?
                  propertyDTOs.stream()
                        .map(DTO::getBusinessObject)
                        .map(Property.class::cast).toList() :
                  null
      );
   }

   @Override
   @JsonIgnore
   public Step getBusinessObject() {
      return step;
   }
}
