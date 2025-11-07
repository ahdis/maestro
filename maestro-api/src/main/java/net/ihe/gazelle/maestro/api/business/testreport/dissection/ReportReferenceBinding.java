package net.ihe.gazelle.maestro.api.business.testreport.dissection;

import com.kereval.gazelle.datahouse.api.business.record.RefType;

import java.util.Objects;

/**
 * Describes a reference that must be appended to the enclosing TestReport item
 * when a given item has been persisted.
 */
public record ReportReferenceBinding(
      String referenceName,
      String referenceType,
      RefType refType
) {

   public ReportReferenceBinding {
      referenceName = Objects.requireNonNull(referenceName, "referenceName");
      referenceType = Objects.requireNonNull(referenceType, "referenceType");
      refType = Objects.requireNonNullElse(refType, RefType.ITEM_ID);
   }
}
