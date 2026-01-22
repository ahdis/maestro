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

import java.util.List;
import java.util.Objects;

/**
 * Result produced by a {@link StepOutputDissector}.
 *
 * @param items             ordered list of descriptors describing the items to persist
 * @param attachments       binary payloads that must become attachments
 * @param propertyMutations instructions to update step outputs once item ids are known
 * @param reportReferences  references that must be appended to the enclosing TestReport item
 */
public record StepOutputProjection(
      List<ItemPlan> items,
      List<AttachmentPlan> attachments,
      List<PropertyMutation> propertyMutations,
      List<MaestroReference> reportReferences
) {

   public StepOutputProjection {
      items = List.copyOf(Objects.requireNonNullElse(items, List.of()));
      attachments = List.copyOf(Objects.requireNonNullElse(attachments, List.of()));
      propertyMutations = List.copyOf(Objects.requireNonNullElse(propertyMutations, List.of()));
      reportReferences = List.copyOf(Objects.requireNonNullElse(reportReferences, List.of()));
   }

   public static StepOutputProjection empty() {
      return new StepOutputProjection(List.of(), List.of(), List.of(), List.of());
   }
}
