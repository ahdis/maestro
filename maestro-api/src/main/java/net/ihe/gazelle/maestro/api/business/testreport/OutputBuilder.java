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
import net.ihe.gazelle.maestro.api.business.testreport.validator.OutputValidator;

@Deprecated(forRemoval = true)
public class OutputBuilder extends AbstractBuilder<Output> {

    private String name;
    private byte[] content;
    private String reference;
    private String format;

    public OutputBuilder() {
        super();
    }

    public OutputBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    public OutputBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public OutputBuilder setContent(byte[] content) {
        this.content = content;
        return this;
    }

    public OutputBuilder setReference(String reference) {
        this.reference = reference;
        return this;
    }

    public OutputBuilder setFormat(String format) {
        this.format = format;
        return this;
    }

    @Override
    protected AbstractValidator<Output> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        return new OutputValidator(validatorBuilderFactory);
    }

    @Override
    protected Output instantiate() {
        return new Output();
    }

    @Override
    protected void make(Output output) {
        output.setName(name)
                .setContent(content)
                .setReference(reference)
                .setFormat(format)
        ;
    }
}
