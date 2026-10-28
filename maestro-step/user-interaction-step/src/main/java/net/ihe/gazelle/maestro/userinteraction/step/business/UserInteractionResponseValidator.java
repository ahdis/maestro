/*
 * Copyright 2025-2026 IHE International.
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

package net.ihe.gazelle.maestro.userinteraction.step.business;

import net.ihe.gazelle.framework.modelvalidator.business.DefaultValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.message.UserInteractionCompleted;
import net.ihe.gazelle.maestro.api.business.property.BooleanProperty;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.SupportedBooleanInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedBooleanInputValidator;
import net.ihe.gazelle.maestro.api.business.test.SupportedFileInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedFileInputValidator;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedTextInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedTextInputValidator;
import net.ihe.gazelle.maestro.api.business.test.SupportedValueSetInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedValueSetInputValidator;
import net.ihe.gazelle.maestro.api.business.testreport.validator.PropertyInReportValidator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Validates submitted answers against the configured fields using the model-validator contract. */
final class UserInteractionResponseValidator extends AbstractValidator<UserInteractionCompleted> {
    private final Map<String, SupportedInput> fields;
    private final PropertyInReportValidator propertyValidator;

    UserInteractionResponseValidator(List<SupportedInput> supportedInputs) {
        this(supportedInputs, DefaultValidatorBuilderFactory.INSTANCE);
    }

    UserInteractionResponseValidator(List<SupportedInput> supportedInputs, ValidatorBuilderFactory factory) {
        super(factory);
        propertyValidator = new PropertyInReportValidator(factory);
        fields = configuredFields(supportedInputs, factory);
    }

    @Override
    protected ValidatorBuilder<? extends UserInteractionCompleted> instantiateValidatorBuilder(ValidatorBuilderFactory factory) {
        return factory.getBuilder(UserInteractionCompleted.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addRulesAndMembers(ValidatorBuilder<? extends UserInteractionCompleted> builder) {
        builder
                .addRule(this::areAnswersKnownAndUnique, "Answers must reference configured fields without duplicates")
                .addRule(this::areAnswerTypesValid, "Answers must contain direct values matching their configured types")
                .addRule(this::areSelectionsValid, "Selections must belong to the configured possible values")
                .addRule(this::areRequiredFieldsAnswered, "Required interaction fields must have a value")
                .addMember("inputs", response -> response.getInputs().stream().filter(Objects::nonNull).toList(),
                        propertyValidator::getAssembledValidatorBuilder);
    }

    private Map<String, SupportedInput> configuredFields(List<SupportedInput> supportedInputs, ValidatorBuilderFactory factory) {
        Map<String, SupportedInput> configured = new HashMap<>();
        for (SupportedInput field : supportedInputs == null ? List.<SupportedInput>of() : supportedInputs) {
            switch (field) {
                case null -> throw new IllegalArgumentException("Interaction field must not be null");
                case SupportedTextInput text -> new SupportedTextInputValidator(factory).assertValid(text);
                case SupportedBooleanInput bool -> new SupportedBooleanInputValidator(factory).assertValid(bool);
                case SupportedFileInput file -> new SupportedFileInputValidator(factory).assertValid(file);
                case SupportedValueSetInput selection -> new SupportedValueSetInputValidator(factory).assertValid(selection);
                default -> throw new IllegalArgumentException("Unsupported interaction field type: " + field.getClass().getSimpleName());
            }
            if (configured.putIfAbsent(field.getId(), field) != null) {
                throw new IllegalArgumentException("Interaction field IDs must be unique");
            }
        }
        return Map.copyOf(configured);
    }

    private boolean areAnswersKnownAndUnique(UserInteractionCompleted response) {
        Set<String> received = new HashSet<>();
        return response.getInputs().stream().allMatch(input -> input != null && input.isNameDefined()
                && fields.containsKey(input.getName()) && received.add(input.getName()));
    }

    private boolean areAnswerTypesValid(UserInteractionCompleted response) {
        return response.getInputs().stream().allMatch(input -> {
            if (input == null || !input.isNameDefined() || input.isReference()) {
                return false;
            }
            SupportedInput field = fields.get(input.getName());
            if (field == null || !propertyType(field).isInstance(input)) {
                return false;
            }
            Object value = input.getValue();
            return value == null || input instanceof StringProperty && value instanceof String
                    || input instanceof BooleanProperty && value instanceof Boolean
                    || input instanceof ByteArrayProperty && value instanceof byte[];
        });
    }

    private boolean areSelectionsValid(UserInteractionCompleted response) {
        return response.getInputs().stream().filter(Objects::nonNull).filter(Property::isNameDefined).allMatch(input -> {
            SupportedInput field = fields.get(input.getName());
            if (field instanceof SupportedTextInput textField && !textField.getPossibleValues().isEmpty()
                    && input.getValue() instanceof String text && !text.isBlank()) {
                return textField.getPossibleValues().contains(text);
            }
            return true;
        });
    }

    private boolean areRequiredFieldsAnswered(UserInteractionCompleted response) {
        Set<String> answered = new HashSet<>();
        for (Property input : response.getInputs()) {
            if (input != null && !input.isReference()) {
                Object value = input.getValue();
                if (value != null && !(value instanceof String text && text.isBlank())) {
                    answered.add(input.getName());
                }
            }
        }
        return fields.values().stream().allMatch(field -> !field.isRequired() || answered.contains(field.getId()));
    }

    private Class<? extends Property> propertyType(SupportedInput field) {
        if (field instanceof SupportedTextInput || field instanceof SupportedValueSetInput) {
            return StringProperty.class;
        }
        if (field instanceof SupportedBooleanInput) {
            return BooleanProperty.class;
        }
        if (field instanceof SupportedFileInput) {
            return ByteArrayProperty.class;
        }
        throw new IllegalArgumentException("Unsupported interaction field type: " + field.getClass().getSimpleName());
    }
}
