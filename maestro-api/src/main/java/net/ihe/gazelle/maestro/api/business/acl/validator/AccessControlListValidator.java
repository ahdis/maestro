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

package net.ihe.gazelle.maestro.api.business.acl.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.security.business.acl.AccessControlList;

public class AccessControlListValidator extends AbstractValidator<AccessControlList> {
    @Override
    protected ValidatorBuilder<? extends AccessControlList> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(AccessControlList.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends AccessControlList> validatorBuilder) {
        validatorBuilder
            .addRule("Owner must be set",
                    acl -> acl.getOwners() != null && !acl.getOwners().isEmpty(), "At least one owner should be set");

    }
}
