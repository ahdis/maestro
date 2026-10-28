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

@Command
public class PutEvidence implements Message {

    @Serial
    private static final long serialVersionUID = -6052477359002871277L;

    private String sessionId;
    private Integer runId;
    private Integer stepId;
    private Property content;

    public String getSessionId() {
        return sessionId;
    }

    public PutEvidence setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public Integer getRunId() {
        return runId;
    }

    public PutEvidence setRunId(Integer runId) {
        this.runId = runId;
        return this;
    }

    public Integer getStepId() {
        return stepId;
    }

    public PutEvidence setStepId(Integer stepId) {
        this.stepId = stepId;
        return this;
    }

    public Property getContent() {
        return content;
    }

    public PutEvidence setContent(Property content) {
        this.content = content;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PutEvidence that)) return false;
        return Objects.equals(sessionId, that.sessionId)
                && Objects.equals(runId, that.runId)
                && Objects.equals(stepId, that.stepId)
                && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, runId, stepId, content);
    }
}
