package net.ihe.gazelle.maestro.api.business.test;

import java.io.Serial;

/**
 * Boolean input
 */
public class SupportedBooleanInput extends SupportedInput {

   @Serial
   private static final long serialVersionUID = 7870119588881673482L;

   /**
    * Default value
    */
   private boolean defaultValue = false;

   /**
    * Default constructor
    */
   public SupportedBooleanInput() {
      //default constructor
   }

   /**
    * Retrieves the default value of the boolean input.
    *
    * @return the default boolean value
    */
   public boolean getDefaultValue() {
      return defaultValue;
   }

   /**
    * Sets the default value for the boolean input.
    *
    * @param defaultValue the boolean value to set as the default
    * @return the current instance of {@code SupportedBooleanInput} with the updated default value
    */
   public SupportedBooleanInput setDefaultValue(boolean defaultValue) {
      this.defaultValue = defaultValue;
      return this;
   }

}
