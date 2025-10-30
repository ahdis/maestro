package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;

public class IntegerProperty extends Property {

   @Serial
   private static final long serialVersionUID = 5284763560506283622L;

   public IntegerProperty() {
      super();
   }

   public IntegerProperty(String name, Object value) {
      super(name, value);
   }

   @Override
   public Integer getValue() {
      return super.getValue();
   }

   @Override
   public Property copy() {
      return new IntegerProperty(getName(), getValue());
   }
}
