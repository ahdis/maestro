/*
 * Copyright 2025 IHE International.
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
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;

public class TestRunReportValidator extends AbstractValidator<TestRunReport> {

    private final TestValidator testValidator;
    private final ItemValidator itemValidator;
    private final StepRunReportValidator stepRunReportValidator;
    private final UnexpectedErrorValidator unexpectedErrorValidator;

    public TestRunReportValidator() {
        super();
        testValidator = new TestValidator();
        itemValidator = new ItemValidator();
        stepRunReportValidator = new StepRunReportValidator();
        unexpectedErrorValidator = new UnexpectedErrorValidator();
    }

    public TestRunReportValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
        testValidator = new TestValidator();
        itemValidator = new ItemValidator();
        stepRunReportValidator = new StepRunReportValidator();
        unexpectedErrorValidator = new UnexpectedErrorValidator();
    }

    @Override
    protected ValidatorBuilder<? extends TestRunReport> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(TestRunReport.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends TestRunReport> validatorBuilder) {
        validatorBuilder
                .addRule("dateTimeValid", TestRunReport::isDateTimeValid, "DateTime must be set and not in the future")
                .addRule("resultValid", TestRunReport::isResultValid, "Result must be set adn reflect global steps result")
                .addRule("testValid", TestRunReport::isTestValid, "Test must be set")
                .addRule("urlToTestRun", TestRunReport::isUrlToTestRunValid, "URL must be set and not empty if present")
                .addMember("test", TestRunReport::getTest, testValidator::getAssembledValidatorBuilder)
                .addMember("inputs", TestRunReport::getInputs, itemValidator::getAssembledValidatorBuilder)
                .addMember("outputs", TestRunReport::getOutputs, itemValidator::getAssembledValidatorBuilder)
                .addMember("stepRunReports", TestRunReport::getStepRunReports, stepRunReportValidator::getAssembledValidatorBuilder)
                .addMember("unexpectedErrors", TestRunReport::getUnexpectedErrors, unexpectedErrorValidator::getAssembledValidatorBuilder);
    }
}
