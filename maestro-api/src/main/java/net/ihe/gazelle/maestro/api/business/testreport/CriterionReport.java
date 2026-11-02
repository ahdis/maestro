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

package net.ihe.gazelle.maestro.api.business.testreport;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Represents a report containing specific criteria and its corresponding grading result.
 * This class provides methods to retrieve and set the description of the criteria
 * and the grading result associated with it.
 */
public class CriterionReport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String description;
    private CriterionResult criterionResult;
    private String userId;
    private Instant dateTime;

    /**
     * Default constructor.
     */
    public CriterionReport() {
        // Default constructor
        criterionResult = CriterionResult.NOT_VERIFIED;
    }

    /**
     * Retrieves the name associated with the criteria in the report.
     *
     * @return the name of the criteria as a String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the name associated with the criteria in the report.
     *
     * @param description the name of the criteria as a String
     * @return the current instance of {@code CriteriaReport} to allow method chaining
     */
    public CriterionReport setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Retrieves the grading result associated with the criterion.
     *
     * @return the grading result of the criteria as an instance of {@code CriterionResult}
     */
    public CriterionResult getCriterionResult() {
        return criterionResult;
    }

    /**
     * Sets the grading result associated with this criterion.
     *
     * @param criterionResult the grading result to be set, represented as an instance of {@code CriterionResult}
     * @return the current instance of {@code CriteriaReport} to allow method chaining
     */
    public CriterionReport setCriterionResult(CriterionResult criterionResult) {
        this.criterionResult = criterionResult;
        return this;
    }

    /**
     * Retrieves the user ID associated with this criterion.
     *
     * @return the user ID as a string, or null if the user ID is not set
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID associated with the criterion report.
     *
     * @param userId the user ID to associate with the report, represented as a {@code String}
     * @return the current instance of {@code CriterionReport} to allow method chaining
     */
    public CriterionReport setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    /**
     * Retrieves the date and time associated with this report.
     *
     * @return the date and time as an {@code Instant}
     */
    public Instant getDateTime() {
        return dateTime;
    }

    /**
     * Sets the date and time associated with this criterion report.
     *
     * @param dateTime the date and time to set, represented as an {@code Instant}
     * @return the current instance of {@code CriterionReport} to allow method chaining
     */
    public CriterionReport setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    /**
     * Determines whether the name of the entity is defined and not blank.
     *
     * @return {@code true} if the name is not null and not blank, {@code false} otherwise
     */
    public boolean isNameDefined() {
        return description != null && !description.isBlank();
    }

    /**
     * Determines whether the version is null or not blank.
     *
     * @return {@code true} if the version is null or not blank, {@code false} otherwise
     */
    public boolean isGradingResultDefined() {
        return criterionResult != null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CriterionReport that)) return false;
        return Objects.equals(description, that.description)
                && criterionResult == that.criterionResult
                && Objects.equals(userId, that.userId)
                && Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, criterionResult, userId, dateTime);
    }
}
