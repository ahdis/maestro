/*
 * Copyright 2025-2026 IHE International.
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

package net.ihe.gazelle.maestro.userinteraction.step.business;

import net.ihe.gazelle.maestro.api.business.message.UserInteractionCompleted;
import net.ihe.gazelle.maestro.api.business.property.*;
import net.ihe.gazelle.maestro.api.business.test.*;
import net.ihe.gazelle.maestro.api.business.testreport.*;
import net.ihe.gazelle.maestro.api.business.userinteract.stub.UserInteractionHandlerStub;
import net.ihe.gazelle.maestro.spi.business.StepRun;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class UserInteractionStepExecutorTest {
    private StepRunReport execute(List<SupportedInput> fields, Property... values) {
        fields.forEach(field -> field.setLabel(field.getId()));
        UserInteractionStepExecutor executor = new UserInteractionStepExecutor(new UserInteractionHandlerStub() {
            @Override
            public UserInteractionCompleted displayMessage(String title, String message, long timeout,
                                                            List<SupportedInput> supportedInputs) {
                return new UserInteractionCompleted().addInputs(List.of(values));
            }
        });
        return executor.execute(new StepRun(new Step().setName("Interaction").setType("USER_INTERACTION")
                .setProperties(List.of(new StringProperty("interactionTitle", "Patient"),
                        new StringProperty("message", "Enter values"),
                        new SupportedInputsProperty("interactionInputs", fields))), List.of()));
    }

    @Test
    void exposesTypedOutputsAndPreservesIndependentInputs() {
        var file = new ByteArrayProperty("document", new byte[]{1, 2}).setFileName("patient.xml").setMimeType("text/xml");
        var report = execute(List.of(new SupportedTextInput().setId("patientId").setRequired(true),
                        new SupportedBooleanInput().setId("consent").setRequired(true),
                        new SupportedFileInput().setId("document"), new SupportedValueSetInput().setValueSetId("choices").setId("choice")),
                new StringProperty("patientId", "123"), new BooleanProperty("consent", false), file,
                new StringProperty("choice", "A"));
        assertEquals(StepResult.DONE, report.getResult());
        assertEquals(4, report.getOutputs().size());
        assertEquals(false, report.getOutput("consent").getValue());
        ByteArrayProperty outputFile = report.getOutput("document");
        assertEquals("patient.xml", outputFile.getFileName());
        assertEquals("text/xml", outputFile.getMimeType());
        for (int index = 0; index < report.getOutputs().size(); index++) {
            assertNotSame(report.getInputs().get(index), report.getOutputs().get(index));
        }
    }

    @Test
    void outputsResolveAsDownstreamReferences() {
        var report = execute(List.of(new SupportedTextInput().setId("patientId"), new SupportedFileInput().setId("document")),
                new StringProperty("patientId", "123"), new ByteArrayProperty("document", new byte[]{1, 2}));
        Map<String, String> mappings = Map.of("patientId", "selectedPatientId", "document", "documentToValidate");
        var context = report.getOutputs().stream().map(output -> output.copy().setName(mappings.get(output.getName()))).toList();
        var consumer = new StepRun(new Step().setProperties(List.of(
                new StringProperty("patientId", "${selectedPatientId}"),
                new ByteArrayProperty("file", "${documentToValidate}"))), context);
        assertEquals("123", consumer.getPropertyValue("patientId"));
        assertArrayEquals(new byte[]{1, 2}, consumer.getPropertyValue("file"));
    }

    @Test
    void acknowledgmentWithoutConfiguredInputsProducesNoOutputs() {
        UserInteractionStepExecutor executor = new UserInteractionStepExecutor(new UserInteractionHandlerStub());
        StepRunReport report = executor.execute(new StepRun(new Step()
                .setName("Acknowledgment")
                .setType(UserInteractionStepDefinition.TYPE)
                .setProperties(List.of(
                        new StringProperty(UserInteractionStepDefinition.INTERACTION_TITLE, "Information"),
                        new StringProperty(UserInteractionStepDefinition.MESSAGE, "Continue"))), List.of()));
        assertEquals(StepResult.DONE, report.getResult());
        assertTrue(report.getInputs().isEmpty());
        assertTrue(report.getOutputs().isEmpty());
    }

    @Test
    void optionalMissingFieldsAndAcknowledgmentProduceNoOutputs() {
        assertTrue(execute(List.of()).getOutputs().isEmpty());
        assertTrue(execute(List.of(new SupportedTextInput().setId("optional"))).getOutputs().isEmpty());
    }

    @Test
    void reusesDefinitionValidatorsBeforeCollectingAnswers() {
        assertThrows(IllegalArgumentException.class, () -> new UserInteractionResponseValidator(
                List.of(new SupportedTextInput().setId("patientId"))));
        assertThrows(IllegalArgumentException.class, () -> new UserInteractionResponseValidator(
                List.of(new SupportedTextInput().setId("choice").setLabel("Choice")
                        .setPossibleValues(List.of("")))));
        assertThrows(IllegalArgumentException.class, () -> new UserInteractionResponseValidator(
                List.of(new SupportedValueSetInput().setId("choice").setLabel("Choice"))));
    }

    @Test
    void preservesEmptyOptionalInputsAndFiltersOnlyOutputs() {
        var report = execute(List.of(new SupportedTextInput().setId("empty"),
                        new SupportedTextInput().setId("blank"), new SupportedBooleanInput().setId("consent")),
                new StringProperty("empty", null), new StringProperty("blank", " "),
                new BooleanProperty("consent", false));
        assertEquals(3, report.getInputs().size());
        assertNull(report.getInputs().get(0).getValue());
        assertEquals(" ", report.getInputs().get(1).getValue());
        assertEquals(1, report.getOutputs().size());
        assertEquals("consent", report.getOutputs().getFirst().getName());
        assertEquals(false, report.getOutputs().getFirst().getValue());
    }

    @Test
    void validatesConfiguredSelectionsAndDuplicateEmptyAnswers() {
        SupportedTextInput selection = new SupportedTextInput().setId("choice").setPossibleValues(List.of("A", "B"));
        assertThrows(IllegalArgumentException.class, () -> execute(List.of(selection), new StringProperty("choice", "C")));
        assertThrows(IllegalArgumentException.class, () -> execute(List.of(selection),
                new StringProperty("choice", null), new StringProperty("choice", null)));
        assertTrue(execute(List.of(selection), new StringProperty("choice", " ")).getOutputs().isEmpty());
    }

    @Test
    void rejectsMissingRequiredUnknownDuplicateAndWronglyTypedValues() {
        List<SupportedInput> fields = List.of(new SupportedTextInput().setId("patientId").setRequired(true));
        assertThrows(IllegalArgumentException.class, () -> execute(fields));
        assertThrows(IllegalArgumentException.class, () -> execute(fields, new StringProperty("other", "123")));
        assertThrows(IllegalArgumentException.class, () -> execute(fields, new BooleanProperty("patientId", true)));
        assertThrows(IllegalArgumentException.class, () -> execute(fields, new StringProperty("patientId", "")));
        assertThrows(IllegalArgumentException.class, () -> execute(fields, new StringProperty("patientId", "123"), new StringProperty("patientId", "456")));
        assertThrows(IllegalArgumentException.class, () -> execute(List.of(fields.getFirst(), fields.getFirst())));
    }
}
