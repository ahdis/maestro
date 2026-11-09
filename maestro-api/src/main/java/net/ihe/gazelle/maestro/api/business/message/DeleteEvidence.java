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

import java.io.Serial;
import java.util.Objects;

/**
 * Represents a message to delete an evidence.
 */
@Command
public class DeleteEvidence implements Message {

    @Serial
    private static final long serialVersionUID = 2918640235352593516L;

    private String sessionId;
    private Integer runId;
    private String stepName;
    private String evidenceName;

    /**
     * Retrieves the session identifier associated with this message.
     *
     * @return the session ID as a string
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session identifier for this instance.
     *
     * @param sessionId the session ID to associate with this instance
     * @return the current instance of {@code DeleteEvidence} for method chaining
     */
    public DeleteEvidence setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    /**
     * Retrieves the run identifier associated with this message.
     *
     * @return the run ID as an Integer
     */
    public Integer getRunId() {
        return runId;
    }

    /**
     * Sets the run identifier for this instance.
     *
     * @param runId the run ID to associate with this instance
     * @return the current instance of {@code DeleteEvidence} for method chaining
     */
    public DeleteEvidence setRunId(Integer runId) {
        this.runId = runId;
        return this;
    }

    /**
     * Retrieves the step name associated with this message.
     *
     * @return the step name as a string
     */
    public String getStepName() {
        return stepName;
    }

    /**
     * Sets the step name associated with this instance.
     *
     * @param stepName the step name to associate with this instance
     * @return the current instance of {@code DeleteEvidence} for method chaining
     */
    public DeleteEvidence setStepName(String stepName) {
        this.stepName = stepName;
        return this;
    }

    /**
     * Retrieves the name of the evidence associated with this message.
     *
     * @return the evidence name as a string
     */
    public String getEvidenceName() {
        return evidenceName;
    }

    /**
     * Sets the name of the evidence associated with this instance.
     *
     * @param evidenceName the name of the evidence to associate with this instance
     * @return the current instance of {@code DeleteEvidence} for method chaining
     */
    public DeleteEvidence setEvidenceName(String evidenceName) {
        this.evidenceName = evidenceName;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DeleteEvidence that)) return false;
        return Objects.equals(sessionId, that.sessionId)
                && Objects.equals(runId, that.runId)
                && Objects.equals(stepName, that.stepName)
                && Objects.equals(evidenceName, that.evidenceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, runId, stepName, evidenceName);
    }
}
