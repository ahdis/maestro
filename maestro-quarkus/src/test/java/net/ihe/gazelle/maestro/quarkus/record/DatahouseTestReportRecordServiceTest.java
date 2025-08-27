package net.ihe.gazelle.maestro.quarkus.record;

import net.ihe.gazelle.maestro.api.business.testreport.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DatahouseTestReportRecordServiceTest {

    private DatahouseTestReportRecordService service;
    private RecordItemClientMock recordItemClientMock;

    @BeforeEach
    void setUp() {
        recordItemClientMock = new RecordItemClientMock();
        service = new DatahouseTestReportRecordService(recordItemClientMock, "http://dh");
    }

    @Test
    void records_inputs_as_attachments_and_updates_references() {
        TestRunReport run = new TestRunReport()
                .setDateTime(new java.util.Date())
                .setResult(Result.UNDEFINED)
                .setTest(new net.ihe.gazelle.maestro.api.business.testreport.Test().setName("T"));
        run.addInput(new Input("in1", "alpha".getBytes(StandardCharsets.UTF_8)));
        run.addInput(new Input("in2", "beta".getBytes(StandardCharsets.UTF_8)));

        TestReport report = new TestReport()
                .setUuid("R-1")
                .setDateTime(new java.util.Date())
                .setResult(Result.UNDEFINED)
                .addTestRunReport(run);

        String id = service.recordTestReportWithReferences(report);

        assertThat(id).isNotBlank();
        assertThat(recordItemClientMock.uploadedAttachmentsBatches).hasSize(1);
        assertThat(recordItemClientMock.uploadedAttachmentsBatches.get(0)).hasSize(2);

        List<Input> inputs = report.getTestRunReports().get(0).getInputs();
        assertThat(inputs.get(0).getReference()).isEqualTo("/attachments/att-1");
        assertThat(inputs.get(1).getReference()).isEqualTo("/attachments/att-2");
        assertThat(inputs.get(0).getContent()).isNull();
        assertThat(inputs.get(1).getContent()).isNull();
    }

    @Test
    void records_json_outputs_as_items_and_updates_references() {
        Output out1 = new Output("out1", "x".getBytes(StandardCharsets.UTF_8), Output.FORMAT_JSON);
        Output out2 = new Output("out2", "y".getBytes(StandardCharsets.UTF_8), Output.FORMAT_JSON);
        StepRunReport step = new StepRunReport().addOutput(out1).addOutput(out2);
        TestRunReport run = new TestRunReport()
                .setDateTime(new java.util.Date())
                .setResult(Result.UNDEFINED)
                .setTest(new net.ihe.gazelle.maestro.api.business.testreport.Test().setName("T"))
                .addStepRunReport(step);
        TestReport report = new TestReport()
                .setUuid("R-2")
                .setDateTime(new java.util.Date())
                .setResult(Result.UNDEFINED)
                .addTestRunReport(run);

        String reportItemId = service.recordTestReportWithReferences(report);

        // two items for outputs, then one for the main report
        assertThat(recordItemClientMock.recordedItems).hasSize(3);
        assertThat(out1.getReference()).isEqualTo("http://dh/items/itm-1");
        assertThat(out2.getReference()).isEqualTo("http://dh/items/itm-2");
        assertThat(out1.getContent()).isNull();
        assertThat(out2.getContent()).isNull();
        assertThat(reportItemId).isEqualTo("itm-3");
    }

    @Test
    void records_non_json_outputs_as_attachments_and_updates_references() {
        Output out1 = new Output("out1", "x".getBytes(StandardCharsets.UTF_8), Output.FORMAT_TEXT);
        Output out2 = new Output("out2", "y".getBytes(StandardCharsets.UTF_8), Output.FORMAT_PDF);
        StepRunReport step = new StepRunReport().addOutput(out1).addOutput(out2);
        TestRunReport run = new TestRunReport()
                .setDateTime(new java.util.Date())
                .setResult(Result.UNDEFINED)
                .setTest(new net.ihe.gazelle.maestro.api.business.testreport.Test().setName("T"))
                .addStepRunReport(step);
        TestReport report = new TestReport()
                .setUuid("R-3")
                .setDateTime(new java.util.Date())
                .setResult(Result.UNDEFINED)
                .addTestRunReport(run);

        String reportItemId = service.recordTestReportWithReferences(report);

        // one batch upload for two attachments; only main report item recorded
        assertThat(recordItemClientMock.uploadedAttachmentsBatches).hasSize(1);
        assertThat(recordItemClientMock.uploadedAttachmentsBatches.get(0)).hasSize(2);
        assertThat(recordItemClientMock.recordedItems).hasSize(1);
        assertThat(out1.getReference()).isEqualTo("/attachments/att-1");
        assertThat(out2.getReference()).isEqualTo("/attachments/att-2");
        assertThat(out1.getContent()).isNull();
        assertThat(out2.getContent()).isNull();
        assertThat(reportItemId).isEqualTo("itm-1");
    }

    @Test
    void records_mixed_outputs_json_and_non_json() {
        Output jsonOut = new Output("json", "{}".getBytes(StandardCharsets.UTF_8), Output.FORMAT_JSON);
        Output txtOut = new Output("txt", "hello".getBytes(StandardCharsets.UTF_8), Output.FORMAT_TEXT);
        StepRunReport step = new StepRunReport().addOutput(jsonOut).addOutput(txtOut);
        TestRunReport run = new TestRunReport()
                .setDateTime(new java.util.Date())
                .setResult(Result.UNDEFINED)
                .setTest(new net.ihe.gazelle.maestro.api.business.testreport.Test().setName("T"))
                .addStepRunReport(step);
        TestReport report = new TestReport()
                .setUuid("R-4")
                .setDateTime(new java.util.Date())
                .setResult(Result.UNDEFINED)
                .addTestRunReport(run);

        String reportItemId = service.recordTestReportWithReferences(report);

        // one output as item, one as attachment, plus main report item
        assertThat(recordItemClientMock.recordedItems).hasSize(2);
        assertThat(recordItemClientMock.uploadedAttachmentsBatches).hasSize(1);
        assertThat(jsonOut.getReference()).isEqualTo("http://dh/items/itm-1");
        assertThat(txtOut.getReference()).isEqualTo("/attachments/att-1");
        assertThat(jsonOut.getContent()).isNull();
        assertThat(txtOut.getContent()).isNull();
        assertThat(reportItemId).isEqualTo("itm-2");
    }
}

