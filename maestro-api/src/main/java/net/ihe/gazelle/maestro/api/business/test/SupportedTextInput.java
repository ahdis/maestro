package net.ihe.gazelle.maestro.api.business.test;

import java.io.Serial;
import java.util.List;

public class SupportedTextInput extends SupportedInput {

   @Serial
   private static final long serialVersionUID = -4705877909585949476L;

   private List<String> possibleValues;
   private String defaultValue;

   public SupportedTextInput() {
      //default constructor
   }

   public List<String> getPossibleValues() {
      return possibleValues;
   }

   public SupportedTextInput setPossibleValues(List<String> possibleValues) {
      this.possibleValues = possibleValues;
      return this;
   }

   public String getDefaultValue() {
      return defaultValue;
   }

   public SupportedTextInput setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
      return this;
   }
}
