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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Aggregates the graph of {@link ItemOutputBuilder} nodes before flattening it into a {@link StepOutputProjection}.
 */
public final class StepOutputProjectionBuilder {

   private final List<ItemOutputBuilder> rootItems = new ArrayList<>();
   private final List<MaestroReference> reportReferences = new ArrayList<>();
   private final StepOutputProjectionFlattener flattener;

   public StepOutputProjectionBuilder() {
      this(new StepOutputProjectionFlattener());
   }

   StepOutputProjectionBuilder(StepOutputProjectionFlattener flattener) {
      this.flattener = Objects.requireNonNull(flattener, "flattener");
   }

   /**
    * Registers a new root item and returns the builder so callers can configure it.
    */
   public ItemOutputBuilder addItem(MaestroItem item) {
      ItemOutputBuilder builder = new ItemOutputBuilder(item);
      rootItems.add(builder);
      return builder;
   }

   /**
    * Adds a raw reference that must be appended to the enclosing TestReport.
    */
   public StepOutputProjectionBuilder addReportReference(MaestroReference reference) {
      reportReferences.add(Objects.requireNonNull(reference, "reference"));
      return this;
   }

   /**
    * Flattens the graph into a projection consumable by the persistence layer.
    */
   public StepOutputProjection build() {
      return flattener.flatten(rootItems, reportReferences);
   }

   static AttachmentOutputBuilder requireAttachment(AttachmentOutputBuilder builder) {
      return Objects.requireNonNull(builder, "attachmentBuilder");
   }
}
