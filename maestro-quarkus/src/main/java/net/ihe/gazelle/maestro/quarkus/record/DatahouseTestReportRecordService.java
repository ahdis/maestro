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
import net.ihe.gazelle.maestro.api.business.testreport.dissection.AttachmentPlan;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.ItemPlan;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.ItemReferenceBinding;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.PropertyMutation;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.PropertyReferenceFactory;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.ReportReferenceBinding;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputContext;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputDissector;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputDissectorProvider;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputProjection;
import net.ihe.gazelle.maestro.engine.business.TestReportRecordException;
import net.ihe.gazelle.maestro.engine.business.TestReportRecordService;
import net.ihe.gazelle.maestro.quarkus.record.entity.TestReportEntity;
import net.ihe.gazelle.maestro.quarkus.record.entity.TestReportReference;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequestScoped
public class DatahouseTestReportRecordService implements TestReportRecordService {

    public static final String TEST_REPORT_TYPE = "TEST_REPORT";
    public static final String BYTE_ARRAY_TYPE = "BYTE_ARRAY";
    private final RecordItemClient recordItemClient;

    private final String datahouseUrl;
    private final StepOutputDissectorProvider dissectorProvider;

    @Inject
    public DatahouseTestReportRecordService(@ConfigProperty(name = "datahouse.url") String datahouseUrl,
                                            StepOutputDissectorProvider dissectorProvider) {
        this(new RecordItemClientProvider().createClient(), datahouseUrl, dissectorProvider);
    }

    // Convenience constructor for tests to bypass config-backed client creation
    DatahouseTestReportRecordService(RecordItemClient recordItemClient,
                                     @ConfigProperty(name = "datahouse.url") String datahouseUrl,
                                     StepOutputDissectorProvider dissectorProvider) {
        this.recordItemClient = recordItemClient;
        this.datahouseUrl = datahouseUrl;
        this.dissectorProvider = dissectorProvider;
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

    private record ItemPersistenceState(String[] itemIds, String[] itemUrls) {
        private static final ItemPersistenceState EMPTY = new ItemPersistenceState(new String[0], new String[0]);
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
                StepOutputProjection projection = safeDissect(dissector,
                        new StepOutputContext(stepRunReport, testRunReport, acl));
                if (isEmpty(projection)) {
                    continue;
                }
                List<Reference> stepReferences = processProjection(stepRunReport, projection, acl);
                aggregatedReferences.addAll(stepReferences);
            }
        }
        return aggregatedReferences;
    }

    // Defensive wrapper so a null projection from a dissector never bubbles up.
    private StepOutputProjection safeDissect(StepOutputDissector dissector, StepOutputContext context) {
        StepOutputProjection projection = dissector.dissect(context);
        return projection != null ? projection : StepOutputProjection.empty();
    }

    // Helper to short-circuit processing when a dissector produced no work.
    private boolean isEmpty(StepOutputProjection projection) {
        return projection.items().isEmpty()
                && projection.attachments().isEmpty()
                && projection.propertyMutations().isEmpty()
                && projection.reportReferences().isEmpty();
    }

    // Applies the projection to the StepRunReport: uploads attachments, persists items, updates outputs, and collects refs.
    private List<Reference> processProjection(StepRunReport stepRunReport,
                                              StepOutputProjection stepOutputProjection,
                                              AccessControlList acl) {
        List<Property> updatedOutputs = new ArrayList<>(stepRunReport.getOutputs());
        ensureOutputCapacity(updatedOutputs, stepOutputProjection);

        List<Reference> references = new ArrayList<>(uploadAttachments(stepOutputProjection.attachments(), updatedOutputs));

        // persist step outputs as items
        ItemPersistenceState persistenceState = persistItems(stepOutputProjection.items(), references, acl);
        applyPropertyMutations(stepOutputProjection.propertyMutations(), updatedOutputs, persistenceState);

        references.addAll(stepOutputProjection.reportReferences());
        stepRunReport.setOutputs(updatedOutputs);
        return references;
    }

    // Makes sure the outputs list is large enough so later mutations can safely slot in replacements.
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
        List<Attachment> attachments = plans.stream()
                .map(AttachmentPlan::attachment)
                .collect(Collectors.toCollection(ArrayList::new));
        List<String> ids = recordItemClient.uploadAttachments(attachments);
        validateUploadedIdsCount(plans.size(), ids.size());

        List<Reference> references = new ArrayList<>();
        for (int i = 0; i < plans.size(); i++) {
            AttachmentPlan plan = plans.get(i);
            String attachmentId = ids.get(i);
            String url = constructUrlToDatahouse(attachmentId);
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

    // Persists the ordered items, wiring parent/report references as we go, and returns the ids/urls for later mutations.
    private ItemPersistenceState persistItems(List<ItemPlan> itemPlans,
                                              List<Reference> reportReferences,
                                              AccessControlList acl) {
        if (itemPlans.isEmpty()) {
            return ItemPersistenceState.EMPTY;
        }

        String[] ids = new String[itemPlans.size()];
        String[] urls = new String[itemPlans.size()];

        for (int i = 0; i < itemPlans.size(); i++) {
            ItemPlan itemPlan = itemPlans.get(i);
            Item item = prepareItemAclAndReferences(itemPlan.item(), acl);
            String itemId = recordItemClient.recordItem(item);
            ids[i] = itemId;
            urls[i] = constructUrlToDatahouse(itemId);
            appendReportBindings(reportReferences, itemPlan, itemId);
            wireParentBindings(itemPlans, i, itemId, itemPlan);
        }

        return new ItemPersistenceState(ids, urls);
    }

    // Rewrites step outputs so they now point at freshly persisted items instead of inline payloads.
    private void applyPropertyMutations(List<PropertyMutation> mutations,
                                        List<Property> outputs,
                                        ItemPersistenceState state) {
        for (PropertyMutation mutation : mutations) {
            String[] ids = state.itemIds();
            String[] urls = state.itemUrls();
            int sourceIndex = mutation.sourceItemIndex();
            if (sourceIndex < 0 || sourceIndex >= ids.length) {
                throw new IllegalStateException("Invalid source item index: " + sourceIndex);
            }
            Property replacement = mutation.factory().create(ids[sourceIndex], urls[sourceIndex]);
            setOutput(outputs, mutation.outputIndex(), replacement);
        }
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

    private Attachment createAttachment(String name, byte[] content) {
        return new Attachment()
                .setFilename(name)
                .setType(BYTE_ARRAY_TYPE)
                .setContent(content);
    }


    private Item prepareItemAclAndReferences(Item item, AccessControlList fallbackAcl) {
        if (item.getAccessControlList() == null) {
            item.setAccessControlList(fallbackAcl);
        }
        if (item.getReferences() == null) {
            item.setReferences(new ArrayList<>());
        }
        return item;
    }

    private void appendReportBindings(List<Reference> reportReferences, ItemPlan itemPlan, String itemId) {
        for (ReportReferenceBinding binding : itemPlan.reportBindings()) {
            reportReferences.add(new Reference()
                    .setName(binding.referenceName())
                    .setType(binding.referenceType())
                    .setRefType(binding.refType())
                    .setValue(itemId));
        }
    }

    private void wireParentBindings(List<ItemPlan> itemPlans, int currentIndex, String childItemId, ItemPlan childPlan) {
        for (ItemReferenceBinding binding : childPlan.parentBindings()) {
            validateParentIndex(binding.parentItemIndex(), currentIndex, itemPlans.size());
            Item parent = itemPlans.get(binding.parentItemIndex()).item();
            if (parent.getReferences() == null) {
                parent.setReferences(new ArrayList<>());
            }
            parent.addReference(new Reference()
                    .setName(binding.referenceName())
                    .setType(binding.referenceType())
                    .setRefType(binding.refType())
                    .setValue(childItemId));
        }
    }

    private void validateParentIndex(int parentIndex, int childIndex, int maxSize) {
        if (parentIndex <= childIndex || parentIndex >= maxSize) {
            throw new IllegalStateException("Parent item index must refer to an item persisted after the child");
        }
    }
}
