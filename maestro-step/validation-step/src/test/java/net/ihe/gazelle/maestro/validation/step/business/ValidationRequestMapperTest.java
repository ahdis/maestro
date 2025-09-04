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
import net.ihe.gazelle.validation.v2.api.business.request.Input;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationRequestMapperTest {

    @Test
    void createValidationRequestTest() {
        ValidationStep step = new ValidationStep("validationStep",
                new Property<>(ValidationStep.VALIDATION_SERVICE, "matchbox"),
                new Property<>(ValidationStep.VALIDATION_PROFILE, "default"),
                List.of(new Property<>("contentToValidate", "content".getBytes()))
        );
        ValidationRequest request = ValidationRequestMapper.createValidationRequest(step);
        assertNotNull(request);
        assertEquals(step.getValidationProfile().getValue(), request.getValidationProfileId());
        assertEquals(1, request.getInputs().size());
        Input item = request.getInputs().getFirst();
        assertNotNull(item);
        assertEquals("contentToValidate", item.getItemId());
        String actualContent = new String(item.getContent(), StandardCharsets.UTF_8);
        assertEquals("content", actualContent);
        assertNull(item.getItemId());
        assertNull(item.getId());
        assertNull(item.getLocation());
    }

}
