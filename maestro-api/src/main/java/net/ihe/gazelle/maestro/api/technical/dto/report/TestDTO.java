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

package net.ihe.gazelle.maestro.api.technical.dto.report;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import net.ihe.gazelle.maestro.api.business.testreport.Test;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "name",
        "version",
        "description"
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

    @JsonGetter(value = "id")
    public String getId() {
        return getBusinessObject().getId();
    }

    @JsonSetter(value = "id")
    public void setId(String id) {
        getBusinessObject().setId(id);
    }

    @JsonGetter(value = "name")
    public String getName() {
        return getBusinessObject().getName();
    }

    @JsonSetter(value = "name")
    public void setName(String name) {
        getBusinessObject().setName(name);
    }

    @JsonGetter(value = "version")
    public String getVersion() {
        return getBusinessObject().getVersion();
    }

    @JsonSetter(value = "version")
    public void setVersion(String version) {
        getBusinessObject().setVersion(version);
    }

    @JsonGetter(value = "description")
    public String getDescription() {
        return getBusinessObject().getDescription();
    }

    @JsonSetter(value = "description")
    public void setDescription(String description) {
        getBusinessObject().setDescription(description);
    }
}
