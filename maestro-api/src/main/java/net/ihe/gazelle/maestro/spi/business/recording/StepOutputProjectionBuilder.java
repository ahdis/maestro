package net.ihe.gazelle.maestro.spi.business.recording;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Aggregates the graph of {@link ItemOutputBuilder} nodes before flattening it into a {@link StepOutputProjection}.
 */
public final class StepOutputProjectionBuilder {

   private final List<ItemOutputBuilder> rootItems = new ArrayList<>();
   private final List<MaestroReference> reportReferences = new ArrayList<>();
   private final StepOutputProjectionFlattener flattener;

   public StepOutputProjectionBuilder() {
      this(new StepOutputProjectionFlattener());
   }

   StepOutputProjectionBuilder(StepOutputProjectionFlattener flattener) {
      this.flattener = Objects.requireNonNull(flattener, "flattener");
   }

   /**
    * Registers a new root item and returns the builder so callers can configure it.
    */
   public ItemOutputBuilder addItem(MaestroItem item) {
      ItemOutputBuilder builder = new ItemOutputBuilder(item);
      rootItems.add(builder);
      return builder;
   }

   /**
    * Adds a raw reference that must be appended to the enclosing TestReport.
    */
   public StepOutputProjectionBuilder addReportReference(MaestroReference reference) {
      reportReferences.add(Objects.requireNonNull(reference, "reference"));
      return this;
   }

   /**
    * Flattens the graph into a projection consumable by the persistence layer.
    */
   public StepOutputProjection build() {
      return flattener.flatten(rootItems, reportReferences);
   }

   static AttachmentOutputBuilder requireAttachment(AttachmentOutputBuilder builder) {
      return Objects.requireNonNull(builder, "attachmentBuilder");
   }
}
