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

   AttachmentPlan build() {
      if (referenceName == null || referenceType == null) {
         throw new IllegalStateException("referenceName and referenceType must be provided");
      }
      int planOutputIndex = propertyFactory == null ? -1 : outputIndex;
      return new AttachmentPlan(attachment, planOutputIndex, propertyFactory, referenceName, referenceType);
   }
}
