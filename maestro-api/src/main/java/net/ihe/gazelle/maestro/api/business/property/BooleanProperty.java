package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;
import java.io.Serializable;

/**
 * Boolean property
 */
public class BooleanProperty extends Property {

   @Serial
   private static final long serialVersionUID = -8823778950334584409L;

   /**
    * Default constructor
    */
   public BooleanProperty() {
   }

   /**
    * Constructs a new BooleanProperty with the specified name and value.
    *
    * @param name  the name of the property, which identifies it uniquely
    * @param value the value to be assigned to the property; must be a Serializable object
    */
   public BooleanProperty(String name, Serializable value) {
      super(name, value);
   }

   @Override
   public Property copy() {
      return new BooleanProperty(getName(), getValue());
   }

}
