package net.ihe.gazelle.maestro.quarkus.record;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kereval.gazelle.datahouse.api.business.record.Attachment;
import com.kereval.gazelle.datahouse.api.business.record.Item;
import com.kereval.gazelle.datahouse.api.business.record.RefType;
import com.kereval.gazelle.datahouse.api.business.record.Reference;
import com.kereval.gazelle.datahouse.technical.rest.client.RecordItemClient;
import com.kereval.gazelle.datahouse.technical.rest.client.RecordItemClientProvider;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.testreport.*;
import net.ihe.gazelle.maestro.engine.business.TestReportRecordException;
import net.ihe.gazelle.maestro.engine.business.TestReportRecordService;
import net.ihe.gazelle.maestro.quarkus.record.entity.TestReportEntity;
import net.ihe.gazelle.maestro.quarkus.record.entity.TestReportReference;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class DatahouseTestReportRecordService implements TestReportRecordService {

    public static final String TEST_REPORT_TYPE = "TEST_REPORT";
    public static final String BYTE_ARRAY_TYPE = "BYTE_ARRAY";
    private final RecordItemClient recordItemClient;

    private final String datahouseUrl;

    @Inject
    public DatahouseTestReportRecordService(@ConfigProperty(name = "datahouse.url") String datahouseUrl) {
        this.recordItemClient = new RecordItemClientProvider().createClient();
        this.datahouseUrl = datahouseUrl;
    }

    // Convenience constructor for tests to bypass config-backed client creation
    DatahouseTestReportRecordService(RecordItemClient recordItemClient, @ConfigProperty(name = "datahouse.url") String datahouseUrl) {
        this.recordItemClient = recordItemClient;
        this.datahouseUrl = datahouseUrl;
    }

    /**
     * Pre-upload input attachments and update the report to reference them; record step outputs;
     * persist sub-reports; then persist the main TestReport with References[] including inputs and sub-reports.
     *
     * @return the created item ID of the main TestReport
     */
    @Override
    public ItemReference recordTestReportWithReferences(TestReport report) {
        // 1) Upload all inputs as attachments and set refs (URL in report, ID in Reference)
        List<Reference> attachmentRefs = createAttachmentsFromTestRunInputs(report.getTestRunReports());

        // 2) Record step outputs and collect their item references (persist once)
        List<Reference> outputItemRefs = recordTestStepOutputs(report.getTestRunReports(), report.getAccessControlList());

        // 3) Persist sub-reports and collect their references
        List<TestReportReference> subReportRefs = recordSubReports(report);

        // 4) Persist the main TestReport with references to inputs, outputs, and sub-reports
        return recordTestReport(report, subReportRefs, attachmentRefs, outputItemRefs);
    }

    /**
     * For each TestRunReport input having content, upload it as an Attachment via the
     * Datahouse API and set the Item reference to the returned attachment ID.
     * Content is cleared afterward to respect content XOR reference rule.
     */
    private List<Reference> createAttachmentsFromTestRunInputs(List<TestRunReport> testRunReports) {
        List<Reference> attachmentRefs = new ArrayList<>();
        if (testRunReports == null || testRunReports.isEmpty()) {
            return attachmentRefs;
        }
        for (TestRunReport testRunReport : testRunReports) {
            processInputsForAttachments(testRunReport.getInputs(), attachmentRefs);
        }
        return attachmentRefs;
    }

    private void processInputsForAttachments(List<Property> inputs, List<Reference> attachmentRefs) {
        if (inputs == null || inputs.isEmpty()) {
            return;
        }
        UploadPlan plan = buildInputUploadPlan(inputs);
        if (plan.toUpload.isEmpty()) {
            return;
        }
        List<String> ids = recordItemClient.uploadAttachments(plan.toUpload);
        validateUploadedIdsCount(plan.toUpload.size(), ids.size());
        mapIdsBackToInputs(inputs, plan.inputIndexes, ids, attachmentRefs);
    }

    private UploadPlan buildInputUploadPlan(List<Property> inputs) {
        List<Attachment> toUpload = new ArrayList<>();
        List<Integer> inputIndexes = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            Property input = inputs.get(i);
            if (shouldUploadAsAttachment(input)) {
                toUpload.add(createAttachment(input.getName(), input.getValue()));
                inputIndexes.add(i);
            }
        }
        return new UploadPlan(toUpload, inputIndexes);
    }

    private boolean shouldUploadAsAttachment(Property input) {
        return input instanceof ByteArrayProperty;
    }

    private void validateUploadedIdsCount(int expected, int actual) {
        if (expected != actual) {
            throw new IllegalStateException("Mismatch between uploaded attachments and returned IDs");
        }
    }

    private void mapIdsBackToInputs(List<Property> inputs, List<Integer> inputIndexes, List<String> ids, List<Reference> attachmentRefs) {
        for (int k = 0; k < inputIndexes.size(); k++) {
            int idx = inputIndexes.get(k);
            String attachmentId = ids.get(k);
            Property input = inputs.get(idx);
            ByteArrayItemProperty itemProperty = new ByteArrayItemProperty(input.getName(), null);
            itemProperty.setReference(constructPathToAttachment(attachmentId));
            inputs.set(idx, itemProperty);
            attachmentRefs.add(new Reference()
                    .setName(input.getName())
                    .setType(BYTE_ARRAY_TYPE)
                    .setValue(attachmentId)
                    .setRefType(RefType.ATTACHMENT)
            );
        }
    }

    private record UploadPlan(List<Attachment> toUpload, List<Integer> inputIndexes) {
    }

    private List<Reference> recordTestStepOutputs(List<TestRunReport> testRunReports, AccessControlList acl) {
        List<Reference> outputRefs = new ArrayList<>();
        if (testRunReports == null) return outputRefs;
        for (TestRunReport testRunReport : testRunReports) {
            if (testRunReport.getStepRunReports() == null) continue;
            for (StepRunReport stepRunReport : testRunReport.getStepRunReports()) {
                List<Property> outputs = stepRunReport.getOutputs();
                outputRefs.addAll(recordOutputs(outputs, acl));
            }
        }
        return outputRefs;
    }

    private ItemReference recordTestReport(TestReport testReport, List<TestReportReference> testReportReferences, List<Reference> attachmentRefs, List<Reference> outputItemRefs) {
        try {
            // Build entity with sub-report references
            TestReportEntity entity = new TestReportEntity(testReport);
            entity.setSubReportsReferences(testReportReferences);

            // Build Datahouse item content (JSON) and references array
            ObjectMapper mapper = new ObjectMapper();
            String jsonContent = mapper.writeValueAsString(entity);

            // Compose references: inputs (attachments) + subreport item IDs + output item IDs
            List<Reference> references = new ArrayList<>();
            if (attachmentRefs != null && !attachmentRefs.isEmpty()) {
                references.addAll(attachmentRefs);
            }

            if (testReportReferences != null) {
                for (TestReportReference ref : testReportReferences) {
                    references.add(new Reference()
                            .setName(ref.getTestSuiteName())
                            .setType(TEST_REPORT_TYPE)
                            .setValue(ref.getTestReportId())
                            .setRefType(RefType.ITEM_ID)
                    );
                }
            }

            if (outputItemRefs != null && !outputItemRefs.isEmpty()) {
                references.addAll(outputItemRefs);
            }

            com.kereval.gazelle.datahouse.api.business.record.Item item =
                    new com.kereval.gazelle.datahouse.api.business.record.Item()
                            .setAccessControlList(testReport.getAccessControlList())
                            .setType(TEST_REPORT_TYPE)
                            .setContent(jsonContent)
                            .setReferences(references);

           String itemId = recordItemClient.recordItem(item);
           return new ItemReference(itemId, constructUrlToDatahouse(itemId));
        } catch (Exception e) {
            throw new TestReportRecordException("Failed to record TestReport", e);
        }
    }

    private List<TestReportReference> recordSubReports(TestReport report) {
        List<TestReportReference> subRefs = new ArrayList<>();
        if (report.getSubReports() == null) return subRefs;
        for (TestReport sub : report.getSubReports()) {
            // Reuse full flow for each sub-report
            ItemReference itemRef = recordTestReportWithReferences(sub);
            TestReportReference ref = new TestReportReference();
            ref.setTestReportId(itemRef.id());
            ref.setResult(sub.getResult());
            ref.setTestSuiteName(sub.getTestSuiteName());
            subRefs.add(ref);
        }
        return subRefs;
    }

    private List<Reference> recordOutputs(List<Property> outputs, AccessControlList acl) {
        List<Reference> references = new ArrayList<>();
        List<ByteArrayProperty> nonJsonOutputs = new ArrayList<>();
        if (outputs == null || outputs.isEmpty()) return references;
        for (Property output : outputs) {
            if (output == null) continue;
            if (shouldUploadAsAttachment(output)) {
                nonJsonOutputs.add((ByteArrayProperty) output);
            } else {
                recordOutputAsItem(acl, output, references);
            }
        }
        // Batch record non-JSON outputs as attachments
        if (!nonJsonOutputs.isEmpty()) {
            recordOutputsAsAttachments(nonJsonOutputs, references);
        }
        return references;
    }

    private void recordOutputAsItem(AccessControlList acl, Property output, List<Reference> references) {
        Item datahouseItem = buildDatahouseItemFromOutput(output, acl);
        String itemId = recordItemClient.recordItem(datahouseItem);
        output = new ByteArrayItemProperty(output.getName(), null)
                .setReference(constructUrlToDatahouse(itemId));
        references.add(new Reference()
                .setType(output.getName())
                .setValue(itemId)
                .setRefType(RefType.ITEM_ID)
        );
    }

    private void recordOutputsAsAttachments(List<ByteArrayProperty> outputs, List<Reference> references) {
        UploadPlan plan = buildOutputUploadPlan(outputs);
        if (plan.toUpload.isEmpty()) {
            return;
        }
        List<String> ids = recordItemClient.uploadAttachments(plan.toUpload);
        validateUploadedIdsCount(plan.toUpload.size(), ids.size());
        mapIdsBackToOutputs(outputs, plan.inputIndexes, ids, references);
    }

    private Item buildDatahouseItemFromOutput(Property maestroOutput, AccessControlList acl) {
        String content = maestroOutput.getValue() != null
                ? new String(maestroOutput.getValue(), StandardCharsets.UTF_8)
                : "";
        return new Item()
                .setAccessControlList(acl)
                .setType(maestroOutput.getName())
                .setContent(content);
    }

    private Attachment createAttachment(String name, byte[] content) {
        return new Attachment()
                .setType(name)
                .setContent(content)
                .setType(BYTE_ARRAY_TYPE)
                ;
    }

    private String constructUrlToDatahouse(String itemId) {
        return datahouseUrl + "/items/" + itemId;
    }

    /**
     * This is just a path as we cannot construct a full URL here, because the Item ID is not known
     * until the full TestReport is recorded.
     */
    private String constructPathToAttachment(String attachmentId) {
        return "/attachments/" + attachmentId;
    }


    private UploadPlan buildOutputUploadPlan(List<ByteArrayProperty> outputs) {
        List<Attachment> toUpload = new ArrayList<>();
        List<Integer> outputIndexes = new ArrayList<>();
        for (int i = 0; i < outputs.size(); i++) {
            Property output = outputs.get(i);
            toUpload.add(createAttachment(output.getName(), output.getValue()));
            outputIndexes.add(i);
        }
        return new UploadPlan(toUpload, outputIndexes);
    }

    private void mapIdsBackToOutputs(List<ByteArrayProperty> outputs, List<Integer> outputIndexes, List<String> ids, List<Reference> attachmentRefs) {
        for (int k = 0; k < outputIndexes.size(); k++) {
            int idx = outputIndexes.get(k);
            String attachmentId = ids.get(k);
            Property output = outputs.get(idx);
            ByteArrayItemProperty itemProperty = new ByteArrayItemProperty(output.getName(), output.getValue());
            itemProperty.setReference(constructPathToAttachment(attachmentId));
            outputs.set(idx, new ByteArrayItemProperty(output.getName(), output.getValue()));
            attachmentRefs.add(new Reference()
                    .setName(itemProperty.getName())
                    .setType(itemProperty.getMimeType())
                    .setValue(attachmentId)
                    .setRefType(RefType.ATTACHMENT)
            );
        }
    }

}
