package net.ihe.gazelle.maestro.api.business.test;

import java.io.Serial;

public class SupportedBooleanInput extends SupportedInput {

   @Serial
   private static final long serialVersionUID = 7870119588881673482L;

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
