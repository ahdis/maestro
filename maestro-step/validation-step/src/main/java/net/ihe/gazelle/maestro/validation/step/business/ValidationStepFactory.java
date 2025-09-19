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

package net.ihe.gazelle.maestro.validation.step.business;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.StepFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO : to delete when StepFactoryProvider will be deleted
public class ValidationStepFactory implements StepFactory {

    @Override
    @SuppressWarnings("unchecked")
    public Step create(String name, Map<String, Property<?>> properties) {
        return new ValidationStep(
                name,
                (Property<String>) properties.get(ValidationStep.VALIDATION_SERVICE),
                (Property<String>) properties.get(ValidationStep.VALIDATION_PROFILE),
                getContentsToValidate(properties)
        );
    }

    @Override
    public String getSupportedStep() {
        return ValidationStep.TYPE;
    }

    private static List<Property<byte[]>> getContentsToValidate(Map<String, Property<?>> properties) {
        // return all byte[] properties which key is not VALIDATION_SERVICE or VALIDATION_PROFILE
        return new ArrayList<>(properties.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(ValidationStep.VALIDATION_SERVICE)
                    && !entry.getKey().equals(ValidationStep.VALIDATION_PROFILE)
                    && byte[].class.equals(entry.getValue().getType()))
                .map(Map.Entry::getValue)
                .map(property -> (Property<byte[]>) property)
                .toList());
    }
}
