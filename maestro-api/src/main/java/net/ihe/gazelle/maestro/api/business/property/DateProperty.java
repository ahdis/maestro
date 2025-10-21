package net.ihe.gazelle.maestro.api.business.property;

import java.time.Instant;

public class DateProperty extends Property {

   public DateProperty() {
      super();
   }

   public DateProperty(String name, Object value) {
      super(name, value);
   }

   @Override
   public Instant getValue() {
      return super.getValue();
   }

}
