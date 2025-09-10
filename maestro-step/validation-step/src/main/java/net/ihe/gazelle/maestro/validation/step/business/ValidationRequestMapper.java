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
import net.ihe.gazelle.validation.v2.api.business.request.InputInRequestBuilder;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequestBuilder;

/**
 * object used to create matching validation request with Validation API
 */
public class ValidationRequestMapper {

    private ValidationRequestMapper() {
        // Empty
    }

    public static ValidationRequest createValidationRequest(ValidationStep validationStep) {
        ValidationRequestBuilder validationRequest = new ValidationRequestBuilder()
                .setValidationProfileId(validationStep.getValidationProfile().getValue());
        for (Property<byte[]> contentToValidate : validationStep.getContentsToValidate()) {
            validationRequest.addInput(new InputInRequestBuilder()
                    .setItemId(contentToValidate.getName())
                    .setContent(contentToValidate.getValue())
                    .setId(contentToValidate.getName())
            );
        }
        return validationRequest.build();
    }
}
