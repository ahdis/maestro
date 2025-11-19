# Step Output Dissection API

The dissection API lets a step describe how its execution outputs should be persisted. A step declares a `StepOutputDissector` that analyzes the `StepRunReport` and returns a `StepOutputProjection`. The projection describes:

* Items to record in Datahouse (`ItemPlan` entries)
* Attachments to upload (`AttachmentPlan`)
* Property mutations required to rewrite step outputs (`PropertyMutation`)
* References to append to the TestReport

The recording engine consumes the projection and performs the actual persistence. This keeps step code focused on modeling the persisted structure rather than on Datahouse-specific details.

## High-level flow

1. The step executes and populates its `StepRunReport` with inline outputs (typically JSON, binaries, etc.).
2. `StepOutputDissector#dissect(StepOutputContext)` inspects the report and uses the builder API to describe the persisted items/attachments.
3. The returned `StepOutputProjection` is passed to `StepOutputRecordService`, which:
   * Persists `ItemPlan`s in dependency order and wires their references using `ItemRelationBinding`.
   * Uploads attachments and rewrites step outputs using the `PropertyMutation` definitions.
   * Adds report-level references described by `ReferenceBinding`.

## Builder API overview

`StepOutputGraphBuilder` is the entry point for creating a projection. Callers register root items (top-level entries that should be persisted) and configure attachments, child items, and property replacements via fluent builders.

```java
StepOutputGraphBuilder graph = new StepOutputGraphBuilder();
ItemOutputBuilder root = graph.addItem(maestroItem);
root.addReferenceBinding(new ReferenceBinding("REPORT", "REPORT", MaestroRefType.ITEM_ID));
root.addPropertyReplacement(reportOutputIndex, propertyFactory);

SubItemOutputBuilder child = root.addSubItem(childItem, "link-to-child", "CHILD_TYPE");
child.addAttachment(new AttachmentOutputBuilder(attachment)
      .withReference("child-attachment", "PDF")
      .replaceOutput(attachmentOutputIndex, attachmentFactory));

StepOutputProjection projection = graph.build();
```

### Item builders

* `ItemOutputBuilder` represents a root item. It allows:
  * Adding `AttachmentOutputBuilder`s scoped to that item.
  * Defining property replacements via `addPropertyReplacement(int outputIndex, PropertyReferenceFactory factory)`.
  * Registering report-level references (`addReferenceBinding(ReferenceBinding)`).
  * Linking sub-items (`addSubItem(MaestroItem item, String referenceName, String referenceType, MaestroRefType refType)`).
* `SubItemOutputBuilder` represents a nested item. It supports the same attachment/property calls but does not expose report bindings because sub items cannot reference the TestReport directly.
* Both builders automatically track relations through `ItemRelationBinding`. The builder graph is flattened with a depth-first topological sort so children are persisted before their referencing items.

### Attachment builders

`AttachmentOutputBuilder` describes an attachment upload:

```java
graph.addItem(report)
     .addAttachment(new AttachmentOutputBuilder(maestroAttachment)
           .withReference("validation-input", "VALIDATION_INPUT"));
```

Calling `replaceOutput(index, factory)` configures the attachment to also rewrite a step output once the upload is persisted (similar to `PropertyMutation` for items).

### Property mutations

`addPropertyReplacement` on item builders describes how to replace a `StepRunReport` output with a reference to the persisted item. The `PropertyReferenceFactory` has access to the persisted item id and URL so it can rebuild the property (e.g., convert an inline `ByteArrayProperty` to a `ByteArrayItemProperty` pointing to Datahouse).

### Top-level references

Use `StepOutputGraphBuilder#addReportReference(MaestroReference)` to inject raw references into the enclosing TestReport (rare case) or `ItemOutputBuilder#addReferenceBinding` to record references when a specific item is persisted (most common case).

## Implementing a dissector

1. Implement the `StepOutputDissector` interface. Inspect the `StepRunReport` from the `StepOutputContext` and determine whether there’s anything to persist.
2. Build a `StepOutputGraphBuilder`. For each logical record you want to store:
   * Create a `MaestroItem` (typically a `DatahouseMaestroItem`) containing the serialized data and references.
   * Register it as a root or sub item.
   * Describe property replacements and attachments as needed.
3. Return `graph.build()`. If the dissector has nothing to persist, return `StepOutputProjection.empty()` to avoid unnecessary work.

### Example: validation report dissector (simplified)

```java
public class ValidationStepOutputDissector implements StepOutputDissector {

   @Override
   public StepOutputProjection dissect(StepOutputContext context) {
      StepRunReport stepReport = context.stepRunReport();
      int reportIndex = findOutputIndex(stepReport, ValidationStepDefinition.REPORT);
      if (reportIndex < 0) {
         return StepOutputProjection.empty();
      }

      ValidationReport report = deserializeReport((ByteArrayProperty) stepReport.getOutputs().get(reportIndex));
      StepOutputGraphBuilder graph = new StepOutputGraphBuilder();
      ItemOutputBuilder reportItem = graph.addItem(
            new DatahouseMaestroItem()
                  .setType(ValidationStepDefinition.REPORT)
                  .setContent(serialize(report))
                  .setAccessControlList(context.accessControlList())
                  .setReferences(new ArrayList<>())
      );

      // Link to TestReport
      reportItem.addReferenceBinding(new ReferenceBinding(
            ValidationStepDefinition.REPORT,
            ValidationStepDefinition.REPORT,
            MaestroRefType.ITEM_ID
      ));

      // Replace inline payload with a reference
      reportItem.addPropertyReplacement(reportIndex, (itemId, url) -> {
         ByteArrayItemProperty property = new ByteArrayItemProperty(ValidationStepDefinition.REPORT, null);
         return property.setReference(url);
      });

      // Attach each input with content
      extractInputAttachments(report).forEach(reportItem::addAttachment);

      return graph.build();
   }
}
```

## Tips for building dissectors

* Always null-check the `StepOutputContext` and its fields. Return `StepOutputProjection.empty()` if you have nothing to persist.
* Ensure the data you serialize is immutable and does not reference mutable objects from the execution context.
* Use `addPropertyReplacement` only for outputs that you intend to replace (e.g., the original inline JSON). Keep attachments detached unless you need to mutate an output property.
* Take advantage of `SubItemOutputBuilder` for nested structures: calling `parent.addSubItem(...)` automatically creates the `ItemRelationBinding`, so you don’t need to manage parent indexes manually.
* When building attachments or items, make sure to populate `MaestroItem#setReferences` and `AttachmentOutputBuilder#withReference` so downstream services can wire TestReport relations correctly.

## Validation and error handling

`StepOutputGraphBuilder.build()` or the `ValidationReportBuilder` invoked by downstream components may throw `IllegalArgumentException` if the generated structure is invalid (e.g., location formats fail validation, required fields are missing). When writing tests for your dissector, replicate typical reports to ensure your structures satisfy the validators.

## References

* `maestro-api`: builder interfaces, projection records, and step SPI.
* `maestro-recording`: persistence service that consumes `StepOutputProjection`.
* `simulation-step/validation-step`: example of dissectors implementations (`ValidationStepOutputDissector`, `SimulationStepOutputDissector`), plus tests demonstrating the builder API.

Use the tests in `net.ihe.gazelle.maestro.recording.StaticStepOutputDissector` as reference when writing your own dissectors.
