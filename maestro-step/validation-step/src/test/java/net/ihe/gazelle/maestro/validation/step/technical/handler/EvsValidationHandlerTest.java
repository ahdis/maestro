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
import net.ihe.gazelle.evsapi.client.business.Validator;
import net.ihe.gazelle.evsapi.client.business.request.HandledObject;
import net.ihe.gazelle.evsapi.client.business.request.ValidationRequest;
import net.ihe.gazelle.evsapi.client.business.response.report.ValidationOverview;
import net.ihe.gazelle.evsapi.client.business.response.report.ValidationReport;
import net.ihe.gazelle.evsapi.client.business.response.report.ValidationTestResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvsValidationHandlerTest {

    @Mock
    private ValidationClient mockValidationClient;

    private EvsValidationHandler evsValidationHandler;



    @BeforeEach
    void setUp() {
        evsValidationHandler = new EvsValidationHandler(mockValidationClient);
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
        List<ValidationServiceProfile> profiles = createValidationServiceProfile();
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
        ValidationRequest validationRequest = createValidationRequest();
        String urlLocation = "http://example.com/validation/12345";
        String expectedOid = "12345";

        when(mockValidationClient.validate(any(ValidationRequest.class))).thenReturn(urlLocation);
        when(mockValidationClient.getValidationReportByOid(expectedOid)).thenReturn(getExpectedEvsReport());

        // When
        ValidationReport result = evsValidationHandler.validate(validationRequest);

        // Then
        assertNotNull(result);
        verify(mockValidationClient).validate(any(ValidationRequest.class));
        verify(mockValidationClient).getValidationReportByOid(expectedOid);
        assertEquals("662d1d93-5f2a-43a2-91d7-55b15d6cdbcc", result.getUuid());
        assertEquals("Gazelle HL7v3 Validator", result.getValidationOverview().getValidationServiceName());
        assertEquals("3.9.2", result.getValidationOverview().getValidationServiceVersion());
        assertEquals(ValidationTestResult.FAILED, result.getValidationOverview().getValidationOverallResult());
    }

    @Test
    void should_throw_exception_when_validation_client_is_null() {
        // Given
        EvsValidationHandler handlerWithNullClient = new EvsValidationHandler(null);
        ValidationRequest validationRequest = createValidationRequest();

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            handlerWithNullClient.validate(validationRequest);
        });

        assertEquals("Validation client is not set", exception.getMessage());
    }

    @Test
    void should_extract_oid_from_url_location() {
        // Given
        String urlLocation = "http://example.com/validation/report/abc123def";
        String expectedOid = "abc123def";


        when(mockValidationClient.validate(any(ValidationRequest.class))).thenReturn(urlLocation);
        when(mockValidationClient.getValidationReportByOid(expectedOid)).thenReturn(getExpectedEvsReport());

        // When
        ValidationReport result = evsValidationHandler.validate(createValidationRequest());

        // Then
        assertNotNull(result);
        verify(mockValidationClient).getValidationReportByOid(expectedOid);

    }

    @Test
    void should_get_validation_profiles_successfully() {
        // Given
        List<ValidationServiceProfile> serviceProfiles = createValidationServiceProfile();

        when(mockValidationClient.getValidationProfiles()).thenReturn(serviceProfiles);

        // When
        List<ValidationServiceProfile> result = evsValidationHandler.getValidationProfiles();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("profile1", result.get(0).getValidator().getKeyword());
        assertEquals("Service 1", result.get(0).getServiceName());
        assertEquals("profile2", result.get(1).getValidator().getKeyword());
        assertEquals("Service 2", result.get(1).getServiceName());
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
    void should_return_null_when_service_profiles_null() {
        // Given
        when(mockValidationClient.getValidationProfiles()).thenReturn(null);

        // When
        List<ValidationServiceProfile> result = evsValidationHandler.getValidationProfiles();

        // Then
        assertNull(result);
    }

    @Test
    void should_return_empty_list_when_service_profiles_empty() {
        // Given
        when(mockValidationClient.getValidationProfiles()).thenReturn(Collections.emptyList());

        // When
        List<ValidationServiceProfile> result = evsValidationHandler.getValidationProfiles();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Helper methods
    private List<ValidationServiceProfile> createValidationServiceProfile() {
        return List.of(
            new ValidationServiceProfile()
                .setValidator(new Validator()
                        .setName("profile1")
                        .setKeyword("profile1"))
                .setServiceName("Service 1"),
            new ValidationServiceProfile()
                .setValidator(new Validator()
                        .setName("profile2")
                        .setKeyword("profile2"))
                .setServiceName("Service 2")
        );
    }

    private ValidationReport getExpectedEvsReport() {
        ValidationReport report = new ValidationReport();
        report.setUuid("662d1d93-5f2a-43a2-91d7-55b15d6cdbcc");

        ValidationOverview overview = new ValidationOverview();
        overview.setDisclaimer("this is a test disclaimer");
        overview.setValidationServiceName("Gazelle HL7v3 Validator");
        overview.setValidationServiceVersion("3.9.2");
        overview.setValidatorID("[ITI-44] Patient Identity Feed HL7V3 - Add Patient Record");
        overview.setValidationOverallResult(ValidationTestResult.FAILED);
        report.setValidationOverview(overview);
        return  report;
    }

    private ValidationRequest createValidationRequest() {
        ValidationRequest request = new ValidationRequest();
        request.setValidationService(
                new ValidationService()
                        .setName("TestService")
                        .setValidator("testProfileId"))
                .addValidationItem(
                new HandledObject()
                        .setContent("test content".getBytes())
                        .setOriginalFileName("testFile.txt"));
        return request;
    }
}
