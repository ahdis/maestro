package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serializable;

/**
 * Interface to store a property value of any type
 */
interface ValueHolder extends Serializable {

   /**
    * Retrieves the value held by the implementing class.
    *
    * @return a Serializable object representing the value, or null if no value is set
    */
   Serializable getValue();

}
