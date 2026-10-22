/*
 * Copyright 2025-2026 IHE International.
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

package net.ihe.gazelle.maestro.api.technical.dto.message;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.technical.dto.testrun.TestRunDTO;
import net.ihe.gazelle.oidc.common.technical.dto.SecuredMessageDTO;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
        name = "StartTestRun",
        description = "Message sent when a new test run is started."
)
@JsonTypeName(StartTestRunDTO.TYPE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "type",
        "testRun",
        "testSessionId",
        "persist",
})
public class StartTestRunDTO extends MessageDTO<TestRun> implements SecuredMessageDTO {

    @JsonIgnore
    public static final String TYPE = "START_TEST_RUN";

    private String testSessionId;
    private boolean persist;
    private String authorization;

    public StartTestRunDTO() {
        this(new TestRun(), "");
    }

    public StartTestRunDTO(TestRun testRun, String authorization) {
        super(testRun);
        this.persist = true;
        this.authorization = authorization;
    }

    @Schema(
            name = "testRun",
            description = "The test run that is staring."
    )
    @JsonProperty("testRun")
    public TestRunDTO getTestRun() {
        return new TestRunDTO(message);
    }

    public void setTestRun(TestRunDTO testRun) {
        this.message = testRun.getBusinessObject();
    }

    @Schema(
            name = "testSessionId",
            description = "The id of the test session in which the test run is executed."
    )
    @JsonProperty("testSessionId")
    public String getTestSessionId() {
        return testSessionId;
    }

    public void setTestSessionId(String testSessionId) {
        this.testSessionId = testSessionId;
    }

    @Schema(
            name = "persist",
            description = "Whether the test report should be persisted."
    )
    @JsonProperty("persist")
    public boolean isPersist() {
        return persist;
    }

    public void setPersist(boolean persist) {
        this.persist = persist;
    }

    @Override
    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    @Override
    @JsonIgnore
    public Class<TestRun> getBusinessObjectClass() {
        return TestRun.class;
    }
}
