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

import net.ihe.gazelle.evsapi.client.business.ValidationClient;
import net.ihe.gazelle.evsapi.client.business.ValidationService;
import net.ihe.gazelle.evsapi.client.business.ValidationServiceProfile;
import net.ihe.gazelle.evsapi.client.business.request.HandledObject;
import net.ihe.gazelle.evsapi.client.business.request.ValidationRequest;
import net.ihe.gazelle.evsapi.client.business.response.report.ValidationReport;
import net.ihe.gazelle.maestro.validation.step.technical.mapper.EvsValidationReportMapper;
import net.ihe.gazelle.validation.v2.api.business.profile.ValidationProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvsValidationHandlerTest {

    @Mock
    private ValidationClient mockValidationClient;

    @Mock
    private EvsValidationReportMapper mockMapper;

    private EvsValidationHandler evsValidationHandler;

    private final ValidationReport evsReport = new ValidationReport();
    private final net.ihe.gazelle.validation.v2.api.business.report.ValidationReport expectedReport = new net.ihe.gazelle.validation.v2.api.business.report.ValidationReport();



    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        evsValidationHandler = new EvsValidationHandler(mockValidationClient);
        var field = EvsValidationHandler.class.getDeclaredField("evsValidationReportMapper");
        field.setAccessible(true);
        field.set(evsValidationHandler, mockMapper);

    }

    @Test
    void should_create_handler_with_validation_client() {
        // Given & When
        EvsValidationHandler handler = new EvsValidationHandler(mockValidationClient);

        // Then
        assertNotNull(handler);
    }

    @Test
    void should_return_true_when_validation_profiles_available() {
        // Given
        List<ValidationServiceProfile> profiles = Arrays.asList(
            createValidationServiceProfile("profile1", "Service 1"),
            createValidationServiceProfile("profile2", "Service 2")
        );
        when(mockValidationClient.getValidationProfiles()).thenReturn(profiles);

        // When
        boolean isAvailable = evsValidationHandler.isAvailable();

        // Then
        assertTrue(isAvailable);
        verify(mockValidationClient, times(2)).getValidationProfiles();    }

    @Test
    void should_return_false_when_no_validation_profiles_available() {
        // Given
        when(mockValidationClient.getValidationProfiles()).thenReturn(Collections.emptyList());

        // When
        boolean isAvailable = evsValidationHandler.isAvailable();

        // Then
        assertFalse(isAvailable);
    }

    @Test
    void should_return_false_when_validation_profiles_null() {
        // Given
        when(mockValidationClient.getValidationProfiles()).thenReturn(null);

        // When
        boolean isAvailable = evsValidationHandler.isAvailable();

        // Then
        assertFalse(isAvailable);
    }

    @Test
    void should_validate_successfully() {
        // Given
        net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest validationRequest = createValidationRequest();
        String serviceName = "TestService";
        String urlLocation = "http://example.com/validation/12345";
        String expectedOid = "12345";

        when(mockValidationClient.validate(any(ValidationRequest.class))).thenReturn(urlLocation);
        when(mockValidationClient.getValidationReportByOid(expectedOid)).thenReturn(evsReport);
        when(mockMapper.mapEvsReportToValidationReport(evsReport, validationRequest)).thenReturn(expectedReport);

        // When
        net.ihe.gazelle.validation.v2.api.business.report.ValidationReport result = evsValidationHandler.validate(validationRequest, serviceName);

        // Then
        assertNotNull(result);
        assertEquals(expectedReport, result);
        verify(mockValidationClient).validate(any(ValidationRequest.class));
        verify(mockValidationClient).getValidationReportByOid(expectedOid);
        verify(mockMapper).mapEvsReportToValidationReport(evsReport, validationRequest);
    }

    @Test
    void should_throw_exception_when_validation_client_is_null() {
        // Given
        EvsValidationHandler handlerWithNullClient = new EvsValidationHandler(null);
        net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest validationRequest = createValidationRequest();

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            handlerWithNullClient.validate(validationRequest, "TestService");
        });

        assertEquals("Validation client is not set", exception.getMessage());
    }

    @Test
    void should_extract_oid_from_url_location() {
        // Given
        net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest validationRequest = createValidationRequest();
        String serviceName = "TestService";
        String urlLocation = "http://example.com/validation/report/abc123def";
        String expectedOid = "abc123def";


        when(mockValidationClient.validate(any(ValidationRequest.class))).thenReturn(urlLocation);
        when(mockValidationClient.getValidationReportByOid(expectedOid)).thenReturn(evsReport);
        when(mockMapper.mapEvsReportToValidationReport(evsReport, validationRequest)).thenReturn(expectedReport);

        // When
        net.ihe.gazelle.validation.v2.api.business.report.ValidationReport result = evsValidationHandler.validate(validationRequest, serviceName);

        // Then
        assertNotNull(result);
        verify(mockValidationClient).getValidationReportByOid(expectedOid);
    }

    @Test
    void should_get_validation_profiles_successfully() {
        // Given
        List<ValidationServiceProfile> serviceProfiles = Arrays.asList(
            createValidationServiceProfile("profile1", "Service 1"),
            createValidationServiceProfile("profile2", "Service 2")
        );
        when(mockValidationClient.getValidationProfiles()).thenReturn(serviceProfiles);

        // When
        List<ValidationProfile> result = evsValidationHandler.getValidationProfiles();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("profile1", result.get(0).getProfileID());
        assertEquals("Service 1", result.get(0).getProfileName());
        assertEquals("profile2", result.get(1).getProfileID());
        assertEquals("Service 2", result.get(1).getProfileName());
    }

    @Test
    void should_return_empty_list_when_client_is_null() {
        // Given
        EvsValidationHandler handlerWithNullClient = new EvsValidationHandler(null);

        Throwable exception = assertThrows(IllegalStateException.class, handlerWithNullClient::getValidationProfiles);
        // Then
        assertEquals("Validation client is not set", exception.getMessage());
    }

    @Test
    void should_return_empty_list_when_service_profiles_null() {
        // Given
        when(mockValidationClient.getValidationProfiles()).thenReturn(null);

        // When
        List<ValidationProfile> result = evsValidationHandler.getValidationProfiles();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void should_return_empty_list_when_service_profiles_empty() {
        // Given
        when(mockValidationClient.getValidationProfiles()).thenReturn(Collections.emptyList());

        // When
        List<ValidationProfile> result = evsValidationHandler.getValidationProfiles();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void should_convert_validation_request_correctly() {
        // Given
        net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest validationRequest = createValidationRequest();
        String serviceName = "TestService";
        String urlLocation = "http://example.com/validation/12345";

        when(mockValidationClient.validate(any(ValidationRequest.class))).thenReturn(urlLocation);
        when(mockValidationClient.getValidationReportByOid(anyString())).thenReturn(evsReport);
        when(mockMapper.mapEvsReportToValidationReport(any(), any())).thenReturn(expectedReport);

        // When
        evsValidationHandler.validate(validationRequest, serviceName);

        // Then
        verify(mockValidationClient).validate(argThat(evsRequest -> {
            assertNotNull(evsRequest);
            assertNotNull(evsRequest.getValidationService());
            assertEquals(1, evsRequest.getHandledObjects().size());

            HandledObject handledObject = evsRequest.getHandledObjects().getFirst();
            assertEquals("test content", new String(handledObject.getContent()));

            ValidationService validationService = evsRequest.getValidationService();
            assertEquals(serviceName, validationService.getName());
            assertEquals("testProfileId", validationService.getValidator());

            return true;
        }));
    }

    // Helper methods
    private ValidationServiceProfile createValidationServiceProfile(String profileId, String serviceName) {
        ValidationServiceProfile profile = new ValidationServiceProfile();
        profile.setProfileID(profileId);
        profile.setServiceName(serviceName);
        return profile;
    }

    private net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest createValidationRequest() {
        net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest request = new net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest();
        request.setValidationProfileId("testProfileId");
        request.addValidationItem(
                new net.ihe.gazelle.validation.v2.api.business.request.ValidationItem()
                        .setContent("test content".getBytes())
                        .setItemId("testItemId")
                        .setLocation("http://example.com/test")
                        .setRole("testRole"));

        return request;
    }
}
