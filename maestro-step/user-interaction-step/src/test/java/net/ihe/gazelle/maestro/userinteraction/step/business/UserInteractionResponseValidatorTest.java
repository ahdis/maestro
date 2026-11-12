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
import net.ihe.gazelle.framework.modelvalidator.business.Validator;
import net.ihe.gazelle.maestro.api.business.message.UserInteractionCompleted;
import net.ihe.gazelle.maestro.api.business.property.BooleanProperty;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.SupportedBooleanInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedTextInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class UserInteractionResponseValidatorTest {

    @Test
    void rejectsNullFieldDefinitionsAsInvalidConfiguration() {
        List<SupportedInput> invalidFields = Collections.singletonList(null);
        assertThrows(IllegalArgumentException.class, () -> new UserInteractionResponseValidator(invalidFields));
    }

    @Test
    void implementsFrameworkContractAndCollectsIndependentFailures() {
        Validator<UserInteractionCompleted> validator = new UserInteractionResponseValidator(
                List.of(new SupportedTextInput().setId("patientId").setLabel("Patient").setRequired(true)));
        var response = new UserInteractionCompleted().addInput(new StringProperty("unknown", "value"));
        var result = assertDoesNotThrow(() -> validator.validate(response));
        assertFalse(result.isValid());
        assertTrue(result.getAllInvalidRules().size() >= 2);
        assertThrows(IllegalArgumentException.class, () -> result.orThrow(IllegalArgumentException::new));
    }

    @Test
    void acceptsFalseAndUnansweredOptionalFieldsWithProvidedFactory() {
        var validator = new UserInteractionResponseValidator(List.of(
                new SupportedBooleanInput().setId("consent").setLabel("Consent").setRequired(true),
                new SupportedTextInput().setId("optional").setLabel("Optional")),
                DefaultValidatorBuilderFactory.INSTANCE);
        var response = new UserInteractionCompleted().addInput(new BooleanProperty("consent", false))
                .addInput(new StringProperty("optional", null));
        assertTrue(validator.validate(response).isValid());
    }

    @Test
    void malformedNamesAndNullEntriesProduceResultsInsteadOfThrowing() {
        var validator = new UserInteractionResponseValidator(List.of());
        var unnamed = new UserInteractionCompleted().addInput(new StringProperty(null, "value"));
        assertFalse(assertDoesNotThrow(() -> validator.validate(unnamed)).isValid());
        var nullEntry = new UserInteractionCompleted().addInput(null);
        assertFalse(assertDoesNotThrow(() -> validator.validate(nullEntry)).isValid());
    }
}
