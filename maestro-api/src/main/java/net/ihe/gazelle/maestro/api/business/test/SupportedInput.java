package net.ihe.gazelle.maestro.api.business.test;

import java.io.Serial;
import java.io.Serializable;

public abstract class SupportedInput implements Serializable {

   @Serial
   private static final long serialVersionUID = 5443082512534032315L;

   private String id;
   private String label;
   private boolean required;

   protected SupportedInput() {
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

   public boolean isIdDefined() {
      return id != null && !id.isEmpty();
   }

   public boolean isLabelDefined() {
      return label != null && !label.isEmpty();
   }
}
