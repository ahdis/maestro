package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;

public class DateProperty extends Property {

   @Serial
   private static final long serialVersionUID = 6019146262304138005L;

   public DateProperty() {
      super();
   }

   public DateProperty(String name, Object value) {
      super(name, value);
   }

   @Override
   public Property copy() {
      return new DateProperty(getName(), getValue());
   }
}
