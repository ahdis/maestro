package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;

public class FloatProperty extends Property {

   @Serial
   private static final long serialVersionUID = -5840963846126882752L;

   public FloatProperty() {
      super();
   }

   public FloatProperty(String name, Object value) {
      super(name, value);
   }

   @Override
   public Property copy() {
      return new FloatProperty(getName(), getValue());
   }
}
