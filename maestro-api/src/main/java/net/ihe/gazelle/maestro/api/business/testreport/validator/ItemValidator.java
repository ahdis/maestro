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
import net.ihe.gazelle.maestro.api.business.testreport.Item;

public class ItemValidator extends AbstractValidator<Item> {

    public ItemValidator() {
        super();
    }

    public ItemValidator(ValidatorBuilderFactory builderFactory) {
        super(builderFactory);
    }

    @Override
    protected ValidatorBuilder<? extends Item> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(Item.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends Item> validatorBuilder) {
        validatorBuilder
                .addRule("nameValid", Item::isNameValid, "Name must be set and not empty")
                .addRule("contentValid", Item::isContentValid, "If reference is null, content must be set and not empty")
                .addRule("referenceValid", Item::isReferenceValid, "If content is null, reference must be set and not empty");
    }
}
