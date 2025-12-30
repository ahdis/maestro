package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;
import java.io.Serializable;

/**
 * String property
 */
public class StringProperty extends Property {

   @Serial
   private static final long serialVersionUID = 1003640813281866135L;

   /**
    * Default constructor
    */
   public StringProperty() {
      super();
   }

   /**
    * Constructs a new StringProperty with the specified name and value.
    *
    * @param name  the name of the property, which identifies it uniquely
    * @param value the value to be assigned to the property; must be a Serializable object
    */
   public StringProperty(String name, Serializable value) {
      super(name, value);
   }

   @Override
   public Property copy() {
      return new StringProperty(getName(), getValue());
   }
}
