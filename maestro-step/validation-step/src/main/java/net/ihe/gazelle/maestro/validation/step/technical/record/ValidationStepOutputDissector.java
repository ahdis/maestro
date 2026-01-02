package net.ihe.gazelle.maestro.validation.step.technical.record;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.spi.business.recording.*;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStepDefinition;
import net.ihe.gazelle.validation.v2.api.business.Input;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.v2.api.technical.dto.report.ValidationReportDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Dissects validation step outputs for recording purposes.
 * This class processes validation reports and extracts attachments and references.
 */
public class ValidationStepOutputDissector implements StepOutputDissector {

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
    public StepOutputProjection dissect(StepOutputContext context) {
        if (context == null || context.stepRunReport() == null) {
            return StepOutputProjection.empty();
        }
        List<Property> outputs = context.stepRunReport().getOutputs();
        if (outputs == null || outputs.isEmpty()) {
            return StepOutputProjection.empty();
        }
        ByteArrayProperty validationReportProperty = (ByteArrayProperty) outputs.stream()
                .filter(output -> ValidationStepDefinition.REPORT.equals(output.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Validation report output property not found"));
        if (validationReportProperty == null) {
            return StepOutputProjection.empty();
        }
        byte[] validationReportBytes = validationReportProperty.getValue();
        ValidationReport validationReport = deserializeReport(validationReportBytes);
        InputAttachmentExtraction extraction = extractInputAttachments(validationReport);
        List<AttachmentOutputBuilder> inputAttachments = extraction.plans();
        StepOutputProjectionBuilder graph = new StepOutputProjectionBuilder();
        MaestroItem reportItem = createItem(serializeReport(validationReport), context);
        ItemOutputBuilder reportBuilder = graph.addItem(reportItem);
        //This reference will be added to the enclosing TestRunReport item later on
        reportBuilder.addReferenceBinding(
                new ReferenceBinding(
                        ValidationStepDefinition.REPORT,
                        ValidationStepDefinition.REPORT,
                        MaestroRefType.ITEM_ID
                )
        );
        reportBuilder.mutateContent(this::mutateInputReferences);

        //process validation inputs as attachments
        reportBuilder.addAttachments(inputAttachments);
        PropertyReferenceFactory factory = createPropertyFactory(validationReportProperty);
        reportBuilder.addPropertyReplacement(ValidationStepDefinition.REPORT, outputs, factory);

        return graph.build();
    }

    private static PropertyReferenceFactory createPropertyFactory(ByteArrayProperty validationReportProperty) {
        return (itemId, itemUrl) -> {
            ByteArrayItemProperty replacement = new ByteArrayItemProperty(validationReportProperty);
            replacement.setValue(null);
            return replacement
                  .setItemType(ValidationStepDefinition.REPORT)
                  .setReference(itemUrl);
        };
    }

    private ValidationReport deserializeReport(byte[] content) {
        try {
            return OBJECT_MAPPER.readValue(content, ValidationReport.class);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to deserialize ValidationReport from JSON", e);
        }
    }

    private String serializeReport(ValidationReport report) {
        try {
            return OBJECT_MAPPER.writeValueAsString(new ValidationReportDTO(report));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to serialize ValidationReport to JSON", e);
        }
    }

    private MaestroItem createItem(String content, StepOutputContext context) {
        return new MaestroItem(
              context.accessControlList(),
              ValidationStepDefinition.REPORT,
              content,
              new ArrayList<>(),
              new HashMap<>()
        );
    }

    private InputAttachmentExtraction extractInputAttachments(ValidationReport validationReport) {
        List<AttachmentOutputBuilder> plans = new ArrayList<>();
        List<Input> referencedInputs = new ArrayList<>();
        if (validationReport.getInputs() == null) {
            return new InputAttachmentExtraction(plans, referencedInputs);
        }
        for (Input input : validationReport.getInputs()) {
            if (input.getContent() != null) {
                plans.add(createAttachmentPlan(input));
                referencedInputs.add(input);
            }
        }
        return new InputAttachmentExtraction(plans, referencedInputs);
    }

    private MaestroAttachment createAttachmentFromInput(Input input) {
        if(input == null || input.getContent() == null) {
            throw new IllegalArgumentException("Input or input content cannot be null");
        }
        return new MaestroAttachment(
              input.getContent(),
              VALIDATION_INPUT,
              input.getId()
        );
    }

    private AttachmentOutputBuilder createAttachmentPlan(Input input) {
        MaestroAttachment attachment = createAttachmentFromInput(input);
        input.setContent(null);
        return new AttachmentOutputBuilder(attachment)
                .withReference(referenceName(input), VALIDATION_INPUT);
    }

    private String referenceName(Input input) {
        return resolveReferenceName(input.getId());
    }

    private String mutateInputReferences(String content, ContentMutationContext context) {
        ValidationReport mutatedReport = deserializeReportContent(content);
        if (mutatedReport.getInputs() != null) {
            for (Input input : mutatedReport.getInputs()) {
                String refName = resolveReferenceName(input.getId());
                String attachmentId = context.findAttachmentId(refName);
                if (attachmentId != null) {
                    input.setItemId(attachmentId);
                    input.setLocation("/attachments/" + attachmentId);
                    input.setContent(null);
                }
            }
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(new ValidationReportDTO(mutatedReport));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to mutate validation report content", e);
        }
    }

    private ValidationReport deserializeReportContent(String content) {
        try {
            ValidationReportDTO dto = OBJECT_MAPPER.readValue(content, ValidationReportDTO.class);
            return dto.getBusinessObject();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to deserialize validation report content", e);
        }
    }

    private String resolveReferenceName(String candidate) {
        if (candidate == null || candidate.isBlank()) {
            return "validation-input";
        }
        return candidate;
    }

    private record InputAttachmentExtraction(List<AttachmentOutputBuilder> plans,
                                             List<Input> referencedInputs) {
    }


}
