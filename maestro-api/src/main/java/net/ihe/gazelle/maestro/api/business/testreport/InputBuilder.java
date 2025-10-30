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
import net.ihe.gazelle.maestro.api.business.testreport.validator.InputValidator;

@Deprecated(forRemoval = true)
public class InputBuilder extends AbstractBuilder<Input> {

    private String name;
    private byte[] content;
    private String reference;
    private String type;

    public InputBuilder() {
        super();
    }

    public InputBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    public InputBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public InputBuilder setContent(byte[] content) {
        this.content = content;
        return this;
    }

    public InputBuilder setReference(String reference) {
        this.reference = reference;
        return this;
    }


    public InputBuilder setType(String type) {
        this.type = type;
        return this;
    }


    @Override
    protected AbstractValidator<Input> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        return new InputValidator(validatorBuilderFactory);
    }

    @Override
    protected Input instantiate() {
        return new Input();
    }

    @Override
    protected void make(Input input) {
        input.setName(name)
                .setContent(content)
                .setReference(reference)
                .setType(type)
        ;
    }
}
