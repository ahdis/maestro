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

/**
 * Instruction allowing the persistence layer to replace a step output with a
 * reference to a freshly persisted item.
 *
 * @param outputIndex     index of the output to replace
 * @param sourceItemIndex index of the {@link ItemPlan} providing the item identifier
 * @param factory         factory creating the replacement property
 */
public record PropertyMutation(
      int outputIndex,
      int sourceItemIndex,
      PropertyReferenceFactory factory
) {

   public PropertyMutation {
      if (outputIndex < 0) {
         throw new IllegalArgumentException("outputIndex must be >= 0");
      }
      if (sourceItemIndex < 0) {
         throw new IllegalArgumentException("sourceItemIndex must be >= 0");
      }
      if (factory == null) {
         throw new IllegalArgumentException("factory must not be null");
      }
   }
}
