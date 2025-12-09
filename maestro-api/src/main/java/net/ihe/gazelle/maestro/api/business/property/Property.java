package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;
import java.io.Serializable;

/**
 * Abstract class representing a property with a name and a value holder.
 */
public abstract class Property implements Serializable {

   @Serial
   private static final long serialVersionUID = -5410177558892768955L;

   private String name;
   private ValueHolder valueHolder;

   Property() {
   }

   Property(String name, Serializable value) {
      this.name = name;
      setValue(value);
   }

   public String getName() {
      return name;
   }

   public Property setName(String name) {
      this.name = name;
      return this;
   }

   public Property setValue(Serializable value) {
      if (value instanceof String strValue && strValue.matches(ReferenceValue.REFERENCE_REGEX)) {
         this.valueHolder = new ReferenceValue(strValue);
      } else {
         this.valueHolder = new DirectValue(value);
      }
      return this;
   }

   public <T extends Serializable> T getValue() {
      return (T) valueHolder.getValue();
   }

   public abstract Property copy();

   ValueHolder getValueHolder() {
      return valueHolder;
   }

   boolean isReference() {
      return valueHolder instanceof ReferenceValue;
   }

   public boolean isNameDefined() {
      return name != null && !name.isBlank();
   }

}
