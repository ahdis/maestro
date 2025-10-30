package net.ihe.gazelle.maestro.api.business.testreport;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;

public class ByteArrayItemProperty extends ByteArrayProperty {

   public ByteArrayItemProperty() {
      super();
   }

   public ByteArrayItemProperty(String name, Object value) {
      super(name, value);
   }

   private String reference;

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
