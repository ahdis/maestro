package net.ihe.gazelle.maestro.validation.step.business;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.spi.business.StepRun;
import net.ihe.gazelle.validation.v2.api.business.UnknownValidationProfileException;
import net.ihe.gazelle.validation.v2.api.business.profile.SupportedInput;
import net.ihe.gazelle.validation.v2.api.business.profile.ValidationProfile;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationTestResult;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationStepExecutorTest {

   @Mock
   private ValidationHandler handler;

   @Mock
   private ReportSerializer serializer;

   @Test
   void shouldBuildValidationRequestAndPersistSerializedReport() {
      ValidationStepExecutor executor = new ValidationStepExecutor(handler, serializer);
      StepRun stepRun = stepRunWithContent("payload".getBytes(StandardCharsets.UTF_8));
      ValidationProfile profile = new ValidationProfile()
            .setProfileID("PROFILE")
            .addSupportedInput(new SupportedInput()
                  .setId(ValidationStepDefinition.CONTENT_TO_VALIDATE)
                  .setRequired(true));
      when(handler.getValidationProfiles()).thenReturn(List.of(profile));
      ValidationReport validationReport = new ValidationReport().setOverallResult(ValidationTestResult.PASSED);
      when(handler.validate(any(ValidationRequest.class))).thenReturn(validationReport);
      byte[] serialized = "{\"status\":\"ok\"}".getBytes(StandardCharsets.UTF_8);
      when(serializer.toByteArray(validationReport)).thenReturn(serialized);

      StepRunReport report = executor.execute(stepRun).join();

      assertEquals(StepResult.PASSED, report.getResult());
      ByteArrayProperty output = (ByteArrayProperty) report.getOutputs().getFirst();
      assertArrayEquals(serialized, output.getValue());
      assertEquals("validation-report.json", output.getFileName());

      ArgumentCaptor<ValidationRequest> requestCaptor = ArgumentCaptor.forClass(ValidationRequest.class);
      verify(handler).validate(requestCaptor.capture());
      ValidationRequest sentRequest = requestCaptor.getValue();
      assertEquals("PROFILE", sentRequest.getValidationProfileId());
      assertEquals(1, sentRequest.getInputs().size());
      assertEquals(ValidationStepDefinition.CONTENT_TO_VALIDATE, sentRequest.getInputs().getFirst().getId());
   }

   @Test
   void shouldFailWithUnknownProfileWhenNoMatchingProfileExists() {
      ValidationStepExecutor executor = new ValidationStepExecutor(handler, serializer);
      StepRun stepRun = stepRunWithContent("content".getBytes(StandardCharsets.UTF_8));
      when(handler.getValidationProfiles())
            .thenReturn(List.of(new ValidationProfile().setProfileID("OTHER_PROFILE")));

      assertThrows(UnknownValidationProfileException.class, () -> executor.execute(stepRun));
   }

   private StepRun stepRunWithContent(byte[] content) {
      Step step = new Step()
            .setName("Validation")
            .setType(ValidationStepDefinition.TYPE)
            .setProperties(List.of(
                  new StringProperty(ValidationStepDefinition.VALIDATION_SERVICE, "MATCHBOX"),
                  new StringProperty(ValidationStepDefinition.VALIDATION_PROFILE, "PROFILE"),
                  new ByteArrayProperty(ValidationStepDefinition.CONTENT_TO_VALIDATE, content)
            ));
      return new StepRun(step, List.of());
   }
}
