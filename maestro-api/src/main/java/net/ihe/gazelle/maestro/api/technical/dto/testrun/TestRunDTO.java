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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.technical.dto.property.PropertyDTO;
import net.ihe.gazelle.maestro.api.technical.dto.test.TestDTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "test",
        "inputs"
})
public class TestRunDTO implements DTO<TestRun> {

    @JsonIgnore
    private final TestRun testRun;

    public TestRunDTO() {
        this(new TestRun());
    }

    public TestRunDTO(TestRun testRun) {
        this.testRun = testRun;
    }

    @Override
    @JsonIgnore
    public TestRun getBusinessObject() {
        return testRun;
    }

    @JsonProperty("test")
    public TestDTO getTest() {
        return new TestDTO(testRun.getTest());
    }

    public void setTest(TestDTO test) {
       testRun.setTest(test.getBusinessObject());
    }

    @JsonProperty("inputs")
    public List<PropertyDTO> getInputs() {
        return testRun.getInputs().stream()
              .map(SubTypingDTO::fromBusinessObject)
              .map(PropertyDTO.class::cast)
              .toList();
    }

    public void setInputs(List<PropertyDTO> inputs) {
        testRun.setInputs(
              inputs != null ? inputs.stream()
                    .map(DTO::getBusinessObject)
                    .map(Property.class::cast).toList() :
                     null
        );
    }
}
