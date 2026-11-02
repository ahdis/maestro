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

import net.ihe.gazelle.maestro.api.business.testreport.CriterionReport;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a message indicating that a grading has been completed.
 */
public class GradingCompleted implements Message {

    @Serial
    private static final long serialVersionUID = 8129967978885590003L;

    /**
     * The session id of the execution
     */
    private String sessionId;

    /**
     * The result of the grading
     */
    private StepResult result;

    /**
     * The criteria report associated with the grading
     */
    private List<CriterionReport> criteriaReport;

    /**
     * The note associated with the grading to be set in the test report
     */
    private String note;

    /**
     * Default constructor.
     */
    public GradingCompleted() {
        this.criteriaReport = new ArrayList<>();
    }

    /**
     * Retrieves the session identifier associated with the execution.
     *
     * @return the session identifier as a string
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session identifier for this grading.
     *
     * @param sessionId the session identifier to associate with the grading
     * @return the current instance of {@code GradingCompleted}
     */
    public GradingCompleted setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    /**
     * Retrieves the result of the grading.
     *
     * @return the grading result, represented as an instance of {@code StepResult}
     */
    public StepResult getResult() {
        return result;
    }

    /**
     * Sets the result of the grading.
     *
     * @param result the result to associate with this grading, represented as an instance of {@code StepResult}
     * @return the current instance of {@code GradingCompleted}
     */
    public GradingCompleted setResult(StepResult result) {
        this.result = result;
        return this;
    }

    /**
     * Retrieves a list of criterion reports associated with the grading.
     * The returned list contains copies of the original {@code CriterionReport} objects.
     *
     * @return a list of {@code CriterionReport} instances representing the grading criteria and their results
     */
    public List<CriterionReport> getCriteriaReport() {
        return new ArrayList<>(criteriaReport);
    }

    /**
     * Sets the criteria report associated with the grading.
     * This method replaces the current criteria report with a copy of the provided list.
     *
     * @param criteriaReport the list of {@code CriterionReport} instances to associate with the grading
     * @return the current instance of {@code GradingCompleted} to allow method chaining
     */
    public GradingCompleted setCriteriaReport(List<CriterionReport> criteriaReport) {
        this.criteriaReport = new ArrayList<>(criteriaReport);
        return this;
    }

    /**
     * Retrieves the note associated with the grading.
     *
     * @return the note as a string, or null if no note is set.
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the note associated with the grading.
     *
     * @param note the note to be associated with the grading, represented as a string
     * @return the current instance of {@code GradingCompleted} for method chaining
     */
    public GradingCompleted setNote(String note) {
        this.note = note;
        return this;
    }
}
