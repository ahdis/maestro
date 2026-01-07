package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;
import java.io.Serializable;

/**
 * A property containing a date
 */
public class DateProperty extends Property {

   @Serial
   private static final long serialVersionUID = 6019146262304138005L;

   /**
    * Default constructor
    */
   public DateProperty() {
      super();
   }

   /**
    * Constructs a new DateProperty with the specified name and value.
    *
    * @param name  the name of the property, which identifies it uniquely
    * @param value the value to be assigned to the property; must be a Serializable object
    */
   public DateProperty(String name, Serializable value) {
      super(name, value);
   }

   @Override
   public Property copy() {
      return new DateProperty(getName(), getValue());
   }
}
