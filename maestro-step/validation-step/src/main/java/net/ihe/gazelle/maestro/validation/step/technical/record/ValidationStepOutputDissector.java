/*
 * Copyright 2026 IHE International.
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

package net.ihe.gazelle.maestro.validation.step.technical.record;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputPlan;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputPlanContext;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputPlanDissector;
import net.ihe.gazelle.maestro.spi.business.recording.plan.FutureReferencePlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ItemPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ReferencePlan;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStepDefinition;
import net.ihe.gazelle.modelmarshaller.technical.marshalling.MarshallingException;
import net.ihe.gazelle.modelmarshaller.technical.marshalling.TextMarshaller;
import net.ihe.gazelle.validation.v2.api.business.Input;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.v2.api.technical.dto.report.ValidationReportDTO;

import java.io.IOException;
import java.util.List;

/**
 * Dissects validation step outputs for recording purposes.
 * This class processes validation reports and extracts attachments and references.
 */
public class ValidationStepOutputDissector implements StepOutputPlanDissector {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * Constant identifier for validation input attachments.
     */
    public static final String VALIDATION_INPUT = "VALIDATION_INPUT";

    /**
     * Default constructor.
     */
    public ValidationStepOutputDissector() { /* Default constructor */ }

    @Override
    public StepOutputPlan dissect(StepOutputPlanContext context) {
        if (context == null || context.stepRunReport() == null) {
            return StepOutputPlan.empty();
        }
        StepRunReport stepRunReport = context.stepRunReport();
        List<Property> outputs = stepRunReport.getOutputs();
        if (outputs == null || outputs.isEmpty()) {
            return StepOutputPlan.empty();
        }
        ByteArrayProperty validationReportProperty =
              context.output(ValidationStepDefinition.REPORT, ByteArrayProperty.class);
        if (validationReportProperty == null) {
            return StepOutputPlan.empty();
        }
        byte[] validationReportBytes = validationReportProperty.getValue();
        ValidationReport validationReport = deserializeReport(validationReportBytes);

        ItemPlan<ValidationReport> reportPlan = new ItemPlan<>(
              ValidationStepDefinition.REPORT,
              validationReport,
              new ValidationReportMarshaller()
        );

        addInputAttachments(reportPlan, validationReport);

        FutureReferencePlan<TestReport> reportReferencePlan = new FutureReferencePlan<>(
              ReferencePlan.forItem(ValidationStepDefinition.REPORT, reportPlan),
              (testReport, refName, refId) -> context.replaceOutput(
                    ValidationStepDefinition.REPORT,
                    output -> {
                        ByteArrayItemProperty replacement = new ByteArrayItemProperty(validationReportProperty);
                        replacement.setValue(null);
                        replacement.setItemType(ValidationStepDefinition.REPORT);
                        replacement.setReference(context.datahouseUrl() + "/items/" + refId);
                        return replacement;
                    }
              )
        );

        return new StepOutputPlan(List.of(reportReferencePlan));
    }

    private ValidationReport deserializeReport(byte[] content) {
        try {
            return OBJECT_MAPPER.readValue(content, ValidationReport.class);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to deserialize ValidationReport from JSON", e);
        }
    }

    private void addInputAttachments(ItemPlan<ValidationReport> reportPlan, ValidationReport validationReport) {
        if (validationReport.getInputs() == null) {
            return;
        }
        for (Input input : validationReport.getInputs()) {
            if (input.getContent() != null) {
                String referenceName = resolveReferenceName(input.getId());
                reportPlan.addDetachedAttachment(
                      referenceName,
                      VALIDATION_INPUT,
                      input.getContent(),
                      input.getId(),
                      this::mutateInputReference
                );
                input.setContent(null);
            }
        }
    }

    private void mutateInputReference(ValidationReport report, String refName, String attachmentId) {
        if (report.getInputs() == null) {
            return;
        }
        for (Input input : report.getInputs()) {
            String candidate = resolveReferenceName(input.getId());
            if (candidate.equals(refName)) {
                input.setItemId(attachmentId);
                input.setLocation("/attachments/" + attachmentId);
                input.setContent(null);
            }
        }
    }

    private String resolveReferenceName(String candidate) {
        if (candidate == null || candidate.isBlank()) {
            return "validation-input";
        }
        return candidate;
    }

    private static final class ValidationReportMarshaller implements TextMarshaller<ValidationReport> {
        @Override
        public String marshallAsString(ValidationReport report) throws MarshallingException {
            try {
                return OBJECT_MAPPER.writeValueAsString(new ValidationReportDTO(report));
            } catch (IOException e) {
                throw new MarshallingException(e.getMessage());
            }
        }

        @Override
        public ValidationReport unmarshall(String payload) {
            throw new UnsupportedOperationException("ValidationReport marshalling is one-way for recording");
        }

        @Override
        public byte[] marshall(ValidationReport report) throws MarshallingException {
            return marshallAsString(report).getBytes();
        }

        @Override
        public ValidationReport unmarshall(byte[] payload) {
            throw new UnsupportedOperationException("ValidationReport marshalling is one-way for recording");
        }
    }
}
