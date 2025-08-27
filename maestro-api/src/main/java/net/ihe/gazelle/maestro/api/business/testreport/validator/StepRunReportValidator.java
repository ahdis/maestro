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

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;

public class StepRunReportValidator extends AbstractValidator<StepRunReport> {

    private final OutputValidator outputValidator;
    private final UnexpectedErrorValidator unexpectedErrorValidator;

    public StepRunReportValidator() {
        super();
        outputValidator = new OutputValidator();
        unexpectedErrorValidator = new UnexpectedErrorValidator();
    }

    public StepRunReportValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
        outputValidator = new OutputValidator();
        unexpectedErrorValidator = new UnexpectedErrorValidator();
    }

    @Override
    protected ValidatorBuilder<? extends StepRunReport> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(StepRunReport.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends StepRunReport> validatorBuilder) {
        validatorBuilder
                .addRule("stepIdValid", StepRunReport::isStepIdValid, "StepId must be set and not empty")
                .addRule("typeValid", StepRunReport::isTypeValid, "Type must be set and not empty")
                .addRule("resultValid", StepRunReport::isResultValid, "Result must be set")
                .addMember("outputs", StepRunReport::getOutputs, outputValidator::getAssembledValidatorBuilder)
                .addMember("unexpectedErrors", StepRunReport::getUnexpectedErrors, unexpectedErrorValidator::getAssembledValidatorBuilder);
    }
}
