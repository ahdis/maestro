package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;
import java.io.Serializable;

/**
 * Implementation of ValueHolder for concrete value
 */
class DirectValue implements ValueHolder {

   @Serial
   private static final long serialVersionUID = 5435920465930342207L;

   /**
    * The value.
    */
   private final Serializable value;

   /**
    * Constructs a new DirectValue with the specified value.
    *
    * @param value the Serializable value to be held; can be null
    */
   public DirectValue(Serializable value) {
      this.value = value;
   }

   @Override
   public Serializable getValue() {
      return value;
   }

   /**
    * Checks whether the value is defined.
    *
    * @return true if the value is not null, otherwise false
    */
   public boolean isValueDefined() {
      return value != null;
   }

}
