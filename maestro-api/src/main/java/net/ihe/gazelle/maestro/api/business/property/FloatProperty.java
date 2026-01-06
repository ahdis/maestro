package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;
import java.io.Serializable;

/**
 * Float property
 */
public class FloatProperty extends Property {

   @Serial
   private static final long serialVersionUID = -5840963846126882752L;

   /**
    * Default constructor
    */
   public FloatProperty() {
      super();
   }

   /**
    * Constructs a new FloatProperty with the specified name and value.
    *
    * @param name  the name of the property, which identifies it uniquely
    * @param value the value to be assigned to the property; must be a Serializable object
    */
   public FloatProperty(String name, Serializable value) {
      super(name, value);
   }

   @Override
   public Property copy() {
      return new FloatProperty(getName(), getValue());
   }
}
