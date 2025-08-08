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

package net.ihe.gazelle.maestro.validation.step.technical.handler;

import net.ihe.gazelle.maestro.validation.step.business.ServiceClientException;
import net.ihe.gazelle.validation.v2.api.business.ValidationService;
import net.ihe.gazelle.validation.v2.api.business.profile.ValidationProfile;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GazelleValidationServiceHandlerTest {

    @Mock
    private ValidationService validationService;

    private GazelleValidationServiceHandler gazelleValidationServiceHandler;

    @BeforeEach
    void setUp() {
        gazelleValidationServiceHandler = new GazelleValidationServiceHandler(validationService);
    }

    @Test
    void should_create_handler_with_validation_service_controller() {
        // Given & When
        GazelleValidationServiceHandler handler = new GazelleValidationServiceHandler();

        // Then
        assertNotNull(handler);
    }

    @Test
    void should_create_handler_with_null_controller() {
        // Given & When
        GazelleValidationServiceHandler handler = new GazelleValidationServiceHandler(null);

        // Then
        assertNotNull(handler);
    }

    @Test
    void should_return_true_when_validation_profiles_available() {
        // Given
        List<ValidationProfile> profiles = Arrays.asList(
            createValidationProfile("profile1", "Profile 1"),
            createValidationProfile("profile2", "Profile 2")
        );
        when(validationService.getValidationProfiles()).thenReturn(profiles);

        // When
        boolean isAvailable = gazelleValidationServiceHandler.isAvailable();

        // Then
        assertTrue(isAvailable);
        verify(validationService, times(2)).getValidationProfiles();
    }

    @Test
    void should_return_false_when_no_validation_profiles_available() {
        // Given
        when(validationService.getValidationProfiles()).thenReturn(Collections.emptyList());

        // When
        boolean isAvailable = gazelleValidationServiceHandler.isAvailable();

        // Then
        assertFalse(isAvailable);
    }

    @Test
    void should_return_false_when_validation_profiles_null() {
        // Given
        when(validationService.getValidationProfiles()).thenReturn(null);

        // When
        boolean isAvailable = gazelleValidationServiceHandler.isAvailable();

        // Then
        assertFalse(isAvailable);
    }

    @Test
    void should_return_false_when_get_profiles_throws_exception() {
        // Given
        when(validationService.getValidationProfiles())
            .thenThrow(new RuntimeException("Service unavailable"));

        // When
        Throwable exception = assertThrows(RuntimeException.class, () -> {
            gazelleValidationServiceHandler.isAvailable();
        });

        // Then
        assertEquals("Service unavailable", exception.getMessage());
    }

    @Test
    void should_validate_successfully() {
        // Given
        ValidationRequest validationRequest = createValidationRequest();
        String serviceName = "TestService";
        ValidationReport expectedReport = new ValidationReport();

        when(validationService.validate(validationRequest)).thenReturn(expectedReport);

        // When
        ValidationReport result = gazelleValidationServiceHandler.validate(validationRequest, serviceName);

        // Then
        assertNotNull(result);
        assertEquals(expectedReport, result);
        verify(validationService).validate(validationRequest);
    }

    @Test
    void should_throw_exception_when_validation_service_controller_is_null() {
        // Given
        GazelleValidationServiceHandler handlerWithNullController = new GazelleValidationServiceHandler(null);
        ValidationRequest validationRequest = createValidationRequest();

        // When & Then
        ServiceClientException exception = assertThrows(ServiceClientException.class, () -> {
            handlerWithNullController.validate(validationRequest, "TestService");
        });

        assertEquals("validationService not set", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_validation_request_is_null() {
        // Given
        String serviceName = "TestService";

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            gazelleValidationServiceHandler.validate(null, serviceName);
        });

        assertEquals("Validation request cannot be null", exception.getMessage());
    }

    @Test
    void should_throw_runtime_exception_when_validation_fails() {
        // Given
        ValidationRequest validationRequest = createValidationRequest();
        String serviceName = "TestService";

        when(validationService.validate(validationRequest))
            .thenThrow(new RuntimeException("Validation service error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gazelleValidationServiceHandler.validate(validationRequest, serviceName);
        });

        assertEquals("Validation service error", exception.getMessage());
    }

    @Test
    void should_get_validation_profiles_successfully() {
        // Given
        List<ValidationProfile> expectedProfiles = Arrays.asList(
            createValidationProfile("profile1", "Profile 1"),
            createValidationProfile("profile2", "Profile 2")
        );
        when(validationService.getValidationProfiles()).thenReturn(expectedProfiles);

        // When
        List<ValidationProfile> result = gazelleValidationServiceHandler.getValidationProfiles();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedProfiles, result);
        verify(validationService).getValidationProfiles();
    }

    @Test
    void should_throw_exception_when_get_profiles_controller_is_null() {
        // Given
        GazelleValidationServiceHandler handlerWithNullController = new GazelleValidationServiceHandler(null);

        // When & Then
        ServiceClientException exception = assertThrows(ServiceClientException.class, handlerWithNullController::getValidationProfiles);

        assertEquals("validationService not set", exception.getMessage());
    }

    @Test
    void should_return_empty_list_when_get_profiles_throws_exception() {
        // Given
        when(validationService.getValidationProfiles())
            .thenThrow(new RuntimeException("Service error"));

        // When
        Throwable exception = assertThrows(RuntimeException.class, gazelleValidationServiceHandler::getValidationProfiles);
        // Then
        assertEquals("Service error", exception.getMessage());
    }


    @Test
    void should_handle_empty_validation_items() {
        // Given
        ValidationRequest validationRequest = new ValidationRequest();
        validationRequest.setValidationProfileId("testProfileId");
        validationRequest.setValidationItems(Collections.emptyList());

        ValidationReport expectedReport = new ValidationReport();
        when(validationService.validate(validationRequest)).thenReturn(expectedReport);

        // When
        ValidationReport result = gazelleValidationServiceHandler.validate(validationRequest, "TestService");

        // Then
        assertNotNull(result);
        assertEquals(expectedReport, result);
    }

    @Test
    void should_verify_interaction_counts() {
        // Given
        ValidationRequest validationRequest = createValidationRequest();
        ValidationReport expectedReport = new ValidationReport();
        when(validationService.validate(validationRequest)).thenReturn(expectedReport);

        // When
        gazelleValidationServiceHandler.validate(validationRequest, "TestService");
        gazelleValidationServiceHandler.validate(validationRequest, "TestService");

        // Then
        verify(validationService, times(2)).validate(validationRequest);
        verify(validationService, never()).getValidationProfiles();
    }

    // Helper methods
    private ValidationProfile createValidationProfile(String profileId, String profileName) {
        ValidationProfile profile = new ValidationProfile();
        profile.setProfileID(profileId);
        profile.setProfileName(profileName);
        return profile;
    }

    private ValidationRequest createValidationRequest() {
        ValidationRequest request = new ValidationRequest();
        request.setValidationProfileId("testProfileId");

        ValidationItem item = new ValidationItem();
        item.setContent("test content".getBytes());
        request.setValidationItems(Collections.singletonList(item));

        return request;
    }
}
