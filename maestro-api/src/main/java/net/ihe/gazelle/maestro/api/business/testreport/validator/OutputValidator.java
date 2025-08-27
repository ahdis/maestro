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
import net.ihe.gazelle.maestro.api.business.testreport.Output;

public class OutputValidator extends AbstractValidator<Output> {

    public OutputValidator() {
        super();
    }

    public OutputValidator(ValidatorBuilderFactory builderFactory) {
        super(builderFactory);
    }

    @Override
    protected ValidatorBuilder<? extends Output> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(Output.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends Output> validatorBuilder) {
        validatorBuilder
                .addRule("nameValid", Output::isNameValid, "Name must be set and not empty")
                .addRule("contentValid", Output::isContentValid, "If reference is null, content must be set and not empty")
                .addRule("referenceValid", Output::isReferenceValid, "If content is null, reference must be set and not empty")
                .addRule("formatValid", Output::isFormatValid, "Format must be set and not empty");
    }
}
