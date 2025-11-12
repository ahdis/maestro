package net.ihe.gazelle.maestro.api.business.testreport.dissection;

import net.ihe.gazelle.maestro.api.business.recording.MaestroRefType;

import java.util.Objects;

/**
 * Instructs the persistence layer to add a reference from a parent item to the
 * current item once its identifier is known.
 *
 * @param parentItemIndex index of the parent entry inside {@link StepOutputProjection#items()}
 * @param referenceName   name for the {@link net.ihe.gazelle.maestro.api.business.recording.MaestroReference}
 * @param referenceType   type for the {@link net.ihe.gazelle.maestro.api.business.recording.MaestroReference}
 * @param refType         reference classification (defaults to {@link MaestroRefType#ITEM_ID})
 */
public record ItemReferenceBinding(
      int parentItemIndex,
      String referenceName,
      String referenceType,
      MaestroRefType refType
) {

   public ItemReferenceBinding {
      if (parentItemIndex < 0) {
         throw new IllegalArgumentException("parentItemIndex must be >= 0");
      }
      referenceName = Objects.requireNonNull(referenceName, "referenceName");
      referenceType = Objects.requireNonNull(referenceType, "referenceType");
      refType = Objects.requireNonNullElse(refType, MaestroRefType.ITEM_ID);
   }
}
