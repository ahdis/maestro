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
import net.ihe.gazelle.maestro.api.business.testreport.Input;

@Deprecated(forRemoval = true)
public class InputValidator extends AbstractValidator<Input> {

    public InputValidator() {
        super();
    }

    public InputValidator(ValidatorBuilderFactory builderFactory) {
        super(builderFactory);
    }

    @Override
    protected ValidatorBuilder<? extends Input> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(Input.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends Input> validatorBuilder) {
        validatorBuilder
                .addRule("nameValid", Input::isNameValid, "Name must be set and not empty")
                .addRule("contentValid", Input::isContentValid, "If reference is null, content must be set and not empty")
                .addRule("referenceValid", Input::isReferenceValid, "If content is null, reference must be set and not empty")
                .addRule("typeValid", Input::isTypeValid, "Type must be set and not empty")
        ;
    }
}
