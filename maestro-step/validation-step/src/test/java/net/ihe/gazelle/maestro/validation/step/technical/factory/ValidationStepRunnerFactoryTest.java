/*
 * Copyright 2025 IHE International.
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

package net.ihe.gazelle.maestro.validation.step.technical.factory;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.PropertyRef;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidationStepRunnerFactoryTest {

    ValidationStepRunnerFactory validationStepRunnerFactory;

    @BeforeEach
    void setUp() {
        validationStepRunnerFactory = new ValidationStepRunnerFactory();
    }

    @Test
    void canonicalNameTest(){
        assertEquals(ValidationStep.class.getCanonicalName(), validationStepRunnerFactory.getSupportedStep());
    }

    @Test
    void getRequiredServicesOfValidationStep() {
        Step validationStep = new ValidationStep(
                "iti78-request-validation",
                new Property<>(ValidationStep.VALIDATION_SERVICE, "matchbox"),
                new Property<>(ValidationStep.VALIDATION_PROFILE, "Parameters"),
                new PropertyRef<>(ValidationStep.CONTENT_TO_VALIDATE, "iti78-request", byte[].class)
        );
        assertEquals(1, validationStepRunnerFactory.getRequiredServices(validationStep).size());
    }

}
