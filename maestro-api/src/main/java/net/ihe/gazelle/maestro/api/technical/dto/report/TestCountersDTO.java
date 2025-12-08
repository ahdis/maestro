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

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.testreport.TestCounters;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "total",
        "passed",
        "failed",
        "undefined",
        "unexpectedErrors"
})
public class TestCountersDTO implements DTO<TestCounters> {

    @JsonIgnore
    private final TestCounters testCounters;

    public TestCountersDTO() {
        this(new TestCounters());
    }

    public TestCountersDTO(TestCounters testCounters) {
        this.testCounters = testCounters;
    }

    @Override
    @JsonIgnore
    public TestCounters getBusinessObject() {
        return testCounters;
    }

    @JsonGetter(value = "total")
    public int getTotal() {
        return getBusinessObject().getTotal();
    }

    @JsonSetter(value = "total")
    public void setTotal(int total) {
        getBusinessObject().setTotal(total);
    }

    @JsonGetter(value = "passed")
    public int getPassed() {
        return getBusinessObject().getPassed();
    }

    @JsonSetter(value = "passed")
    public void setPassed(int passed) {
        getBusinessObject().setPassed(passed);
    }

    @JsonGetter(value = "failed")
    public int getFailed() {
        return getBusinessObject().getFailed();
    }

    @JsonSetter(value = "failed")
    public void setFailed(int failed) {
        getBusinessObject().setFailed(failed);
    }

    @JsonGetter(value = "undefined")
    public int getUndefined() {
        return getBusinessObject().getUndefined();
    }

    @JsonSetter(value = "undefined")
    public void setUndefined(int undefined) {
        getBusinessObject().setUndefined(undefined);
    }

    @JsonGetter(value = "unexpectedErrors")
    public int getUnexpectedErrors() {
        return getBusinessObject().getUnexpectedErrors();
    }

    @JsonSetter(value = "unexpectedErrors")
    public void setUnexpectedErrors(int unexpectedErrors) {
        getBusinessObject().setUnexpectedErrors(unexpectedErrors);
    }
}
