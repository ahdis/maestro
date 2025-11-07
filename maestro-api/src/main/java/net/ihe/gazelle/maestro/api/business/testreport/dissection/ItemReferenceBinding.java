package net.ihe.gazelle.maestro.api.business.testreport.dissection;

import com.kereval.gazelle.datahouse.api.business.record.RefType;

import java.util.Objects;

/**
 * Instructs the persistence layer to add a reference from a parent item to the
 * current item once its identifier is known.
 *
 * @param parentItemIndex index of the parent entry inside {@link StepOutputProjection#items()}
 * @param referenceName   value for {@link com.kereval.gazelle.datahouse.api.business.record.Reference#setName(String)}
 * @param referenceType   value for {@link com.kereval.gazelle.datahouse.api.business.record.Reference#setType(String)}
 * @param refType         Datahouse reference type (defaults to {@link RefType#ITEM_ID})
 */
public record ItemReferenceBinding(
      int parentItemIndex,
      String referenceName,
      String referenceType,
      RefType refType
) {

   public ItemReferenceBinding {
      if (parentItemIndex < 0) {
         throw new IllegalArgumentException("parentItemIndex must be >= 0");
      }
      referenceName = Objects.requireNonNull(referenceName, "referenceName");
      referenceType = Objects.requireNonNull(referenceType, "referenceType");
      refType = Objects.requireNonNullElse(refType, RefType.ITEM_ID);
   }
}
