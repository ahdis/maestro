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
