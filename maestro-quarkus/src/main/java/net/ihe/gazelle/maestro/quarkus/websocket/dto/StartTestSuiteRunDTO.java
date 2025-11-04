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

package net.ihe.gazelle.maestro.quarkus.websocket.dto;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.api.technical.dto.testrun.TestSuiteRunDTO;

@JsonTypeName(StartTestSuiteRunDTO.TYPE)
@JsonPropertyOrder({
        "type",
        "testSuiteRun"
})
public class StartTestSuiteRunDTO extends MessageDTO<TestSuiteRun> {

    @JsonIgnore
    public static final String TYPE = "START_TEST_SUITE_RUN";

    public StartTestSuiteRunDTO() {
        super(new TestSuiteRun());
    }

    public StartTestSuiteRunDTO(TestSuiteRun testSuiteRun) {
        super(testSuiteRun);
    }

    @JsonGetter("testSuiteRun")
    public TestSuiteRunDTO getTestSuiteRun() {
        return new TestSuiteRunDTO(getBusinessObject());
    }

    @JsonSetter("testSuiteRun")
    public void setTestSuiteRun(TestSuiteRunDTO testSuiteRun) {
        this.message = testSuiteRun.getBusinessObject();
    }

    @Override
    @JsonIgnore
    public Class<TestSuiteRun> getBusinessObjectClass() {
        return TestSuiteRun.class;
    }
}

