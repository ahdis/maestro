package net.ihe.gazelle.maestro.validation.step.technical.handler;

import net.ihe.gazelle.validation.v2.api.business.ValidationService;
import net.ihe.gazelle.validation.v2.api.business.profile.ValidationProfile;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GazelleValidationServiceHandlerTest {

   private final ValidationService validationService = mock(ValidationService.class);
   private GazelleValidationServiceHandler handler;

   @BeforeEach
   void setUp() {
      handler = new GazelleValidationServiceHandler(validationService);
   }

   @Test
   void isAvailableDelegatesToValidationService() {
      when(validationService.getValidationProfiles()).thenReturn(List.of(mock(ValidationProfile.class)));

      assertEquals(true, handler.isAvailable());
      verify(validationService).getValidationProfiles();
   }

   @Test
   void validateReturnsReport() {
      ValidationRequest request = new ValidationRequest();
      ValidationReport report = new ValidationReport();
      when(validationService.validate(request)).thenReturn(report);

      ValidationReport result = handler.validate(request);

      assertEquals(report, result);
      verify(validationService).validate(request);
   }

   @Test
   void getValidationProfilesReturnsResults() {
      List<ValidationProfile> profiles = List.of(mock(ValidationProfile.class));
      when(validationService.getValidationProfiles()).thenReturn(profiles);

      assertEquals(profiles, handler.getValidationProfiles());
   }

   @Test
   void constructorRejectsNullValidationService() {
      assertThrows(IllegalArgumentException.class, () -> new GazelleValidationServiceHandler(null));
   }

   @Test
   void validateThrowsWhenRequestIsNull() {
      assertThrows(IllegalArgumentException.class, () -> handler.validate(null));
   }
}
