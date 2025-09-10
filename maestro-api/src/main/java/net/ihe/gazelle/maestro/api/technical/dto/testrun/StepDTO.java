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
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;

import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonPropertyOrder({"name", "properties"})
public abstract class StepDTO<T extends Step> implements SubTypingDTO<T> {

    protected T step;

    protected StepDTO(T step) {
        initWithBusinessObject(step);
    }

    @JsonGetter("name")
    public String getName() {
        return step.getName();
    }

    @JsonSetter("name")
    public void setName(String name) {
        this.step.setName(name);
    }

    @JsonGetter("properties")
    public List<PropertyDTO> getProperties() {
        List<PropertyDTO> propertyDTOList = new ArrayList<>();
        for (Property<?> property : step.getProperties()) {
            propertyDTOList.add(new PropertyDTO(property));
        }
        return propertyDTOList;
    }

    @JsonSetter("properties")
    public void setProperties(List<PropertyDTO> propertyDTOList) {
        List<Property<?>> properties = new ArrayList<>();
        for (PropertyDTO propertyDTO : propertyDTOList) {
            properties.add(propertyDTO.getDomain());
        }
        step.setProperties(properties);
    }

    @JsonIgnore
    @Override
    public SubTypingDTO<T> initWithBusinessObject(T step) {
        this.step = step;
        return this;
    }

    @JsonIgnore
    @Override
    public T getBusinessObject() {
        return step;
    }
}
