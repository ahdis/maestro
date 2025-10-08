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

package net.ihe.gazelle.maestro.api.technical.dto.testrun;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.Test;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "name",
        "steps"
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

    @JsonGetter("id")
    public String getId() {
        return getBusinessObject().getId();
    }

    @JsonSetter("id")
    public void setId(String id) {
        getBusinessObject().setId(id);
    }

    @JsonGetter("name")
    public String getName() {
        return getBusinessObject().getName();
    }

    @JsonSetter("name")
    public void setName(String name) {
        getBusinessObject().setName(name);
    }

    @SuppressWarnings("rawtypes")
    @JsonGetter("steps")
    public List<StepDTO> getSteps() {
        return getBusinessObject().getSteps().stream()
                .map(SubTypingDTO::fromBusinessObject)
                .map(StepDTO.class::cast)
                .toList();
    }

    @SuppressWarnings("rawtypes")
    @JsonSetter("steps")
    public void setSteps(List<StepDTO> steps) {
        getBusinessObject().setSteps(steps.stream()
                .map(DTO::getBusinessObject)
                .map(Step.class::cast)
                .toList());
    }
}
