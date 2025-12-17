package net.ihe.gazelle.maestro.api.business.testreport;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;

import java.io.Serial;
import java.io.Serializable;

public class ByteArrayItemProperty extends ByteArrayProperty {

    @Serial
    private static final long serialVersionUID = 7729334463586120850L;

   private String itemType;

   public ByteArrayItemProperty() {
      super();
   }

   public ByteArrayItemProperty(String name, Serializable value) {
      super(name, value);
   }

   public ByteArrayItemProperty(ByteArrayProperty property) {
        super(property.getName(), property.getValue());
        super.setFileName(property.getFileName());
        super.setMimeType(property.getMimeType());
   }

   private String reference;

   public String getItemType() {
      return itemType;
   }

   public ByteArrayItemProperty setItemType(String itemType) {
      this.itemType = itemType;
      return this;
   }

   public String getReference() {
      return reference;
   }

   public ByteArrayItemProperty setReference(String reference) {
      this.reference = reference;
      return this;
   }

   public boolean isReferenceDefinedIfValueNull() {
      return getValue() != null || reference != null;
   }

}
