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
import net.ihe.gazelle.maestro.api.business.message.StartTestRun;
import net.ihe.gazelle.maestro.api.technical.dto.testrun.TestRunDTO;
import net.ihe.gazelle.oidc.common.technical.dto.SecuredMessageDTO;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Duration;
import java.time.Instant;

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
        "timeoutSeconds",
        "persist",
})
public class StartTestRunDTO extends MessageDTO<StartTestRun> implements SecuredMessageDTO {

    @JsonIgnore
    public static final String TYPE = "START_TEST_RUN";

    private String authorization;

    public StartTestRunDTO() {
        this(new StartTestRun(), "");
    }

    public StartTestRunDTO(StartTestRun startTestRun, String authorization) {
        super(startTestRun);
        this.authorization = authorization;
    }

    @Schema(
            name = "testRun",
            description = "The test run that is staring."
    )
    @JsonProperty("testRun")
    public TestRunDTO getTestRun() {
        return new TestRunDTO(message.getTestRun());
    }

    public void setTestRun(TestRunDTO testRun) {
        message.setTestRun(testRun.getBusinessObject());
    }

    @Schema(
            name = "testSessionId",
            description = "The id of the test session in which the test run is executed."
    )
    @JsonProperty("testSessionId")
    public String getTestSessionId() {
        return message.getTestSessionId();
    }

    public void setTestSessionId(String testSessionId) {
        message.setTestSessionId(testSessionId);
    }

    @Schema(
            name = "timeoutSeconds",
            description = "The timeout in seconds for the test run."
    )
    @JsonProperty("timeoutSeconds")
    public Long getTimeoutSeconds() {
        return Duration.between(Instant.now(), message.getExpirationTimestamp()).toSeconds();
    }

    public void setTimeoutSeconds(Long timeoutSeconds) {
        message.setExpirationTimestamp(timeoutSeconds);
    }

    @Schema(
            name = "persist",
            description = "Whether the test report should be persisted."
    )
    @JsonProperty("persist")
    public boolean isPersist() {
        return message.isPersist();
    }

    public void setPersist(boolean persist) {
        message.setPersist(persist);
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonProperty("promptOnStepFailure")
    public boolean isPromptOnStepFailure() {
        return message.isPromptOnStepFailure();
    }

    public void setPromptOnStepFailure(boolean promptOnStepFailure) {
        message.setPromptOnStepFailure(promptOnStepFailure);
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
    public Class<StartTestRun> getBusinessObjectClass() {
        return StartTestRun.class;
    }
}
