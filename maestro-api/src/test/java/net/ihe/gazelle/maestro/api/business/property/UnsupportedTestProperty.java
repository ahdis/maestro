package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;
import java.io.Serializable;

public class UnsupportedTestProperty extends Property {

    @Serial
    private static final long serialVersionUID = 1839440204100058971L;

    public UnsupportedTestProperty(String name, Serializable value) {
      setName(name);
      setValue(value);
   }

   @Override
   public Property copy() {
      return new UnsupportedTestProperty(getName(), getValue());
   }
}

