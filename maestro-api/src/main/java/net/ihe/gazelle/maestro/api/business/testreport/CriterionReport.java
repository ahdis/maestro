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

import java.util.Objects;

/**
 * Represents a report containing specific criteria and its corresponding grading result.
 * This class provides methods to retrieve and set the description of the criteria
 * and the grading result associated with it.
 */
public class CriterionReport {

    private String description;
    private CriterionResult criterionResult;

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
                && criterionResult == that.criterionResult;
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, criterionResult);
    }
}
