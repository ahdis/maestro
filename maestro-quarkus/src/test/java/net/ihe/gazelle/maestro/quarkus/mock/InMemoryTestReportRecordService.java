package net.ihe.gazelle.maestro.quarkus.mock;

import net.ihe.gazelle.maestro.api.business.testreport.Item;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.maestro.business.TestReportRecordService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTestReportRecordService implements TestReportRecordService {

    private final Map<String, TestReport> storedReports = new ConcurrentHashMap<>();
    private final Map<String, Item> storedItems = new ConcurrentHashMap<>();
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

    private void processInputsForAttachments(List<Item> inputs) {
        if (inputs == null || inputs.isEmpty()) {
            return;
        }

        for (Item input : inputs) {
            if (hasContent(input)) {
                String attachmentId = generateAttachmentId();
                storedAttachments.put(attachmentId, input.getContent().clone());
                input.setReference(constructUrlToAttachment(attachmentId));
                input.setContent(null); // Clear content after storing
            }
        }
    }

    /**
     * Record step outputs as items and update their references
     */
    private void recordTestStepOutputs(List<TestRunReport> testRunReports) {
        if (testRunReports == null) return;

        for (TestRunReport testRunReport : testRunReports) {
            if (testRunReport.getStepRunReports() == null) continue;

            for (StepRunReport stepRunReport : testRunReport.getStepRunReports()) {
                List<Item> outputs = stepRunReport.getOutputs();
                recordItems(outputs);
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

    /**
     * Record items and update their references
     */
    private void recordItems(List<Item> items) {
        if (items == null || items.isEmpty()) return;

        for (Item item : items) {
            if (item == null) continue;

            String itemId = generateItemId();
            storedItems.put(itemId, cloneItem(item));
            item.setReference(constructUrlToItem(itemId));
            item.setContent(null); // Clear content after recording
        }
    }

    private boolean hasContent(Item input) {
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



    private Item cloneItem(Item original) {
        if (original == null) return null;

        Item clone = new Item();
        clone.setName(original.getName());
        clone.setContent(original.getContent() != null ? original.getContent().clone() : null);
        clone.setReference(original.getReference());

        return clone;
    }

    // Utility methods for testing
    public TestReport getStoredReport(String reportId) {
        return storedReports.get(reportId);
    }

    public Item getStoredItem(String itemId) {
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
