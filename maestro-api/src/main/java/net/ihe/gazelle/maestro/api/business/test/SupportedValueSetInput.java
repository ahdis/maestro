package net.ihe.gazelle.maestro.api.business.test;

import java.util.List;

public class SupportedValueSetInput extends SupportedInput {

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
