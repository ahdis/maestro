package net.ihe.gazelle.maestro.api.business.test;

import java.io.Serial;

/**
 * A value set input.
 */
public class SupportedValueSetInput extends SupportedInput {

   @Serial
   private static final long serialVersionUID = 4610026342138566392L;

   /**
    * The id of the value set.
    */
   private String valueSetId;

   /**
    * The default value.
    */
   private String defaultValue;

   /**
    * Default constructor
    */
   public SupportedValueSetInput() {
      //default constructor
   }

   /**
    * Retrieves the ID of the value set.
    *
    * @return the ID of the value set as a string
    */
   public String getValueSetId() {
      return valueSetId;
   }

   /**
    * Sets the ID of the value set.
    *
    * @param valueSetId the ID to set for the value set
    * @return the current instance of {@code SupportedValueSetInput} for method chaining
    */
   public SupportedValueSetInput setValueSetId(String valueSetId) {
      this.valueSetId = valueSetId;
      return this;
   }

   /**
    * Retrieves the default value of the value set.
    *
    * @return the default value as a string
    */
   public String getDefaultValue() {
      return defaultValue;
   }

   /**
    * Sets the default value of the value set.
    *
    * @param defaultValue the default value to set for the value set
    * @return the current instance of {@code SupportedValueSetInput} for method chaining
    */
   public SupportedValueSetInput setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
      return this;
   }

   /**
    * Checks if the value set ID is defined.
    *
    * @return true if the value set ID is not null and not empty, false otherwise
    */
   public boolean isValueSetIdDefined() {
      return valueSetId != null && !valueSetId.isEmpty();
   }

}
