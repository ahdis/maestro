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

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.validator.GradingCriterionValidator;

/**
 * Builder for {@link GradingCriterion}.
 */
public class GradingCriterionBuilder extends AbstractBuilder<GradingCriterion> {

    private String name;
    private CriterionResult criterionResult;

    /**
     * Default constructor.
     */
    public GradingCriterionBuilder() {
        super();
    }

    /**
     * Constructs an instance of {@code GradingCriterionBuilder} with a provided {@code ValidatorBuilderFactory}.
     *
     * @param validatorBuilderFactory the factory to be used for creating validators
     */
    public GradingCriterionBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    /**
     * Constructs an instance of {@code GradingCriterionBuilder} using an existing {@code GradingCriterion}.
     *
     * @param gradingCriterion the criteria report object whose values will
     *                              be used to initialize this builder; can be null
     */
    public GradingCriterionBuilder(GradingCriterion gradingCriterion) {
        if (gradingCriterion != null) {
            this.name = gradingCriterion.getDescription();
            this.criterionResult = gradingCriterion.getCriterionResult();
        }
    }

    /**
     * Sets the name for the criteria report.
     *
     * @param name the name to set for the grading criterion.
     * @return the current instance of {@code GradingCriterionBuilder} to allow method chaining
     */
    public GradingCriterionBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the grading result for the grading criterion.
     *
     * @param criterionResult the grading result to set; must be a valid {@link CriterionResult} value
     * @return the current instance of {@code GradingCriterionBuilder} to allow method chaining
     */
    public GradingCriterionBuilder setGradingResult(CriterionResult criterionResult) {
        this.criterionResult = criterionResult;
        return this;
    }

    @Override
    protected AbstractValidator<GradingCriterion> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        return new GradingCriterionValidator(validatorBuilderFactory);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected GradingCriterion instantiate() {
        return new GradingCriterion();
    }

    @Override
    protected void make(GradingCriterion gradingCriterion) {
        gradingCriterion
                .setDescription(name)
                .setCriterionResult(criterionResult);
    }
}
