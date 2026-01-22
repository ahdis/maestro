/*
 * Copyright 2026 IHE International.
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

package net.ihe.gazelle.maestro.validation.step.technical.record;

import net.ihe.gazelle.maestro.spi.business.recording.StepOutputDissector;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputDissectorFactory;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStepDefinition;

/**
 * Factory for creating ValidationStepOutputDissector instances.
 * This factory creates dissectors for validation step outputs.
 */
public class ValidationStepOutputDissectorFactory implements StepOutputDissectorFactory {

    /**
     * Default constructor.
     */
    public ValidationStepOutputDissectorFactory() { /* Default constructor */ }

    @Override
    public boolean supports(final String stepType) {
        return ValidationStepDefinition.TYPE.equals(stepType);
    }

    @Override
    public StepOutputDissector create() {
        return new ValidationStepOutputDissector();
    }

}
