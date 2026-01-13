/*
 * Copyright 2026 IHE International.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestro.spi.business.recording;

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
