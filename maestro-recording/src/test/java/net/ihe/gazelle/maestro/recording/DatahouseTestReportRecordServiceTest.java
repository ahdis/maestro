package net.ihe.gazelle.maestro.recording;

import net.ihe.gazelle.maestro.api.business.property.BooleanProperty;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.IntegerProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.recording.ItemReference;
import net.ihe.gazelle.maestro.api.business.testreport.*;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.AttachmentPlan;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.ItemPlan;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.PropertyMutation;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputContext;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputDissector;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputDissectorProvider;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class DatahouseTestReportRecordServiceTest {

    private DatahouseTestReportRecordService service;
    private RecordItemClientMock recordItemClientMock;
    private TestStepOutputDissectorProvider dissectorProvider;

    @BeforeEach
    void setUp() {
        recordItemClientMock = new RecordItemClientMock();
        dissectorProvider = new TestStepOutputDissectorProvider();
        service = new DatahouseTestReportRecordService(recordItemClientMock, "http://dh", dissectorProvider);
    }

    @Test
    void records_inputs_as_attachments_and_updates_references() {
        ByteArrayProperty firstInput = binaryInput("in1", "alpha");
        ByteArrayProperty secondInput = binaryInput("in2", "beta");

        TestRunReport run = newTestRun("run-inputs")
                .addInput(firstInput)
                .addInput(secondInput);
        TestReport report = newReport("R-1", run);

        ItemReference reportRef = service.recordTestReportWithReferences(report);

        assertThat(reportRef.id()).isEqualTo("itm-1");
        assertThat(reportRef.location()).isEqualTo("http://dh/items/itm-1");
        assertThat(recordItemClientMock.recordedItems).hasSize(1);
        assertThat(recordItemClientMock.uploadedAttachmentsBatches).hasSize(1);
        assertThat(recordItemClientMock.uploadedAttachmentsBatches.get(0)).hasSize(2);

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

        service.recordTestReportWithReferences(report);

        assertThat(recordItemClientMock.uploadedAttachmentsBatches).isEmpty();

        List<Property> inputs = firstRun(report).getInputs();
        assertThat(inputs).hasSize(3);
        assertThat(inputs.get(0)).isInstanceOf(StringProperty.class);
        assertThat(((StringProperty) inputs.get(0)).getValue()).isEqualTo("hello");
        assertThat(inputs.get(1)).isInstanceOf(BooleanProperty.class);
        assertThat(((BooleanProperty) inputs.get(1)).getValue()).isTrue();
        assertThat(inputs.get(2)).isInstanceOf(IntegerProperty.class);
        assertThat(((IntegerProperty) inputs.get(2)).getValue()).isEqualTo(42);
    }

    @Test
    void records_step_outputs_using_dissection_projection() {
        StepRunReport step = new StepRunReport()
                .setStepId("step-outputs")
                .addOutput(new ByteArrayProperty("json-output", "{}".getBytes(StandardCharsets.UTF_8)))
                .addOutput(new ByteArrayProperty("attachment-output", "pdf".getBytes(StandardCharsets.UTF_8)));

        TestRunReport run = newTestRun("run-outputs").addStepRunReport(step);
        TestReport report = newReport("R-outputs", run);

        StepOutputProjection projection = new StepOutputProjection(
                List.of(ItemPlan.of(new DatahouseMaestroItem()
                        .setType("STEP_OUTPUT")
                        .setContent("{\"payload\":\"json\"}"))),
                List.of(AttachmentPlan.forOutput(
                        new DatahouseMaestroAttachment()
                                .setFilename("attachment-output.txt")
                                .setType("text/plain")
                                .setContent("payload".getBytes(StandardCharsets.UTF_8)),
                        1,
                        (itemId, itemUrl) -> new ByteArrayItemProperty("attachment-output", null)
                                .setReference("/attachments/" + itemId),
                        "attachment-output",
                        "STEP_OUTPUT_ATTACHMENT"
                )),
                List.of(new PropertyMutation(
                        0,
                        0,
                        (itemId, itemUrl) -> new ByteArrayItemProperty("json-output", null).setReference(itemUrl)
                )),
                List.of()
        );
        dissectorProvider.register(step.getStepId(), projection);

        ItemReference reportRef = service.recordTestReportWithReferences(report);

        assertThat(recordItemClientMock.recordedItems).hasSize(2);
        assertThat(recordItemClientMock.uploadedAttachmentsBatches).hasSize(1);
        assertThat(recordItemClientMock.uploadedAttachmentsBatches.get(0)).hasSize(1);
        assertThat(reportRef.id()).isEqualTo("itm-2");
        assertThat(reportRef.location()).isEqualTo("http://dh/items/itm-2");

        List<Property> outputs = firstStep(report).getOutputs();
        assertThat(outputs).hasSize(2);
        assertBinaryAttachment(outputs.get(0), "http://dh/items/itm-1");
        assertBinaryAttachment(outputs.get(1), "/attachments/att-1");
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
        assertThat(attachment.getValue()).isNull();
    }

    private static final class TestStepOutputDissectorProvider implements StepOutputDissectorProvider {

        private final Map<String, StepOutputProjection> projectionsById = new HashMap<>();

        void register(String stepId, StepOutputProjection projection) {
            projectionsById.put(Objects.requireNonNull(stepId, "stepId"), Objects.requireNonNull(projection, "projection"));
        }

        @Override
        public StepOutputDissector resolve(StepRunReport stepRunReport) {
            StepOutputProjection projection = projectionsById.get(stepRunReport.getStepId());
            return new StaticStepOutputDissector(projection != null ? projection : StepOutputProjection.empty());
        }

        private record StaticStepOutputDissector(StepOutputProjection projection) implements StepOutputDissector {
            @Override
            public StepOutputProjection dissect(StepOutputContext context) {
                return projection;
            }
        }
    }
}
