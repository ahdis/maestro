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

package net.ihe.gazelle.maestro.api.business.testreport;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.validator.EntityIdentificationValidator;

public class EntityIdentificationBuilder extends AbstractBuilder<EntityIdentification> {

    private String version;
    private String name;

    public EntityIdentificationBuilder() {
        super();
    }

    public EntityIdentificationBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }
    
    public EntityIdentificationBuilder(EntityIdentification entityIdentification) {
        if (entityIdentification != null) {
            this.version = entityIdentification.getVersion();
            this.name = entityIdentification.getName();
        }
    }

    public EntityIdentificationBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public EntityIdentificationBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    protected AbstractValidator<EntityIdentification> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        return new EntityIdentificationValidator(validatorBuilderFactory);
    }

    @Override
    protected EntityIdentification instantiate() {
        return new EntityIdentification();
    }

    @Override
    protected void make(EntityIdentification entityIdentification) {
        entityIdentification
                .setVersion(version)
                .setName(name);
    }
}
