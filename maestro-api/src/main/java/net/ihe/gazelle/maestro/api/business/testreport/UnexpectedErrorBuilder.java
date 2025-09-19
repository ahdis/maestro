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
import net.ihe.gazelle.lang.UnexpectedError;
import net.ihe.gazelle.maestro.api.business.testreport.validator.UnexpectedErrorValidator;

public class UnexpectedErrorBuilder extends AbstractBuilder<UnexpectedError> {

    private String name;
    private String message;
    private UnexpectedErrorBuilder cause;

    public UnexpectedErrorBuilder() {
        super();
    }

    public UnexpectedErrorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    public UnexpectedErrorBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public UnexpectedErrorBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public UnexpectedErrorBuilder setCause(UnexpectedErrorBuilder cause) {
        this.cause = cause;
        return this;
    }

    public UnexpectedErrorBuilder fromThrowable(Throwable t) {
        this.name = t.getClass().getSimpleName();
        this.message = (t.getMessage() != null && !t.getMessage().isBlank()) ? t.getMessage() : "Unexpected Error";
        this.cause = t.getCause() != null ? new UnexpectedErrorBuilder().fromThrowable(t.getCause()) : null;
        return this;
    }

    @Override
    protected AbstractValidator<UnexpectedError> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        return new UnexpectedErrorValidator(validatorBuilderFactory);
    }

    @Override
    protected UnexpectedError instantiate() {
        return new UnexpectedError();
    }

    @Override
    protected void make(UnexpectedError unexpectedError) {
        unexpectedError
                .setName(name)
                .setMessage(message)
                .setCause(AbstractBuilder.staticBuildWithoutValidation(cause));
    }
}