package net.ihe.gazelle.maestro.api.business.test;

public class SupportedBooleanInput extends SupportedInput {

   private boolean defaultValue;

   public SupportedBooleanInput() {
      //default constructor
   }

   public boolean isDefaultValue() {
      return defaultValue;
   }

   public SupportedBooleanInput setDefaultValue(boolean defaultValue) {
      this.defaultValue = defaultValue;
      return this;
   }
}
