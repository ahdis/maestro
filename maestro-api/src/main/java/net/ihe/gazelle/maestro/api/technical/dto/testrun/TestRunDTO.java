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
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "testSetId",
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

    @JsonGetter("testSetId")
    public String getTestSetId() {
        return getBusinessObject().getTestSetId();
    }

    @JsonSetter("testSetId")
    public void setTestSetId(String testSetId) {
        getBusinessObject().setTestSetId(testSetId);
    }

    @JsonGetter("test")
    public TestDTO getTest() {
        return new TestDTO(getBusinessObject().getTest());
    }

    @JsonSetter("test")
    public void setTest(TestDTO test) {
        getBusinessObject().setTest(test.getBusinessObject());
    }

    @JsonGetter("inputs")
    public List<PropertyDTO> getInputs() {
        return getBusinessObject().getInputs().stream().map(PropertyDTO::new).toList();
    }

    @JsonSetter("inputs")
    public void setInputs(List<PropertyDTO> inputs) {
        List<Property<?>> properties = new ArrayList<>();
        for (PropertyDTO propertyDTO : inputs) {
            properties.add(propertyDTO.getBusinessObject());
        }
        getBusinessObject().setInputs(properties);
    }
}
