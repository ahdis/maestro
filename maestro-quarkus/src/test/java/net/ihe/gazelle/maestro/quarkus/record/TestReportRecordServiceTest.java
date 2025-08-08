package net.ihe.gazelle.maestro.quarkus.record;

import com.kereval.gazelle.datahouse.api.business.record.Attachment;
import com.kereval.gazelle.datahouse.technical.rest.client.RecordItemClient;
import net.ihe.gazelle.maestro.api.business.testreport.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestReportRecordServiceTest {

    private static class FakeRecordItemClient implements RecordItemClient {
        int itemSeq = 0;
        int attachSeq = 0;
        final List<Object> recordedItems = new ArrayList<>();
        final List<List<Attachment>> uploadedAttachmentsBatches = new ArrayList<>();

        @Override
        public String recordItem(com.kereval.gazelle.datahouse.api.business.record.Item item) {
            recordedItems.add(item);
            return "itm-" + (++itemSeq);
        }

        @Override
        public String recordItem(com.kereval.gazelle.datahouse.api.business.record.Item item, List<Attachment> attachments) {
            // not used by the service for this flow; fallback to single-arg path
            return recordItem(item);
        }

        // New API exposed by datahouse; service calls it via reflection
        public List<String> uploadAttachments(List<Attachment> attachments) {
            uploadedAttachmentsBatches.add(attachments);
            List<String> ids = new ArrayList<>();
            for (int i = 0; i < attachments.size(); i++) {
                ids.add("att-" + (++attachSeq));
            }
            return ids;
        }
    }

    private TestReportRecordService service;
    private FakeRecordItemClient fakeClient;

    @BeforeEach
    void setup() throws Exception {
        fakeClient = new FakeRecordItemClient();
        service = new TestReportRecordService(fakeClient, "http://dh");
    }

    @Test
    void persists_inputs_as_attachments_and_sets_urls_in_report() {
        // Given a report with one test run and two input attachments
        TestRunReport run = new TestRunReport()
                .setDateTime(new java.util.Date())
                .setResult(Result.UNDEFINED)
                .setTest(new net.ihe.gazelle.maestro.api.business.testreport.Test().setName("T"));
        run.addInput(new Item("inA", "alpha".getBytes(StandardCharsets.UTF_8)));
        run.addInput(new Item("inB", "beta".getBytes(StandardCharsets.UTF_8)));

        TestReport report = new TestReport()
                .setUuid("r-1")
                .setDateTime(new java.util.Date())
                .setResult(Result.UNDEFINED)
                .addTestRunReport(run);

        // When
        String reportItemId = service.recordTestReportWithReferences(report);

        // Then: upload called once with two attachments
        assertThat(fakeClient.uploadedAttachmentsBatches).hasSize(1);
        assertThat(fakeClient.uploadedAttachmentsBatches.get(0)).hasSize(2);

        // Inputs in domain hold URL references to attachments
        List<Item> inputs = report.getTestRunReports().get(0).getInputs();
        assertThat(inputs.get(0).getReference()).isEqualTo("http://dh/attachments/att-1");
        assertThat(inputs.get(1).getReference()).isEqualTo("http://dh/attachments/att-2");
        assertThat(inputs.get(0).getContent()).isNull();
        assertThat(inputs.get(1).getContent()).isNull();

        // Report item was recorded
        assertThat(reportItemId).isNotBlank();
        assertThat(fakeClient.recordedItems).isNotEmpty();
    }

    @Test
    void persists_step_outputs_once_and_sets_item_urls_in_report() {
        // Given a report with one step having two outputs
        Item out1 = new Item("out1", "x".getBytes(StandardCharsets.UTF_8));
        Item out2 = new Item("out2", "y".getBytes(StandardCharsets.UTF_8));
        StepRunReport step = new StepRunReport()
                .addOutput(out1)
                .addOutput(out2);
        TestRunReport run = new TestRunReport()
                .setDateTime(new java.util.Date())
                .setResult(Result.UNDEFINED)
                .setTest(new net.ihe.gazelle.maestro.api.business.testreport.Test().setName("T"))
                .addStepRunReport(step);
        TestReport report = new TestReport()
                .setUuid("r-2")
                .setDateTime(new java.util.Date())
                .setResult(Result.UNDEFINED)
                .addTestRunReport(run);

        // When
        String reportItemId = service.recordTestReportWithReferences(report);

        // Then: first two recordItem calls are outputs, then the report item
        // Verify outputs gained item URLs and content cleared
        assertThat(out1.getReference()).isEqualTo("http://dh/items/itm-1");
        assertThat(out2.getReference()).isEqualTo("http://dh/items/itm-2");
        assertThat(out1.getContent()).isNull();
        assertThat(out2.getContent()).isNull();

        // Total recordItem calls: 2 outputs + 1 report
        assertThat(fakeClient.recordedItems.size()).isEqualTo(3);
        assertThat(reportItemId).isEqualTo("itm-3");
    }

    @Test
    void persists_subreports_and_includes_references_in_main_item() {
        // Given a main report with a subreport (no inputs/outputs to keep it simple)
        TestReport sub = new TestReport()
                .setUuid("sub-1").setDateTime(new java.util.Date())
                .setResult(Result.UNDEFINED);
        TestReport report = new TestReport()
                .setUuid("root-1").setDateTime(new java.util.Date())
                .setResult(Result.UNDEFINED)
                .addSubReport(sub);

        // When
        String rootId = service.recordTestReportWithReferences(report);

        // Then: two report items persisted (sub then root)
        assertThat(fakeClient.recordedItems.size()).isEqualTo(2);
        assertThat(rootId).isEqualTo("itm-2");
    }
}

