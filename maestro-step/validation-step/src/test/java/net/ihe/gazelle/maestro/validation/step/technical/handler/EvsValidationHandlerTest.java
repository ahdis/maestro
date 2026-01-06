package net.ihe.gazelle.maestro.validation.step.technical.handler;

import net.ihe.gazelle.evsapi.client.business.ValidationClient;
import net.ihe.gazelle.evsapi.client.business.ValidationServiceProfile;
import net.ihe.gazelle.evsapi.client.business.Validator;
import net.ihe.gazelle.maestro.validation.step.business.ValidationHandler;
import net.ihe.gazelle.validation.v2.api.business.profile.ValidationProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EvsValidationHandlerTest {

   private static final String SERVICE_NAME = "EVS_SERVICE";

   private final ValidationClient validationClient = mock(ValidationClient.class);
   private ValidationHandler handler;

   @BeforeEach
   void setUp() {
      handler = new EvsValidationHandler(SERVICE_NAME, validationClient);
   }

   @Test
   void isAvailableReturnsTrueWhenProfilesExist() {
      when(validationClient.getValidationProfilesByServiceName(SERVICE_NAME))
            .thenReturn(List.of(
                  new ValidationServiceProfile()
                  .setValidator(
                        new Validator().setKeyword("profile")
                  )
            ));

      assertTrue(handler.isAvailable());
   }

   @Test
   void isAvailableReturnsFalseWhenClientReturnsNull() {
      when(validationClient.getValidationProfilesByServiceName(SERVICE_NAME)).thenReturn(null);

      assertFalse(handler.isAvailable());
   }

   @Test
   void getValidationProfilesMapsEvsProfiles() {
      ValidationServiceProfile evsProfile = new ValidationServiceProfile()
            .setValidator(
                  new Validator().setKeyword("profile")
            )
            .setServiceName("EVS");
      when(validationClient.getValidationProfilesByServiceName(SERVICE_NAME))
            .thenReturn(List.of(evsProfile));

      List<ValidationProfile> profiles = handler.getValidationProfiles();

      assertNotNull(profiles);
      assertEquals(1, profiles.size());
   }

}
