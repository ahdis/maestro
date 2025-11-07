package net.ihe.gazelle.maestro.validation.step.technical.record;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kereval.gazelle.datahouse.api.business.record.Attachment;
import com.kereval.gazelle.datahouse.api.business.record.Item;
import com.kereval.gazelle.datahouse.api.business.record.RefType;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.testreport.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.*;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStepDefinition;
import net.ihe.gazelle.validation.v2.api.business.Input;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ValidationStepOutputDissector implements StepOutputDissector {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
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
        List<AttachmentPlan> inputAttachments = extractInputAttachments(validationReport);
        StepOutputProjectionBuilder builder = new StepOutputProjectionBuilder();
        Item reportItem = createItem(serializeReport(validationReport), context);
        ItemHandle handle = builder.addItem(reportItem);
        builder.addReportBinding(
                handle,
                new ReportReferenceBinding(
                        ValidationStepDefinition.REPORT,
                        ValidationStepDefinition.REPORT,
                        RefType.ITEM_ID
                )
        );

        //process validation inputs as attachments
        inputAttachments.forEach(builder::addAttachment);
        PropertyReferenceFactory factory = createPropertyFactory(validationReportProperty);
        builder.addPropertyMutation(validationReportIndex, handle, factory);

        return builder.build();
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

    private String serializeReport(ValidationReport report) {
        try {
            return OBJECT_MAPPER.writeValueAsString(report);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to serialize ValidationReport to JSON", e);
        }
    }

    private Item createItem(String content, StepOutputContext context) {
        return new Item()
                .setType(ValidationStepDefinition.REPORT)
                .setContent(content)
                .setAccessControlList(context.accessControlList())
                .setReferences(new ArrayList<>());

    }

    private List<AttachmentPlan> extractInputAttachments(ValidationReport validationReport) {
        List<AttachmentPlan> plans = new ArrayList<>();
        if (validationReport.getInputs() == null) {
            return plans;
        }
        for (Input input : validationReport.getInputs()) {
            if (input.getContent() != null) {
                plans.add(createAttachmentPlan(input));
            }
        }
        return plans;
    }

    private Attachment createAttachmentFromInput(Input input) {
        if(input == null || input.getContent() == null) {
            throw new IllegalArgumentException("Input or input content cannot be null");
        }
        return new Attachment()
                .setType(VALIDATION_INPUT)
                .setContent(input.getContent())
                .setFilename(input.getId());
    }

    private AttachmentPlan createAttachmentPlan(Input input) {
        Attachment attachment = createAttachmentFromInput(input);
        input.setContent(null);
        return AttachmentPlan.detached(attachment, input.getId(), VALIDATION_INPUT);
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
