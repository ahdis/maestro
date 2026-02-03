# Step Output Dissection (v2)

The v2 dissection API lets a step describe how its execution outputs should be persisted without exposing index
management or recorder internals. A step declares a `StepOutputPlanDissector` that analyzes the `StepRunReport`
and returns a `StepOutputPlan`.

## High-level flow

1. The step executes and populates its `StepRunReport` outputs (typically inline JSON or binaries).
2. `StepOutputPlanDissector#dissect(StepOutputPlanContext)` inspects the report and builds a plan.
3. The recorder persists `ItemPlan` and `AttachmentPlan` objects, then runs the declared mutations to:
   * update references inside item payloads, and
   * replace inline outputs with `ByteArrayItemProperty` references.

## Plan concepts

* `ItemPlan<T>`: describes a Datahouse item to persist from a business object.
* `AttachmentPlan`: describes a binary attachment to upload.
* `ReferencePlan`: describes a reference between items, attachments, or URLs.
* `FutureReferencePlan<T>`: pairs a `ReferencePlan` with an optional mutation on the containing item object.

### DX helpers (v2)

* `ItemPlan#withReference(...)` adds an item or attachment reference (with optional mutation).
* `ItemPlan#addDetachedAttachment(...)` creates an `AttachmentPlan` and wires it as a reference.
* `StepOutputPlanContext#output(...)` finds an output by name and type.
* `StepOutputPlanContext#replaceOutput(...)` replaces an output by name without manual index updates.

## Example (validation report)

```java
public class ValidationStepOutputDissector implements StepOutputPlanDissector {

   @Override
   public StepOutputPlan dissect(StepOutputPlanContext context) {
      if (context == null || context.stepRunReport() == null) {
         return StepOutputPlan.empty();
      }

      ByteArrayProperty reportProperty =
            context.output(ValidationStepDefinition.REPORT, ByteArrayProperty.class);
      if (reportProperty == null) {
         return StepOutputPlan.empty();
      }

      ValidationReport report = deserializeReport(reportProperty.getValue());
      ItemPlan<ValidationReport> reportPlan =
            new ItemPlan<>(ValidationStepDefinition.REPORT, report, new ValidationReportMarshaller());

      for (Input input : report.getInputs()) {
         if (input.getContent() != null) {
            reportPlan.addDetachedAttachment(
                  input.getId(),
                  "VALIDATION_INPUT",
                  input.getContent(),
                  input.getId(),
                  (mutated, refName, attachmentId) -> {
                     input.setItemId(attachmentId);
                     input.setLocation("/attachments/" + attachmentId);
                     input.setContent(null);
                  }
            );
         }
      }

      FutureReferencePlan<TestReport> reportReference =
            new FutureReferencePlan<>(
                  ReferencePlan.forItem(ValidationStepDefinition.REPORT, reportPlan),
                  (testReport, refName, itemId) -> context.replaceOutput(
                        ValidationStepDefinition.REPORT,
                        output -> new ByteArrayItemProperty(reportProperty)
                              .setValue(null)
                              .setItemType(ValidationStepDefinition.REPORT)
                              .setReference(context.datahouseUrl() + "/items/" + itemId)
                  )
            );

      return new StepOutputPlan(List.of(reportReference));
   }
}
```

## Tips

* Always null-check the context and its report; return `StepOutputPlan.empty()` when nothing must be persisted.
* Prefer `addDetachedAttachment` over manual `AttachmentPlan` wiring.
* Use `context.replaceOutput(...)` to keep output replacement index-free.
* Keep mutations focused on the item instance passed to the mutation; do not assume hidden global state.
