package net.ihe.gazelle.maestro.quarkus.record;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kereval.gazelle.datahouse.api.business.record.Attachment;
import com.kereval.gazelle.datahouse.api.business.record.RefType;
import com.kereval.gazelle.datahouse.api.business.record.Reference;
import com.kereval.gazelle.datahouse.technical.rest.client.RecordItemClient;
import com.kereval.gazelle.datahouse.technical.rest.client.RecordItemClientProvider;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testreport.Item;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.maestro.business.TestReportRecordException;
import net.ihe.gazelle.maestro.business.TestReportRecordService;
import net.ihe.gazelle.maestro.quarkus.record.entity.TestReportEntity;
import net.ihe.gazelle.maestro.quarkus.record.entity.TestReportReference;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequestScoped
public class DatahouseTestReportRecordService implements TestReportRecordService {

    public static final String TEST_REPORT = "TEST_REPORT";
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
    public String recordTestReportWithReferences(TestReport report) {
        // 1) Upload all inputs as attachments and set refs (URL in report, ID in Reference)
        List<Reference> attachmentRefs = createAttachmentsFromTestRunInputs(report.getTestRunReports());

        // 2) Record step outputs and collect their item references (persist once)
        List<Reference> outputItemRefs = recordTestStepOutputs(report.getTestRunReports());

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

    private void processInputsForAttachments(List<Item> inputs, List<Reference> attachmentRefs) {
        if (inputs == null || inputs.isEmpty()) {
            return;
        }
        UploadPlan plan = buildUploadPlan(inputs);
        if (plan.toUpload.isEmpty()) {
            return;
        }
        List<String> ids = recordItemClient.uploadAttachments(plan.toUpload);
        validateUploadedIdsCount(plan.toUpload.size(), ids.size());
        mapIdsBackToInputs(inputs, plan.inputIndexes, ids, attachmentRefs);
    }

    private UploadPlan buildUploadPlan(List<Item> inputs) {
        List<Attachment> toUpload = new ArrayList<>();
        List<Integer> inputIndexes = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            Item input = inputs.get(i);
            if (hasContent(input)) {
                toUpload.add(createAttachment(input));
                inputIndexes.add(i);
            }
        }
        return new UploadPlan(toUpload, inputIndexes);
    }

    private boolean hasContent(Item input) {
        return input != null && input.getContent() != null && input.getContent().length > 0;
    }

    private void validateUploadedIdsCount(int expected, int actual) {
        if (expected != actual) {
            throw new IllegalStateException("Mismatch between uploaded attachments and returned IDs");
        }
    }

    private void mapIdsBackToInputs(List<Item> inputs, List<Integer> inputIndexes, List<String> ids, List<Reference> attachmentRefs) {
        for (int k = 0; k < inputIndexes.size(); k++) {
            int idx = inputIndexes.get(k);
            String attachmentId = ids.get(k);
            Item input = inputs.get(idx);
            input.setReference(constructPathToAttachment(attachmentId));
            input.setContent(null);
            attachmentRefs.add(new Reference()
                    .setType(input.getName())
                    .setValue(attachmentId)
                    .setRefType(RefType.ATTACHMENT)
            );
        }
    }

    private record UploadPlan(List<Attachment> toUpload, List<Integer> inputIndexes) {
    }

    private List<Reference> recordTestStepOutputs(List<TestRunReport> testRunReports) {
        List<Reference> outputRefs = new ArrayList<>();
        if (testRunReports == null) return outputRefs;
        for (TestRunReport testRunReport : testRunReports) {
            if (testRunReport.getStepRunReports() == null) continue;
            for (StepRunReport stepRunReport : testRunReport.getStepRunReports()) {
                List<Item> outputs = stepRunReport.getOutputs();
                outputRefs.addAll(recordMaestroItems(outputs));
            }
        }
        return outputRefs;
    }

    private String recordTestReport(TestReport testReport, List<TestReportReference> testReportReferences, List<Reference> attachmentRefs, List<Reference> outputItemRefs) {
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
                            .setType(ref.getTestSuiteName())
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
                            .setAccessControlList(
                                    new AccessControlList()
                                            .setOwners(Set.of("gazelle"))
                                            .setPublic(true)
                            )
                            .setType(TEST_REPORT)
                            .setContent(jsonContent)
                            .setReferences(references);

            return recordItemClient.recordItem(item);
        } catch (Exception e) {
            throw new TestReportRecordException("Failed to record TestReport", e);
        }
    }

    private List<TestReportReference> recordSubReports(TestReport report) {
        List<TestReportReference> subRefs = new ArrayList<>();
        if (report.getSubReports() == null) return subRefs;
        for (TestReport sub : report.getSubReports()) {
            // Reuse full flow for each sub-report
            String subItemId = recordTestReportWithReferences(sub);
            TestReportReference ref = new TestReportReference();
            ref.setTestReportId(subItemId);
            ref.setResult(sub.getResult());
            ref.setTestSuiteName(sub.getTestSuiteName());
            subRefs.add(ref);
        }
        return subRefs;
    }

    private List<Reference> recordMaestroItems(List<Item> items) {
        List<Reference> references = new ArrayList<>();
        if (items == null || items.isEmpty()) return references;
        for (Item item : items) {
            if (item == null) continue;
            com.kereval.gazelle.datahouse.api.business.record.Item datahouseItem = convertMaestroItemToDatahouseItem(item);
            String itemId = recordItemClient.recordItem(datahouseItem);
            item.setReference(constructUrlToDatahouse(itemId));
            item.setContent(null); // Clear content after recording to avoid duplication
            references.add(new Reference()
                    .setType(item.getName())
                    .setValue(itemId)
                    .setRefType(RefType.ITEM_ID)
            );
        }
        return references;
    }

    private com.kereval.gazelle.datahouse.api.business.record.Item convertMaestroItemToDatahouseItem(Item maestroItem) {
        String content = maestroItem.getContent() != null
                ? new String(maestroItem.getContent(), StandardCharsets.UTF_8)
                : "";
        return new com.kereval.gazelle.datahouse.api.business.record.Item()
                .setAccessControlList(new AccessControlList()
                        .setOwners(Set.of("gazelle"))
                        .setPublic(true)
                ) //todo: set real ACL
                .setType(maestroItem.getName())
                .setContent(content);
    }

    private Attachment createAttachment(Item maestroItem) {
        return new Attachment()
                .setType(maestroItem.getName())
                .setContent(maestroItem.getContent());
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

}