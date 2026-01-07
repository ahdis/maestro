package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;
import java.io.Serializable;

/**
 * Integer property
 */
public class IntegerProperty extends Property {

   @Serial
   private static final long serialVersionUID = 5284763560506283622L;

   /**
    * Default constructor
    */
   public IntegerProperty() {
      super();
   }

   /**
    * Constructs a new IntegerProperty with the specified name and value.
    *
    * @param name  the name of the property, which identifies it uniquely
    * @param value the value to be assigned to the property; must be a Serializable object
    */
   public IntegerProperty(String name, Serializable value) {
      super(name, value);
   }

   @Override
   public Property copy() {
      return new IntegerProperty(getName(), getValue());
   }
}
