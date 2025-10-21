package net.ihe.gazelle.maestro.api.business.test;

public abstract class SupportedInput {

   private String id;
   private String label;
   private boolean required;

   public SupportedInput() {
      // Default constructor
   }

   public String getId() {
      return id;
   }

   public SupportedInput setId(String id) {
      this.id = id;
      return this;
   }

   public String getLabel() {
      return label;
   }

   public SupportedInput setLabel(String label) {
      this.label = label;
      return this;
   }

   public boolean isRequired() {
      return required;
   }

   public SupportedInput setRequired(boolean required) {
      this.required = required;
      return this;
   }
}
