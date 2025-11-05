package net.ihe.gazelle.maestro.api.business.property;

public class UnsupportedTestProperty extends Property {

   public UnsupportedTestProperty(String name, Object value) {
      setName(name);
      setValue(value);
   }

   @Override
   public Property copy() {
      return new UnsupportedTestProperty(getName(), getValue());
   }
}

