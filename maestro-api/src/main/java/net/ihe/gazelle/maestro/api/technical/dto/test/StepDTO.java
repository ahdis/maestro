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

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.technical.dto.property.PropertyDTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonPropertyOrder({"name", "type", "properties"})
public abstract class StepDTO<T extends Step> implements SubTypingDTO<T> {

   protected T step;

   protected StepDTO(T step) {
      initWithBusinessObject(step);
   }

   @JsonProperty("name")
   public String getName() {
      return step.getName();
   }

   public void setName(String name) {
      step.setName(name);
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
   public SubTypingDTO<T> initWithBusinessObject(T step) {
      this.step = step;
      return this;
   }

   @Override
   @JsonIgnore
   public T getBusinessObject() {
      return step;
   }
}
