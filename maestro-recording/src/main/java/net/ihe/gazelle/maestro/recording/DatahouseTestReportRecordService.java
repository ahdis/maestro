package net.ihe.gazelle.maestro.recording;

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
import net.ihe.gazelle.maestro.api.business.recording.ItemReference;
import net.ihe.gazelle.maestro.api.business.recording.TestReportRecordException;
import net.ihe.gazelle.maestro.api.business.recording.TestReportRecordService;
import net.ihe.gazelle.maestro.api.business.testreport.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.*;
import net.ihe.gazelle.maestro.recording.entity.TestReportEntity;
import net.ihe.gazelle.maestro.recording.entity.TestReportReference;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class DatahouseTestReportRecordService implements TestReportRecordService {

    public static final String TEST_REPORT_TYPE = "TEST_REPORT";
    public static final String BYTE_ARRAY_TYPE = "BYTE_ARRAY";
    private final RecordItemClient recordItemClient;

    private final StepOutputDissectorProvider dissectorProvider;
    private final StepOutputRecordService stepOutputRecordService;

    @Inject
    public DatahouseTestReportRecordService(@ConfigProperty(name = "datahouse.url") String datahouseUrl,
                                            StepOutputDissectorProvider dissectorProvider) {
        this.recordItemClient = new RecordItemClientProvider().createClient();
        this.stepOutputRecordService = new StepOutputRecordService(recordItemClient, datahouseUrl);
        this.dissectorProvider = dissectorProvider;
    }

    // Convenience constructor for tests to bypass config-backed client creation
    DatahouseTestReportRecordService(RecordItemClient recordItemClient,
                                     @ConfigProperty(name = "datahouse.url") String datahouseUrl,
                                     StepOutputDissectorProvider dissectorProvider) {
        this.recordItemClient = recordItemClient;
        this.dissectorProvider = dissectorProvider;
        this.stepOutputRecordService = new StepOutputRecordService(recordItemClient, datahouseUrl);
    }

    @Override
    public String recordTestReport(TestReport report) {
        return recordTestReportWithReferences(report).location();
    }

    /**
     * Pre-upload input attachments and update the report to reference them; record step outputs;
     * persist sub-reports; then persist the main TestReport with References[] including inputs and sub-reports.
     *
     * @return the created item ID of the main TestReport
     */
    private ItemReference recordTestReportWithReferences(TestReport report) {
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
            List<Property> inputs = new ArrayList<>(testRunReport.getInputs());
            processInputsForAttachments(inputs, attachmentRefs);
            testRunReport.setInputs(inputs);
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

    // Walks every step of every test run, invoking the right dissector and aggregating the references to return.
    private List<Reference> recordTestStepOutputs(List<TestRunReport> testRunReports, AccessControlList acl) {
        List<Reference> aggregatedReferences = new ArrayList<>();
        if (testRunReports == null) {
            return aggregatedReferences;
        }
        for (TestRunReport testRunReport : testRunReports) {
            List<StepRunReport> stepRunReports = testRunReport.getStepRunReports();
            if (stepRunReports == null) {
                continue;
            }
            for (StepRunReport stepRunReport : stepRunReports) {
                StepOutputDissector dissector = dissectorProvider.resolve(stepRunReport);
                if(dissector != null) {
                    StepOutputProjection projection = safeDissect(dissector,
                            new StepOutputContext(stepRunReport, acl));
                    if (isEmpty(projection)) {
                        continue;
                    }
                    List<Property> updatedOutputs = new ArrayList<>(stepRunReport.getOutputs());
                    ensureOutputCapacity(updatedOutputs, projection);
                    List<Reference> references = new ArrayList<>(uploadAttachments(projection.attachments(), updatedOutputs));
                    List<Reference> stepReferences = stepOutputRecordService.dissectAndRecord(stepRunReport, projection, acl, references, updatedOutputs);
                    aggregatedReferences.addAll(stepReferences);
                }
            }
        }
        return aggregatedReferences;
    }

    private StepOutputProjection safeDissect(StepOutputDissector dissector, StepOutputContext context) {
        StepOutputProjection projection = dissector.dissect(context);
        return projection != null ? projection : StepOutputProjection.empty();
    }

    private boolean isEmpty(StepOutputProjection projection) {
        return projection.items().isEmpty()
                && projection.attachments().isEmpty()
                && projection.propertyMutations().isEmpty()
                && projection.reportReferences().isEmpty();
    }


    // Makes sure the outputs list is large enough so later mutations can safely slot in replacements.
    // (because we are using indexes)
    private void ensureOutputCapacity(List<Property> outputs, StepOutputProjection projection) {
        int required = projection.attachments().stream()
                .mapToInt(AttachmentPlan::outputIndex)
                .filter(idx -> idx >= 0)
                .max()
                .orElse(-1);
        int requiredFromMutations = projection.propertyMutations().stream()
                .mapToInt(PropertyMutation::outputIndex)
                .max()
                .orElse(-1);
        required = Math.max(required, requiredFromMutations);
        while (outputs.size() <= required) {
            outputs.add(null);
        }
    }

    // Streams attachment payloads to Datahouse, replaces the original properties, and returns the new references.
    private List<Reference> uploadAttachments(List<AttachmentPlan> plans, List<Property> outputs) {
        if (plans.isEmpty()) {
            return List.of();
        }
        List<Attachment> datahouseAttachments = plans.stream()
                .map(AttachmentPlan::attachment)
                .filter(DatahouseMaestroAttachment.class::isInstance)
                .map(DatahouseMaestroAttachment.class::cast)
                .map(DatahouseMaestroAttachment::toDatahouseAttachment)
                .toList();
        List<String> ids = recordItemClient.uploadAttachments(datahouseAttachments);
        validateUploadedIdsCount(plans.size(), ids.size());

        List<Reference> references = new ArrayList<>();
        for (int i = 0; i < plans.size(); i++) {
            AttachmentPlan plan = plans.get(i);
            String attachmentId = ids.get(i);
            String url = stepOutputRecordService.constructUrlToDatahouse(attachmentId);
            if (plan.propertyFactory() != null) {
                Property replacement = plan.propertyFactory().create(attachmentId, url);
                setOutput(outputs, plan.outputIndex(), replacement);
            }
            references.add(new Reference()
                    .setName(plan.referenceName())
                    .setType(plan.referenceType())
                    .setValue(attachmentId)
                    .setRefType(RefType.ATTACHMENT));
        }
        return references;
    }


    // Utility to grow the outputs list lazily before writing the replacement property (because it's a list and not a map).
    private void setOutput(List<Property> outputs, int index, Property replacement) {
        while (outputs.size() <= index) {
            outputs.add(null);
        }
        outputs.set(index, replacement);
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

            Item item = new Item()
                            .setAccessControlList(testReport.getAccessControlList())
                            .setType(TEST_REPORT_TYPE)
                            .setContent(jsonContent)
                            .setReferences(references);

            String itemId = recordItemClient.recordItem(item);
            return new ItemReference(itemId, stepOutputRecordService.constructUrlToDatahouse(itemId));
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

    /**
     * This is just a path as we cannot construct a full URL here, because the Item ID is not known
     * until the full TestReport is recorded.
     */
    private String constructPathToAttachment(String attachmentId) {
        return "/attachments/" + attachmentId;
    }

    private Attachment createAttachment(String name, byte[] content) {
        return new Attachment()
                .setFilename(name)
                .setType(BYTE_ARRAY_TYPE)
                .setContent(content);
    }


}
