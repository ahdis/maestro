package net.ihe.gazelle.maestro.api.business.test;

import java.io.Serial;

public class SupportedValueSetInput extends SupportedInput {

   @Serial
   private static final long serialVersionUID = 4610026342138566392L;

   private String valueSetId;
   private String defaultValue;

   public SupportedValueSetInput() {
      //default constructor
   }

   public String getValueSetId() {
      return valueSetId;
   }

   public SupportedValueSetInput setValueSetId(String valueSetId) {
      this.valueSetId = valueSetId;
      return this;
   }

   public String getDefaultValue() {
      return defaultValue;
   }

   public SupportedValueSetInput setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
      return this;
   }
}
