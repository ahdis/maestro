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

package net.ihe.gazelle.maestro.api.business.testreport.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.GradingCriterion;

/**
 * Validator implementation for {@link GradingCriterion}, responsible for defining
 */
public class GradingCriterionValidator extends AbstractValidator<GradingCriterion> {

    /**
     * Default constructor
     */
    public GradingCriterionValidator() {
        super();
    }

    /**
     * Constructs an GradingCriterionValidator using the provided ValidatorBuilderFactory.
     *
     * @param builderFactory The factory to create ValidatorBuilder instances for validating GradingCriterion objects.
     */
    public GradingCriterionValidator(ValidatorBuilderFactory builderFactory) {
        super(builderFactory);
    }

    @Override
    protected ValidatorBuilder<? extends GradingCriterion> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(GradingCriterion.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends GradingCriterion> validatorBuilder) {
        validatorBuilder
                .addRule(GradingCriterion::isNameDefined, "Name must be defined and not empty.")
                .addRule(GradingCriterion::isGradingResultDefined, "Grading result must be defined.");
    }
}
