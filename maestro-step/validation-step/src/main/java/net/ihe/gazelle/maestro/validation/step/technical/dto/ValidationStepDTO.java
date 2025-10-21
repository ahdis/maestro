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

package net.ihe.gazelle.maestro.validation.step.technical.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.api.technical.dto.test.StepDTO;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStep;

@JsonTypeName(ValidationStep.TYPE)
public class ValidationStepDTO extends StepDTO<ValidationStep> {

    public ValidationStepDTO() {
        super(new ValidationStep());
    }

    public ValidationStepDTO(ValidationStep validationStep) {
        super(validationStep);
    }

    @Override
    public Class<ValidationStep> getBusinessObjectClass() {
        return ValidationStep.class;
    }
}
