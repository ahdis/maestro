package net.ihe.gazelle.maestro.api.business.testreport.dissection;

import java.util.Map;
import java.util.Objects;

/**
 * Resolved references available while mutating item content.
 *
 * @param itemIdsByReferenceName       map of reference name to persisted item id
 * @param attachmentIdsByReferenceName map of reference name to persisted attachment id
 */
public record ContentMutationContext(
      Map<String, String> itemIdsByReferenceName,
      Map<String, String> attachmentIdsByReferenceName,
      String datahouseUrl
) {

   public ContentMutationContext {
      itemIdsByReferenceName = Map.copyOf(Objects.requireNonNullElse(itemIdsByReferenceName, Map.of()));
      attachmentIdsByReferenceName = Map.copyOf(Objects.requireNonNullElse(attachmentIdsByReferenceName, Map.of()));
   }

   public String findItemId(String referenceName) {
      return itemIdsByReferenceName.get(referenceName);
   }

   public String findAttachmentId(String referenceName) {
      return attachmentIdsByReferenceName.get(referenceName);
   }
}
