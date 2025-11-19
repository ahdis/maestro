package net.ihe.gazelle.maestro.validation.step.technical.record;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.recording.MaestroAttachment;
import net.ihe.gazelle.maestro.api.business.recording.MaestroItem;
import net.ihe.gazelle.maestro.api.business.recording.MaestroRefType;
import net.ihe.gazelle.maestro.api.business.testreport.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.AttachmentOutputBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.ItemOutputBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.PropertyReferenceFactory;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.ReferenceBinding;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputContext;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputDissector;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputGraphBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputProjection;
import net.ihe.gazelle.maestro.recording.DatahouseMaestroAttachment;
import net.ihe.gazelle.maestro.recording.DatahouseMaestroItem;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStepDefinition;
import net.ihe.gazelle.maestro.validation.step.technical.record.dto.ValidationInputReferenceDTO;
import net.ihe.gazelle.maestro.validation.step.technical.record.dto.ValidationReportItemDTO;
import net.ihe.gazelle.validation.v2.api.business.Input;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ValidationStepOutputDissector implements StepOutputDissector {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    public static final String VALIDATION_INPUT = "VALIDATION_INPUT";

    @Override
    public StepOutputProjection dissect(StepOutputContext context) {
        if (context == null || context.stepRunReport() == null) {
            return StepOutputProjection.empty();
        }
        List<Property> outputs = context.stepRunReport().getOutputs();
        if (outputs == null || outputs.isEmpty()) {
            return StepOutputProjection.empty();
        }
        int validationReportIndex = findValidationReport(outputs);
        if (validationReportIndex < 0) {
            return StepOutputProjection.empty();
        }
        ByteArrayProperty validationReportProperty = (ByteArrayProperty) outputs.get(validationReportIndex);
        if (validationReportProperty == null) {
            return StepOutputProjection.empty();
        }
        byte[] validationReportBytes = validationReportProperty.getValue();
        ValidationReport validationReport = deserializeReport(validationReportBytes);
        InputAttachmentExtraction extraction = extractInputAttachments(validationReport);
        List<AttachmentOutputBuilder> inputAttachments = extraction.plans();
        StepOutputGraphBuilder graph = new StepOutputGraphBuilder();
        MaestroItem reportItem = createItem(serializeReport(validationReport, extraction.referencedInputs()), context);
        ItemOutputBuilder reportBuilder = graph.addItem(reportItem);
        //This reference will be added to the enclosing TestRunReport item later on
        reportBuilder.addReferenceBinding(
                new ReferenceBinding(
                        ValidationStepDefinition.REPORT,
                        ValidationStepDefinition.REPORT,
                        MaestroRefType.ITEM_ID
                )
        );

        //process validation inputs as attachments
        reportBuilder.addAttachments(inputAttachments);
        PropertyReferenceFactory factory = createPropertyFactory(validationReportProperty);
        reportBuilder.addPropertyReplacement(validationReportIndex, factory);

        return graph.build();
    }

    private static PropertyReferenceFactory createPropertyFactory(ByteArrayProperty validationReportProperty) {
        return (itemId, itemUrl) -> {
            ByteArrayItemProperty replacement = new ByteArrayItemProperty(validationReportProperty);
            replacement.setValue(null);
            return replacement.setReference(itemUrl);
        };
    }

    private ValidationReport deserializeReport(byte[] content) {
        try {
            return OBJECT_MAPPER.readValue(content, ValidationReport.class);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to deserialize ValidationReport from JSON", e);
        }
    }

    private String serializeReport(ValidationReport report, List<Input> referencedInputs) {
        try {
            ValidationReportItemDTO dto = new ValidationReportItemDTO(report, VALIDATION_INPUT, referencedInputs);
            return OBJECT_MAPPER.writeValueAsString(dto);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to serialize ValidationReport to JSON", e);
        }
    }

    private MaestroItem createItem(String content, StepOutputContext context) {
        return new DatahouseMaestroItem()
                .setType(ValidationStepDefinition.REPORT)
                .setContent(content)
                .setAccessControlList(context.accessControlList())
                .setReferences(new ArrayList<>());
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
        return new DatahouseMaestroAttachment()
                .setType(VALIDATION_INPUT)
                .setContent(input.getContent())
                .setFilename(input.getId());
    }

    private AttachmentOutputBuilder createAttachmentPlan(Input input) {
        MaestroAttachment attachment = createAttachmentFromInput(input);
        input.setContent(null);
        return new AttachmentOutputBuilder(attachment)
                .withReference(referenceName(input), VALIDATION_INPUT);
    }

    private String referenceName(Input input) {
        return ValidationInputReferenceDTO.resolveReferenceName(input.getId());
    }

    private record InputAttachmentExtraction(List<AttachmentOutputBuilder> plans,
                                             List<Input> referencedInputs) {
    }

    private int findValidationReport(List<Property> outputs) {
        for (int i = 0; i < outputs.size(); i++) {
            Property property = outputs.get(i);
            if (property instanceof ByteArrayProperty
                    && ValidationStepDefinition.REPORT.equals(property.getName())) {
                return i;
            }
        }
        return -1;
    }
}
