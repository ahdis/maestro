package net.ihe.gazelle.maestro.spi.business.recording;

import java.util.List;
import java.util.Objects;

/**
 * Description of an item that must be persisted by the recording implementation.
 *
 * @param item             fully populated descriptor ready to be converted before persistence
 * @param relationBindings instructions telling the persistence layer which item must receive
 *                         a reference once the current entry has been persisted. The target
 *                         item index must come later in {@link StepOutputProjection#items()}
 *                         so that its references can still be updated before it is recorded.
 * @param reportBindings   references that should be added to the enclosing TestReport
 *                         when the current item is persisted.
 * @param contentMutator   optional hook to adjust the content once related ids are known
 */
public record ItemPlan(
      MaestroItem item,
      List<ItemRelationBinding> relationBindings,
      List<ReferenceBinding> reportBindings,
      ContentMutator contentMutator
) {

   public ItemPlan {
      Objects.requireNonNull(item, "item");
      relationBindings = List.copyOf(Objects.requireNonNullElse(relationBindings, List.of()));
      reportBindings = List.copyOf(Objects.requireNonNullElse(reportBindings, List.of()));
   }

   public static ItemPlan of(MaestroItem item) {
      return new ItemPlan(item, List.of(), List.of(), null);
   }
}
