package net.ihe.gazelle.maestro.api.business.property;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ReferencedValue implements ValueHolder {

   public static final String REFERENCE_REGEX = "^\\$\\{([^}]+)}$";
   public static final Pattern REFERENCE_PATTERN = Pattern.compile(REFERENCE_REGEX);

   private String reference;
   private transient Object resolvedValue;

   public ReferencedValue(String reference) {
      Matcher matcher = REFERENCE_PATTERN.matcher(reference);
      this.reference = matcher.matches() ? matcher.group(1) : reference;
   }

   public String getReference() {
      return reference;
   }

   public ReferencedValue setReference(String reference) {
      this.reference = reference;
      return this;
   }

   @Override
   public Object getValue() {
      return resolvedValue != null ? resolvedValue : format(reference);
   }

   public ReferencedValue setValue(Object resolvedValue) {
      this.resolvedValue = resolvedValue;
      return this;
   }

   public static String format(String referenceId) {
      return "${" + referenceId + "}";
   }

   public boolean isReferenceDefined() {
      return reference != null && !reference.isBlank();
   }
}
