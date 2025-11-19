package net.ihe.gazelle.maestro.api.business.testreport.dissection;

import net.ihe.gazelle.maestro.api.business.recording.MaestroRefType;

import java.util.Objects;

/**
 * Describes a reference that must be appended to the enclosing TestReport item
 * when a given item has been persisted.
 */
public record ReferenceBinding(
      String referenceName,
      String referenceType,
      MaestroRefType refType
) {

   public ReferenceBinding {
      refType = Objects.requireNonNullElse(refType, MaestroRefType.ITEM_ID);
   }
}
