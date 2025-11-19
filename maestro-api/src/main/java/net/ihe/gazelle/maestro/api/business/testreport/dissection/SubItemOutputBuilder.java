package net.ihe.gazelle.maestro.api.business.testreport.dissection;

import net.ihe.gazelle.maestro.api.business.recording.MaestroItem;
import net.ihe.gazelle.maestro.api.business.recording.MaestroRefType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builder describing a nested item linked to another item inside the same projection.
 */
public final class SubItemOutputBuilder implements GraphNodeBuilder {

   private final MaestroItem item;
   private final GraphNodeBuilder referencingNode;
   private final String referenceName;
   private final String referenceType;
   private final MaestroRefType refType;
   private final List<AttachmentOutputBuilder> attachments = new ArrayList<>();
   private final List<SubItemOutputBuilder> subItems = new ArrayList<>();
   private final List<PropertyReplacement> propertyReplacements = new ArrayList<>();

   SubItemOutputBuilder(MaestroItem item,
         GraphNodeBuilder referencingNode,
         String referenceName,
         String referenceType,
         MaestroRefType refType) {
      this.item = Objects.requireNonNull(item, "item");
      this.referencingNode = Objects.requireNonNull(referencingNode, "referencingNode");
      this.referenceName = Objects.requireNonNull(referenceName, "referenceName");
      this.referenceType = Objects.requireNonNull(referenceType, "referenceType");
      this.refType = Objects.requireNonNullElse(refType, MaestroRefType.ITEM_ID);
   }

   /**
    * Adds an attachment scoped to this sub item.
    */
   public SubItemOutputBuilder addAttachment(AttachmentOutputBuilder attachmentBuilder) {
      attachments.add(StepOutputGraphBuilder.requireAttachment(attachmentBuilder));
      return this;
   }

   /**
    * Adds several attachments scoped to this sub item.
    */
   public SubItemOutputBuilder addAttachments(List<AttachmentOutputBuilder> attachmentBuilders) {
      if (attachmentBuilders != null) {
         attachmentBuilders.forEach(this::addAttachment);
      }
      return this;
   }

   /**
    * Registers a property mutation targeting this sub item.
    */
   public SubItemOutputBuilder addPropertyReplacement(int outputIndex, PropertyReferenceFactory factory) {
      if (outputIndex < 0) {
         throw new IllegalArgumentException("outputIndex must be >= 0");
      }
      propertyReplacements.add(new PropertyReplacement(outputIndex, Objects.requireNonNull(factory, "factory")));
      return this;
   }

   /**
    * Adds a nested sub item.
    */
   public SubItemOutputBuilder addSubItem(MaestroItem item,
         String referenceName,
         String referenceType) {
      return addSubItem(item, referenceName, referenceType, MaestroRefType.ITEM_ID);
   }

   /**
    * Adds a nested sub item.
    */
   public SubItemOutputBuilder addSubItem(MaestroItem item,
         String referenceName,
         String referenceType,
         MaestroRefType refType) {
      SubItemOutputBuilder child = new SubItemOutputBuilder(item, this, referenceName, referenceType, refType);
      subItems.add(child);
      return child;
   }

   GraphNodeBuilder referencingNode() {
      return referencingNode;
   }

   String referenceName() {
      return referenceName;
   }

   String referenceType() {
      return referenceType;
   }

   MaestroRefType refType() {
      return refType;
   }

   @Override
   public MaestroItem item() {
      return item;
   }

   @Override
   public List<AttachmentOutputBuilder> attachments() {
      return attachments;
   }

   @Override
   public List<SubItemOutputBuilder> subItems() {
      return subItems;
   }

   @Override
   public List<PropertyReplacement> propertyReplacements() {
      return propertyReplacements;
   }
}
