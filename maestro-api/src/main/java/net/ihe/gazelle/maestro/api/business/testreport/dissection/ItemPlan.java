package net.ihe.gazelle.maestro.api.business.testreport.dissection;

import net.ihe.gazelle.maestro.api.business.recording.MaestroItem;

import java.util.List;
import java.util.Objects;

/**
 * Description of an item that must be persisted by the recording implementation.
 *
 * @param item             fully populated descriptor ready to be converted before persistence
 * @param parentBindings   relations to parent items within the same projection.
 *                         A binding is evaluated once the current item has been persisted.
 *                         Implementations must ensure the referenced parent item appears
 *                         later in {@link StepOutputProjection#items()} so that the parent
 *                         can still be mutated before it is recorded.
 * @param reportBindings   references that should be added to the enclosing TestReport
 *                         when the current item is persisted.
 */
public record ItemPlan(
      MaestroItem item,
      List<ItemReferenceBinding> parentBindings,
      List<ReportReferenceBinding> reportBindings
) {

   public ItemPlan {
      Objects.requireNonNull(item, "item");
      parentBindings = List.copyOf(Objects.requireNonNullElse(parentBindings, List.of()));
      reportBindings = List.copyOf(Objects.requireNonNullElse(reportBindings, List.of()));
   }

   public static ItemPlan of(MaestroItem item) {
      return new ItemPlan(item, List.of(), List.of());
   }
}
