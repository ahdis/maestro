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
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.api.business.test.Test;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
      "id",
      "name",
      "supportedInputs",
      "steps",
})
public class TestDTO implements DTO<Test> {

   @JsonIgnore
   private final Test test;

   public TestDTO() {
      this(new Test());
   }

   public TestDTO(Test test) {
      this.test = test;
   }

   @Override
   @JsonIgnore
   public Test getBusinessObject() {
      return test;
   }

   @JsonProperty("id")
   public String getId() {
      return test.getId();
   }

   public void setId(String id) {
      test.setId(id);
   }

   @JsonProperty("name")
   public String getName() {
      return test.getName();
   }

   public void setName(String name) {
      test.setName(name);
   }

   @SuppressWarnings("rawtypes")
   @JsonProperty("supportedInputs")
   public List<SupportedInputDTO> getSupportedInputs() {
      return test.getSupportedInputs().stream()
            .map(SubTypingDTO::fromBusinessObject)
            .map(SupportedInputDTO.class::cast)
            .toList();
   }

   @SuppressWarnings("rawtypes")
   public void setSupportedInputs(List<SupportedInputDTO> supportedInputs) {
      test.setSupportedInputs(
            supportedInputs != null ?
                  supportedInputs.stream()
                        .map(DTO::getBusinessObject)
                        .map(SupportedInput.class::cast)
                        .toList() :
                  null
      );
   }

   @SuppressWarnings("rawtypes")
   @JsonProperty("steps")
   public List<StepDTO> getSteps() {
      return test.getSteps().stream()
            .map(SubTypingDTO::fromBusinessObject)
            .map(StepDTO.class::cast)
            .toList();
   }

   @SuppressWarnings("rawtypes")
   public void setSteps(List<StepDTO> steps) {
      test.setSteps(
            steps != null ?
                  steps.stream()
                        .map(DTO::getBusinessObject)
                        .map(Step.class::cast)
                        .toList() :
                  null
      );
   }
}
