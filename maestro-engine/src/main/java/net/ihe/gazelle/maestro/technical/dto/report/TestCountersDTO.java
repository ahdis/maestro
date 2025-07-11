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

package net.ihe.gazelle.maestro.technical.dto.report;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.testreport.TestCounters;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"total", "passed", "failed", "undefined", "unexpectedErrors"})
public class TestCountersDTO {

    @JsonIgnore
    private final TestCounters domain;

    public TestCountersDTO() {
        this.domain = new TestCounters();
    }

    public TestCountersDTO(TestCounters domain) {
        this.domain = domain;
    }

    @JsonGetter(value = "total")
    public int getTotal() {
        return domain.getTotal();
    }

    @JsonSetter(value = "total")
    public void setTotal(int total) {
        domain.setTotal(total);
    }

    @JsonGetter(value = "passed")
    public int getPassed() {
        return domain.getPassed();
    }

    @JsonSetter(value = "passed")
    public void setPassed(int passed) {
        domain.setPassed(passed);
    }

    @JsonGetter(value = "failed")
    public int getFailed() {
        return domain.getFailed();
    }

    @JsonSetter(value = "failed")
    public void setFailed(int failed) {
        domain.setFailed(failed);
    }

    @JsonGetter(value = "undefined")
    public int getUndefined() {
        return domain.getUndefined();
    }

    @JsonSetter(value = "undefined")
    public void setUndefined(int undefined) {
        domain.setUndefined(undefined);
    }

    @JsonGetter(value = "unexpectedErrors")
    public int getUnexpectedErrors() {
        return domain.getUnexpectedErrors();
    }

    @JsonSetter(value = "unexpectedErrors")
    public void setUnexpectedErrors(int unexpectedErrors) {
        domain.setUnexpectedErrors(unexpectedErrors);
    }

    @JsonIgnore
    public TestCounters getDomain() {
        return domain;
    }
}
