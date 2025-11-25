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

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"name", "type", "properties", "outputMappings"})
public class StepDTO implements DTO<Step> {

   public static final String MAPPING_DELIMITER = ":";
   private final Step step;

   public StepDTO() {
      this(new Step());
   }

   public StepDTO(Step step) {
      this.step = step;
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
   @SuppressWarnings({"java:S3740",  "rawtypes"})
   public List<PropertyDTO> getProperties() {
      return step.getProperties().stream()
            .map(SubTypingDTO::fromBusinessObject)
            .map(PropertyDTO.class::cast).toList();
   }

   @SuppressWarnings({"java:S3740", "rawtypes"})
   public void setProperties(List<PropertyDTO> propertyDTOs) {
      step.setProperties(
            propertyDTOs != null ?
                  propertyDTOs.stream()
                        .map(DTO::getBusinessObject)
                        .map(Property.class::cast).toList() :
                  null
      );
   }

   @JsonProperty("outputMappings")
   public List<String> getOutputMappings() {
      return step.getOutputMappings().entrySet().stream()
            .map(StepDTO::formatMapping)
            .toList();
   }

   public void setOutputMappings(List<String> mappings) {
      if(mappings != null) {
         mappings.stream()
               .map(StepDTO::parseMapping)
               .forEach(e -> step.getOutputMappings().put(e.getKey(), e.getValue()));
      }
   }

   @Override
   @JsonIgnore
   public Step getBusinessObject() {
      return step;
   }

   private static Map.Entry<String, String> parseMapping(String outputMapping) {
      String[] mapping = outputMapping.split(MAPPING_DELIMITER);
      if (mapping.length == 2) {
         return new AbstractMap.SimpleEntry<>(mapping[0], mapping[1]);
      } else {
         throw new IllegalArgumentException("Invalid output mapping: " + outputMapping);
      }
   }

   private static String formatMapping(Map.Entry<String, String> outputMapping) {
      return outputMapping.getKey() + MAPPING_DELIMITER + outputMapping.getValue();
   }

}

