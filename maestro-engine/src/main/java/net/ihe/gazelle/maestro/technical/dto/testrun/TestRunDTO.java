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

package net.ihe.gazelle.maestro.technical.dto.testrun;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"testSetId", "test", "inputs"})
public class TestRunDTO {

    @JsonIgnore
    private final TestRun domain;

    public TestRunDTO() {
        domain = new TestRun();
    }

    public TestRunDTO(TestRun domain) {
        this.domain = domain;
    }

    @JsonGetter("testSetId")
    public String getTestSetId() {
        return domain.getTestSetId();
    }

    @JsonSetter("testSetId")
    public void setTestSetId(String testSetId) {
        domain.setTestSetId(testSetId);
    }

    @JsonGetter("test")
    public TestDTO getTest() {
        return new TestDTO(domain.getTest());
    }

    @JsonSetter("test")
    public void setTest(TestDTO test) {
        domain.setTest(test.getDomain());
    }

    @JsonGetter("inputs")
    public List<PropertyDTO> getInputs() {
        List<PropertyDTO> propertyDTOList = new ArrayList<>();
        for (Property<?> property : domain.getInputs()) {
            propertyDTOList.add(new PropertyDTO(property));
        }
        return propertyDTOList;
    }

    @JsonSetter("inputs")
    public void setInputs(List<PropertyDTO> inputs) {
        List<Property<?>> properties = new ArrayList<>();
        for (PropertyDTO propertyDTO : inputs) {
            properties.add(propertyDTO.getDomain());
        }
        domain.setInputs(properties);
    }

    @JsonIgnore
    public TestRun getDomain() {
        return domain;
    }
}
