/*
 * Copyright 2025 Kereval.
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

import net.ihe.gazelle.errorhandling.business.UnexpectedErrorValidator;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.property.PropertyValidator;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;

public class StepRunReportValidator extends AbstractValidator<StepRunReport> {

    private final PropertyValidator propertyValidator;
    private final UnexpectedErrorValidator unexpectedErrorValidator;

    public StepRunReportValidator() {
        super();
        propertyValidator = new PropertyValidator();
        unexpectedErrorValidator = new UnexpectedErrorValidator();
    }

    public StepRunReportValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
        propertyValidator = new PropertyValidator(validatorBuilderFactory);
        unexpectedErrorValidator = new UnexpectedErrorValidator(validatorBuilderFactory);
    }

    @Override
    protected ValidatorBuilder<? extends StepRunReport> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(StepRunReport.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends StepRunReport> validatorBuilder) {
        validatorBuilder
                .addRule(StepRunReport::isStepIdValid, "Step Id must be set and not empty")
                .addRule(StepRunReport::isStepNameValid, "Step Name must be set and not empty")
                .addRule( StepRunReport::isTypeValid, "Type must be set and not empty")
                .addRule(StepRunReport::isResultValid, "Result must be set")
                .addMember("outputs", StepRunReport::getOutputs, propertyValidator::getAssembledValidatorBuilder)
                .addMember("unexpectedErrors", StepRunReport::getUnexpectedErrors, unexpectedErrorValidator::getAssembledValidatorBuilder);
    }
}
