package net.ihe.gazelle.maestro.api.business.property;

import net.ihe.gazelle.maestro.api.business.testreport.Input;

/**
 * Abstract class representing a property with a name and a value holder.
 *
 * @param <T> the type of the property's value
 * @param <S> the type of the subclass extending Property
 */
public abstract class Property {

   private String name;
   ValueHolder valueHolder;

   Property() {
   }

   Property(String name, Object value) {
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

   public Property setValue(Object value) {
      if (value instanceof String strValue && strValue.matches(ReferencedValue.REFERENCE_REGEX)) {
         this.valueHolder = new ReferencedValue(strValue);
      } else {
         this.valueHolder = new DirectValue(value);
      }
      return this;
   }

   public <T> T getValue() {
      return (T) valueHolder.getValue();
   }

   ValueHolder getValueHolder() {
      return valueHolder;
   }

   public boolean isReference() {
      return valueHolder instanceof ReferencedValue;
   }

   public boolean isNameDefined() {
      return name != null && !name.isBlank();
   }

}
