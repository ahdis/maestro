package net.ihe.gazelle.maestro.spi.business.recording;

import java.util.Objects;

/**
 * Description of an attachment that must be uploaded by the chosen recording implementation.
 *
 * @param attachment     payload to upload
 * @param owningItemIndex index of the item (inside {@link StepOutputProjection#items()}) that owns this attachment
 * @param outputIndex    index of the related output inside {@link net.ihe.gazelle.maestro.api.business.testreport.StepRunReport#getOutputs()}
 * @param propertyFactory factory used to build the replacement property once the attachment id is known
 *                        (may be {@code null} if no step output has to be mutated)
 * @param referenceName  name used inside {@link MaestroReference}
 * @param referenceType  type used inside {@link MaestroReference}
 */
public record AttachmentPlan(
      MaestroAttachment attachment,
      int owningItemIndex,
      int outputIndex,
      PropertyReferenceFactory propertyFactory,
      String referenceName,
      String referenceType
) {

   public AttachmentPlan {
      Objects.requireNonNull(attachment, "attachment");
      if (owningItemIndex < 0) {
         throw new IllegalArgumentException("owningItemIndex must be >= 0");
      }
      if (outputIndex < 0 && propertyFactory != null) {
         throw new IllegalArgumentException("outputIndex must be >= 0 when a property factory is provided");
      }
      Objects.requireNonNull(referenceName, "referenceName");
      Objects.requireNonNull(referenceType, "referenceType");
   }

   public static AttachmentPlan forOutput(
         MaestroAttachment attachment,
         int owningItemIndex,
         int outputIndex,
         PropertyReferenceFactory propertyFactory,
         String referenceName,
         String referenceType) {
      return new AttachmentPlan(attachment, owningItemIndex, outputIndex, propertyFactory, referenceName, referenceType);
   }

   public static AttachmentPlan detached(MaestroAttachment attachment,
         int owningItemIndex,
         String referenceName,
         String referenceType) {
      return new AttachmentPlan(attachment, owningItemIndex, -1, null, referenceName, referenceType);
   }
}
