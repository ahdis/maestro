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
import net.ihe.gazelle.maestro.api.business.testreport.validator.ItemValidator;

public class ItemBuilder extends AbstractBuilder<Item> {

    private String name;
    private byte[] content;
    private String reference;

    public ItemBuilder() {
        super();
    }

    public ItemBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    public ItemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder setContent(byte[] content) {
        this.content = content;
        return this;
    }

    public ItemBuilder setReference(String reference) {
        this.reference = reference;
        return this;
    }

    @Override
    protected AbstractValidator<Item> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        return new ItemValidator(validatorBuilderFactory);
    }

    @Override
    protected Item instantiate() {
        return new Item();
    }

    @Override
    protected void make(Item item) {
        item.setName(name)
                .setContent(content)
                .setReference(reference);
    }
}
