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

import java.util.Objects;

/**
 * Instructs the persistence layer to add a reference on an item once a related entry has been persisted.
 *
 * @param referencingItemIndex index of the item receiving the reference inside {@link StepOutputProjection#items()}
 * @param referenceName        name used in the resulting {@link MaestroReference}
 * @param referenceType        type used in the resulting {@link MaestroReference}
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
