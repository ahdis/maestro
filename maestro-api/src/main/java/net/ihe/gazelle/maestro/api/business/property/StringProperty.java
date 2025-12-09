package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;
import java.io.Serializable;

public class StringProperty extends Property {

   @Serial
   private static final long serialVersionUID = 1003640813281866135L;

   public StringProperty() {
      super();
   }

   public StringProperty(String name, Serializable value) {
      super(name, value);
   }

   @Override
   public Property copy() {
      return new StringProperty(getName(), getValue());
   }
}
