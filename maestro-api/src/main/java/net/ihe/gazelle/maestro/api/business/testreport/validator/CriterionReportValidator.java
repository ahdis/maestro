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
import net.ihe.gazelle.maestro.api.business.testreport.CriterionReport;

/**
 * Validator implementation for {@link CriterionReport}, responsible for defining
 */
public class CriterionReportValidator extends AbstractValidator<CriterionReport> {

    /**
     * Default constructor
     */
    public CriterionReportValidator() {
        super();
    }

    /**
     * Constructs an CriterionReportValidator using the provided ValidatorBuilderFactory.
     *
     * @param builderFactory The factory to create ValidatorBuilder instances for validating CriterionReport objects.
     */
    public CriterionReportValidator(ValidatorBuilderFactory builderFactory) {
        super(builderFactory);
    }

    @Override
    protected ValidatorBuilder<? extends CriterionReport> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(CriterionReport.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends CriterionReport> validatorBuilder) {
        validatorBuilder
                .addRule(CriterionReport::isNameDefined, "Name must be defined and not empty.")
                .addRule(CriterionReport::isGradingResultDefined, "Grading result must be defined.");
    }
}
