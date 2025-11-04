package net.ihe.gazelle.maestro.api.business.testreport;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;

import java.io.Serial;

public class ByteArrayItemProperty extends ByteArrayProperty {

    @Serial
    private static final long serialVersionUID = 7729334463586120850L;

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
