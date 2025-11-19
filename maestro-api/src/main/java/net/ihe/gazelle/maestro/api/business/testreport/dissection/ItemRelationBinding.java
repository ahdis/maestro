package net.ihe.gazelle.maestro.api.business.testreport.dissection;

import net.ihe.gazelle.maestro.api.business.recording.MaestroRefType;

import java.util.Objects;

/**
 * Instructs the persistence layer to add a reference on an item once a related entry has been persisted.
 *
 * @param referencingItemIndex index of the item receiving the reference inside {@link StepOutputProjection#items()}
 * @param referenceName        name used in the resulting {@link net.ihe.gazelle.maestro.api.business.recording.MaestroReference}
 * @param referenceType        type used in the resulting {@link net.ihe.gazelle.maestro.api.business.recording.MaestroReference}
 * @param refType              classification of the reference (defaults to {@link MaestroRefType#ITEM_ID})
 */
public record ItemRelationBinding(
      int referencingItemIndex,
      String referenceName,
      String referenceType,
      MaestroRefType refType
) {

   public ItemRelationBinding {
      if (referencingItemIndex < 0) {
         throw new IllegalArgumentException("referencingItemIndex must be >= 0");
      }
      refType = Objects.requireNonNullElse(refType, MaestroRefType.ITEM_ID);
   }
}
