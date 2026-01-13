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
 * Builder describing an attachment upload and optional Property mutation.
 */
public final class AttachmentOutputBuilder {

   private final MaestroAttachment attachment;
   private int outputIndex = -1;
   private PropertyReferenceFactory propertyFactory;
   private String referenceName;
   private String referenceType;

   public AttachmentOutputBuilder(MaestroAttachment attachment) {
      this.attachment = Objects.requireNonNull(attachment, "attachment");
   }

   public AttachmentOutputBuilder withReference(String referenceName, String referenceType) {
      this.referenceName = Objects.requireNonNull(referenceName, "referenceName");
      this.referenceType = Objects.requireNonNull(referenceType, "referenceType");
      return this;
   }

   public AttachmentOutputBuilder replaceOutput(int outputIndex, PropertyReferenceFactory factory) {
      if (outputIndex < 0) {
         throw new IllegalArgumentException("outputIndex must be >= 0");
      }
      this.outputIndex = outputIndex;
      this.propertyFactory = Objects.requireNonNull(factory, "factory");
      return this;
   }

   AttachmentPlan build(int owningItemIndex) {
      if (referenceName == null || referenceType == null) {
         throw new IllegalStateException("referenceName and referenceType must be provided");
      }
      int planOutputIndex = propertyFactory == null ? -1 : outputIndex;
      if (owningItemIndex < 0) {
         throw new IllegalArgumentException("owningItemIndex must be >= 0");
      }
      return new AttachmentPlan(attachment, owningItemIndex, planOutputIndex, propertyFactory, referenceName, referenceType);
   }
}
