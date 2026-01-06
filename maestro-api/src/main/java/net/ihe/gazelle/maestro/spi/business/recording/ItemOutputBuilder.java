package net.ihe.gazelle.maestro.spi.business.recording;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builder describing a root item inside the step output projection.
 */
public final class ItemOutputBuilder implements GraphNodeBuilder {

   private final MaestroItem item;
   private final List<AttachmentOutputBuilder> attachments = new ArrayList<>();
   private final List<SubItemOutputBuilder> subItems = new ArrayList<>();
   private final List<PropertyReplacement> propertyReplacements = new ArrayList<>();
   private final List<ReferenceBinding> reportBindings = new ArrayList<>();
   private ContentMutator contentMutator;

   ItemOutputBuilder(MaestroItem item) {
      this.item = Objects.requireNonNull(item, "item");
   }

   /**
    * Adds an attachment scoped to this item.
    */
   public ItemOutputBuilder addAttachment(AttachmentOutputBuilder attachmentBuilder) {
      attachments.add(StepOutputProjectionBuilder.requireAttachment(attachmentBuilder));
      return this;
   }

   /**
    * Adds several attachments scoped to this item.
    */
   public ItemOutputBuilder addAttachments(List<AttachmentOutputBuilder> attachmentBuilders) {
      if (attachmentBuilders != null) {
         attachmentBuilders.forEach(this::addAttachment);
      }
      return this;
   }

   /**
    * Registers a property mutation targeting this item.
    */
   public ItemOutputBuilder addPropertyReplacement(String outputName, List<Property> outputs, PropertyReferenceFactory factory) {
      if (outputs == null || outputs.isEmpty()) {
         throw new IllegalArgumentException("outputs must be provided to add a property replacement");
      }
      int outputIndex = findOutputByName(outputName, outputs);
      propertyReplacements.add(new PropertyReplacement(outputIndex, Objects.requireNonNull(factory, "factory")));
      return this;
   }

   /**
    * Adds a reference binding that must be appended to the enclosing TestReport.
    * We keep this separated so we can add item without providing a reference binding.
    */
   public ItemOutputBuilder addReferenceBinding(ReferenceBinding binding) {
      reportBindings.add(Objects.requireNonNull(binding, "binding"));
      return this;
   }

   /**
    * Registers a content mutator that will be invoked once referenced identifiers are known.
    */
   public ItemOutputBuilder mutateContent(ContentMutator mutator) {
      this.contentMutator = Objects.requireNonNull(mutator, "mutator");
      return this;
   }

   /**
    * Adds a sub item linked to this item.
    */
   public SubItemOutputBuilder addSubItem(MaestroItem item,
         String referenceName,
         String referenceType) {
      return addSubItem(item, referenceName, referenceType, MaestroRefType.ITEM_ID);
   }

   /**
    * Adds a sub item linked to this item.
    */
   public SubItemOutputBuilder addSubItem(MaestroItem item,
         String referenceName,
         String referenceType,
         MaestroRefType refType) {
      SubItemOutputBuilder child = new SubItemOutputBuilder(item, this, referenceName, referenceType, refType);
      subItems.add(child);
      return child;
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

   List<ReferenceBinding> reportBindings() {
      return reportBindings;
   }

   @Override
   public ContentMutator contentMutator() {
      return contentMutator;
   }

   private int findOutputByName(String outputName, List<Property> outputs) {
      for (int i = 0; i < outputs.size(); i++) {
         Property property = outputs.get(i);
         if (outputName.equals(property.getName()) && property instanceof ByteArrayProperty) {
            return i;
         }
      }
      return -1;
   }
}
