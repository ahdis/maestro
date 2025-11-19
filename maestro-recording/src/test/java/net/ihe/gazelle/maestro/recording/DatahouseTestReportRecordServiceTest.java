package net.ihe.gazelle.maestro.recording;

import com.kereval.gazelle.datahouse.api.business.record.Item;
import com.kereval.gazelle.datahouse.api.business.record.Reference;
import net.ihe.gazelle.maestro.api.business.property.*;
import net.ihe.gazelle.maestro.api.business.testreport.*;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputDissectorProvider;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DatahouseTestReportRecordServiceTest {

    private DatahouseTestReportRecordService service;
    private RecordItemClientMock recordItemClientMock;

    @BeforeEach
    void setUp() {
        recordItemClientMock = new RecordItemClientMock();
        service = new DatahouseTestReportRecordService(recordItemClientMock, "http://dh", emptyDissectorProvider());
    }

    @Test
    void records_inputs_as_attachments_and_updates_references() {
        ByteArrayProperty firstInput = binaryInput("in1", "alpha");
        ByteArrayProperty secondInput = binaryInput("in2", "beta");

        TestRunReport run = newTestRun("run-inputs")
                .addInput(firstInput)
                .addInput(secondInput);
        TestReport report = newReport("R-1", run);

        String reportLocation = service.recordTestReport(report);

        assertThat(reportLocation).isEqualTo("http://dh/items/itm-1");
        assertThat(recordItemClientMock.recordedItems).hasSize(1);
        assertThat(recordItemClientMock.uploadedAttachmentsBatches).hasSize(1);
        assertThat(recordItemClientMock.uploadedAttachmentsBatches.getFirst()).hasSize(2);

        List<Property> inputs = firstRun(report).getInputs();
        assertThat(inputs).hasSize(2);
        assertBinaryAttachment(inputs.get(0), "/attachments/att-1");
        assertBinaryAttachment(inputs.get(1), "/attachments/att-2");
    }

    @Test
    void keeps_non_binary_inputs_inline() {
        StringProperty textInput = new StringProperty("text", "hello");
        BooleanProperty booleanInput = new BooleanProperty("flag", true);
        IntegerProperty numberInput = new IntegerProperty("count", 42);

        TestRunReport run = newTestRun("run-inline")
                .addInput(textInput)
                .addInput(booleanInput)
                .addInput(numberInput);
        TestReport report = newReport("R-inline", run);

        service.recordTestReport(report);

        assertThat(recordItemClientMock.uploadedAttachmentsBatches).isEmpty();

        List<Property> inputs = firstRun(report).getInputs();
        assertThat(inputs).hasSize(3);
        assertThat(inputs.get(0)).isInstanceOf(StringProperty.class);
        assertEquals("hello", inputs.get(0).getValue());
        assertThat(inputs.get(1)).isInstanceOf(BooleanProperty.class);
        assertTrue((Boolean) inputs.get(1).getValue());
        assertThat(inputs.get(2)).isInstanceOf(IntegerProperty.class);
        assertEquals(42, (Integer) inputs.get(2).getValue());
    }

    @Test
    void records_step_outputs_using_dissection_projection() {
        service = new DatahouseTestReportRecordService(recordItemClientMock, "http://dh", staticDissectorProvider());

        StepRunReport step = new StepRunReport()
                .setStepId("step-outputs")
                .addOutput(new ByteArrayProperty(StaticStepOutputDissector.ROOT_OUTPUT_NAME, "{}".getBytes(StandardCharsets.UTF_8)))
                .addOutput(new ByteArrayProperty(StaticStepOutputDissector.SIBLING_OUTPUT_NAME, "{}".getBytes(StandardCharsets.UTF_8)))
                .addOutput(new ByteArrayProperty(StaticStepOutputDissector.ATTACHMENT_OUTPUT_NAME, "pdf".getBytes(StandardCharsets.UTF_8)));

        TestRunReport run = newTestRun("run-outputs").addStepRunReport(step);
        TestReport report = newReport("R-outputs", run);


        String reportLocation = service.recordTestReport(report);

        assertThat(recordItemClientMock.recordedItems).hasSize(5);
        assertThat(recordItemClientMock.uploadedAttachmentsBatches).hasSize(1);
        assertThat(recordItemClientMock.uploadedAttachmentsBatches.getFirst()).hasSize(2);
        assertThat(reportLocation).isEqualTo("http://dh/items/itm-5");

        List<Property> outputs = firstStep(report).getOutputs();
        assertThat(outputs).hasSize(3);
        assertBinaryAttachment(outputs.get(0), "http://dh/items/itm-4");
        assertBinaryAttachment(outputs.get(1), "http://dh/items/itm-3");
        assertBinaryAttachment(outputs.get(2), "/attachments/att-1");

        Item rootItem = recordedItemOfType(StaticStepOutputDissector.ROOT_ITEM_TYPE);
        assertThat(referenceNames(rootItem))
                .containsExactlyInAnyOrder(StaticStepOutputDissector.CHILD_REFERENCE_NAME,
                        StaticStepOutputDissector.SIBLING_REFERENCE_NAME);

        Item childItem = recordedItemOfType(StaticStepOutputDissector.CHILD_ITEM_TYPE);
        assertThat(referenceNames(childItem))
                .containsExactly(StaticStepOutputDissector.GRANDCHILD_REFERENCE_NAME);

        Item testReportItem = recordedItemOfType(DatahouseTestReportRecordService.TEST_REPORT_TYPE);
        assertThat(referenceNames(testReportItem))
                .contains(StaticStepOutputDissector.ATTACHMENT_REFERENCE_NAME,
                        StaticStepOutputDissector.DETACHED_REFERENCE_NAME,
                        StaticStepOutputDissector.REPORT_REFERENCE_NAME);
    }

    private static ByteArrayProperty binaryInput(String name, String value) {
        return new ByteArrayProperty(name, value.getBytes(StandardCharsets.UTF_8));
    }

    private static TestReport newReport(String uuid, TestRunReport run) {
        return new TestReport()
                .setUuid(uuid)
                .setDateTime(Instant.now())
                .setResult(Result.UNDEFINED)
                .addTestRunReport(run);
    }

    private static TestRunReport newTestRun(String runId) {
        return new TestRunReport()
                .setRunId(runId)
                .setDateTime(Instant.now())
                .setResult(Result.UNDEFINED)
                .setTest(new net.ihe.gazelle.maestro.api.business.testreport.Test().setName("T"));
    }

    private static TestRunReport firstRun(TestReport report) {
        return report.getTestRunReports().getFirst();
    }

    private static StepRunReport firstStep(TestReport report) {
        return firstRun(report).getStepRunReports().getFirst();
    }

    private static void assertBinaryAttachment(Property property, String expectedReference) {
        assertThat(property).isInstanceOf(ByteArrayItemProperty.class);
        ByteArrayItemProperty attachment = (ByteArrayItemProperty) property;
        assertThat(attachment.getReference()).isEqualTo(expectedReference);
        assertNull(attachment.getValue());
    }

    private StepOutputDissectorProvider emptyDissectorProvider() {
        return step -> new StaticStepOutputDissector(StepOutputProjection.empty());
    }

    private StepOutputDissectorProvider staticDissectorProvider() {
        return step -> new StaticStepOutputDissector();
    }

    private Item recordedItemOfType(String type) {
        return recordItemClientMock.recordedItems.stream()
                .map(Item.class::cast)
                .filter(item -> type.equals(item.getType()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No recorded item of type " + type));
    }

    private List<String> referenceNames(Item item) {
        return item.getReferences().stream()
                .map(Reference::getName)
                .toList();
    }

}
