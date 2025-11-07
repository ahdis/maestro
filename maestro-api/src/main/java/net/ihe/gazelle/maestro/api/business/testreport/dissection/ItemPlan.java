package net.ihe.gazelle.maestro.api.business.testreport.dissection;

import com.kereval.gazelle.datahouse.api.business.record.Item;

import java.util.List;
import java.util.Objects;

/**
 * Description of an item that must be persisted in Datahouse.
 *
 * @param item             fully populated item ready to be persisted
 * @param parentBindings   relations to parent items within the same projection.
 *                         A binding is evaluated once the current item has been persisted.
 *                         Implementations must ensure the referenced parent item appears
 *                         later in {@link StepOutputProjection#items()} so that the parent
 *                         can still be mutated before it is recorded.
 * @param reportBindings   references that should be added to the enclosing TestReport
 *                         when the current item is persisted.
 */
public record ItemPlan(
      Item item,
      List<ItemReferenceBinding> parentBindings,
      List<ReportReferenceBinding> reportBindings
) {

   public ItemPlan {
      Objects.requireNonNull(item, "item");
      parentBindings = List.copyOf(Objects.requireNonNullElse(parentBindings, List.of()));
      reportBindings = List.copyOf(Objects.requireNonNullElse(reportBindings, List.of()));
   }

   public static ItemPlan of(Item item) {
      return new ItemPlan(item, List.of(), List.of());
   }
}
