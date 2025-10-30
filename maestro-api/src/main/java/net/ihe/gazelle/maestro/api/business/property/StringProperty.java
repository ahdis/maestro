package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;

public class StringProperty extends Property {

   @Serial
   private static final long serialVersionUID = 1003640813281866135L;

   public StringProperty() {
      super();
   }

   public StringProperty(String name, Object value) {
      super(name, value);
   }

   @Override
   public String getValue() {
      return super.getValue();
   }

   @Override
   public Property copy() {
      return new StringProperty(getName(), getValue());
   }
}
