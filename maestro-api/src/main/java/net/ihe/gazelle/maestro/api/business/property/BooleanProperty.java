package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;
import java.io.Serializable;

public class BooleanProperty extends Property {

   @Serial
   private static final long serialVersionUID = -8823778950334584409L;

   public BooleanProperty() {
   }

   public BooleanProperty(String name, Serializable value) {
      super(name, value);
   }

   @Override
   public Property copy() {
      return new BooleanProperty(getName(), getValue());
   }

}
