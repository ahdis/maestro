/*
 * Copyright 2026 IHE International.
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

package net.ihe.gazelle.maestro.api.business.message;

import net.ihe.gazelle.lang.Command;
import net.ihe.gazelle.maestro.api.business.property.Property;

import java.io.Serial;
import java.util.Objects;


/**
 * Represents a message for adding new evidence.
 */
@Command
public class PutEvidence implements Message {

    @Serial
    private static final long serialVersionUID = -6052477359002871277L;

    private String sessionId;
    private Integer runId;
    private String stepName;
    private Property content;

    /**
     * Retrieves the session ID associated with this instance.
     *
     * @return the session ID as a string, or null if no session ID is set.
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session ID associated with this instance.
     *
     * @param sessionId the session ID to be set.
     * @return the current instance of {@code PutEvidence} for method chaining.
     */
    public PutEvidence setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    /**
     * Retrieves the run ID associated with this instance.
     *
     * @return the run ID as an {@code Integer}, or null if no run ID is set.
     */
    public Integer getRunId() {
        return runId;
    }

    /**
     * Sets the run ID associated with this instance.
     *
     * @param runId the run ID to be set.
     * @return the current instance of {@code PutEvidence} for method chaining.
     */
    public PutEvidence setRunId(Integer runId) {
        this.runId = runId;
        return this;
    }

    /**
     * Retrieves the name of the step associated with this instance.
     *
     * @return the step name as a string, or null if no step name is set.
     */
    public String getStepName() {
        return stepName;
    }

    /**
     * Sets the name of the step associated with this instance.
     *
     * @param stepName the name of the step to be set.
     * @return the current instance of {@code PutEvidence} for method chaining.
     */
    public PutEvidence setStepName(String stepName) {
        this.stepName = stepName;
        return this;
    }

    /**
     * Retrieves the content associated with this instance.
     *
     * @return the content as a {@code Property} instance, or null if no content is set.
     */
    public Property getContent() {
        return content;
    }

    /**
     * Sets the content associated with this instance.
     *
     * @param content the content to be set as a {@code Property} instance.
     * @return the current instance of {@code PutEvidence} for method chaining.
     */
    public PutEvidence setContent(Property content) {
        this.content = content;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PutEvidence that)) return false;
        return Objects.equals(sessionId, that.sessionId)
                && Objects.equals(runId, that.runId)
                && Objects.equals(stepName, that.stepName)
                && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, runId, stepName, content);
    }
}
