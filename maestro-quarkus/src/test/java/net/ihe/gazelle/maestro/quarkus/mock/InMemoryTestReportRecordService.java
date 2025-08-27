package net.ihe.gazelle.maestro.quarkus.mock;

import net.ihe.gazelle.maestro.api.business.testreport.*;
import net.ihe.gazelle.maestro.business.TestReportRecordService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTestReportRecordService implements TestReportRecordService {

    private final Map<String, TestReport> storedReports = new ConcurrentHashMap<>();
    private final Map<String, Output> storedItems = new ConcurrentHashMap<>();
    private final Map<String, byte[]> storedAttachments = new ConcurrentHashMap<>();
    private final AtomicLong reportIdCounter = new AtomicLong(1);
    private final AtomicLong itemIdCounter = new AtomicLong(1);
    private final AtomicLong attachmentIdCounter = new AtomicLong(1);

    @Override
    public String recordTestReportWithReferences(TestReport report) {
        // 1) Process inputs as attachments and update references
        processTestRunInputs(report.getTestRunReports());

        // 2) Record step outputs and update their references
        recordTestStepOutputs(report.getTestRunReports());

        // 3) Record sub-reports recursively
        recordSubReports(report);

        // 4) Store the main test report
        String reportId = generateReportId();
        storedReports.put(reportId, new TestReport(report));

        return reportId;
    }

    /**
     * Process inputs from test run reports, store content as attachments and update references
     */
    private void processTestRunInputs(List<TestRunReport> testRunReports) {
        if (testRunReports == null || testRunReports.isEmpty()) {
            return;
        }

        for (TestRunReport testRunReport : testRunReports) {
            processInputsForAttachments(testRunReport.getInputs());
        }
    }

    private void processInputsForAttachments(List<Input> inputs) {
        if (inputs == null || inputs.isEmpty()) {
            return;
        }

        for (Input input : inputs) {
            if (hasContent(input)) {
                String attachmentId = generateAttachmentId();
                storedAttachments.put(attachmentId, input.getContent().clone());
                input.setReference(constructUrlToAttachment(attachmentId));
                input.setContent(null); // Clear content after storing
            }
        }
    }

    private void recordTestStepOutputs(List<TestRunReport> testRunReports) {
        if (testRunReports == null) return;

        for (TestRunReport testRunReport : testRunReports) {
            if (testRunReport.getStepRunReports() == null) continue;

            for (StepRunReport stepRunReport : testRunReport.getStepRunReports()) {
                List<Output> outputs = stepRunReport.getOutputs();
                if (outputs == null || outputs.isEmpty()) continue;
                for (Output output : outputs) {
                    if (output == null) continue;
                    if (Output.FORMAT_JSON.equals(output.getFormat())) {
                        recordOutputAsItem(output);
                    } else {
                        recordOutputAsAttachment(output);
                    }
                }
            }
        }
    }

    /**
     * Record sub-reports recursively
     */
    private void recordSubReports(TestReport report) {
        if (report.getSubReports() == null) return;

        for (TestReport subReport : report.getSubReports()) {
            // Recursively record each sub-report
            recordTestReportWithReferences(subReport);
        }
    }

    private void recordOutputAsItem(Output output) {
        String itemId = generateItemId();
        storedItems.put(itemId, cloneItem(output));
        output.setReference(constructUrlToItem(itemId));
        output.setContent(null);
    }

    private void recordOutputAsAttachment(Output output) {
        if (output.getContent() == null || output.getContent().length == 0) return;
        String attachmentId = generateAttachmentId();
        storedAttachments.put(attachmentId, output.getContent().clone());
        output.setReference(constructUrlToAttachment(attachmentId));
        output.setContent(null);
    }

    private boolean hasContent(Input input) {
        return input != null && input.getContent() != null && input.getContent().length > 0;
    }

    private String generateReportId() {
        return "test-report-" + reportIdCounter.getAndIncrement();
    }

    private String generateItemId() {
        return "item-" + itemIdCounter.getAndIncrement();
    }

    private String generateAttachmentId() {
        return "attachment-" + attachmentIdCounter.getAndIncrement();
    }

    private String constructUrlToAttachment(String attachmentId) {
        return "memory://attachments/" + attachmentId;
    }

    private String constructUrlToItem(String itemId) {
        return "memory://items/" + itemId;
    }



    private Output cloneItem(Output original) {
        if (original == null) return null;

        Output clone = new Output();
        clone.setName(original.getName());
        clone.setContent(original.getContent() != null ? original.getContent().clone() : null);
        clone.setReference(original.getReference());
        clone.setFormat(original.getFormat());

        return clone;
    }

    // Utility methods for testing
    public TestReport getStoredReport(String reportId) {
        return storedReports.get(reportId);
    }

    public Output getStoredItem(String itemId) {
        return storedItems.get(itemId);
    }

    public byte[] getStoredAttachment(String attachmentId) {
        return storedAttachments.get(attachmentId);
    }

    public void clear() {
        storedReports.clear();
        storedItems.clear();
        storedAttachments.clear();
        reportIdCounter.set(1);
        itemIdCounter.set(1);
        attachmentIdCounter.set(1);
    }

    public int getStoredReportsCount() {
        return storedReports.size();
    }

    public int getStoredItemsCount() {
        return storedItems.size();
    }

    public int getStoredAttachmentsCount() {
        return storedAttachments.size();
    }
}
