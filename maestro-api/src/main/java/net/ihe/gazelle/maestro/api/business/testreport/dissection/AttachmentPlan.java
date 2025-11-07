package net.ihe.gazelle.maestro.api.business.testreport.dissection;

import com.kereval.gazelle.datahouse.api.business.record.Attachment;

import java.util.Objects;

/**
 * Description of an attachment that must be uploaded to Datahouse.
 *
 * @param attachment     payload to upload
 * @param outputIndex    index of the related output inside {@link net.ihe.gazelle.maestro.api.business.testreport.StepRunReport#getOutputs()}
 * @param propertyFactory factory used to build the replacement property once the attachment id is known
 *                        (may be {@code null} if no step output has to be mutated)
 * @param referenceName  value for {@link com.kereval.gazelle.datahouse.api.business.record.Reference#setName(String)}
 * @param referenceType  value for {@link com.kereval.gazelle.datahouse.api.business.record.Reference#setType(String)}
 */
public record AttachmentPlan(
      Attachment attachment,
      int outputIndex,
      PropertyReferenceFactory propertyFactory,
      String referenceName,
      String referenceType
) {

   public AttachmentPlan {
      Objects.requireNonNull(attachment, "attachment");
      if (outputIndex < 0 && propertyFactory != null) {
         throw new IllegalArgumentException("outputIndex must be >= 0 when a property factory is provided");
      }
      Objects.requireNonNull(referenceName, "referenceName");
      Objects.requireNonNull(referenceType, "referenceType");
   }

   public static AttachmentPlan forOutput(
         Attachment attachment,
         int outputIndex,
         PropertyReferenceFactory propertyFactory,
         String referenceName,
         String referenceType) {
      return new AttachmentPlan(attachment, outputIndex, propertyFactory, referenceName, referenceType);
   }

   public static AttachmentPlan detached(
         Attachment attachment,
         String referenceName,
         String referenceType) {
      return new AttachmentPlan(attachment, -1, null, referenceName, referenceType);
   }
}
