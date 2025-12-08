package net.ihe.gazelle.maestro.api.business.property;


import java.io.Serial;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ReferenceValue implements ValueHolder {

   static final String REFERENCE_REGEX = "^\\$\\{([^}]+)}$";
   static final Pattern REFERENCE_PATTERN = Pattern.compile(REFERENCE_REGEX);

   @Serial
   private static final long serialVersionUID = -8735006520614740322L;

   private String reference;
   private transient Supplier<Object> valueSupplier;

   ReferenceValue(String reference) {
      Matcher matcher = REFERENCE_PATTERN.matcher(reference);
      this.reference = matcher.matches() ? matcher.group(1) : reference;
   }

   String getReference() {
      return reference;
   }

   ReferenceValue setReference(String reference) {
      this.reference = reference;
      return this;
   }

   @Override
   public Object getValue() {
      return valueSupplier != null ? valueSupplier.get() : format(reference);
   }

   ReferenceValue supply(Supplier<Object> valueSupplier) {
      this.valueSupplier = valueSupplier;
      return this;
   }

   boolean isSupplied() {
      return valueSupplier != null;
   }

   static String format(String referenceId) {
      return "${" + referenceId + "}";
   }

   public boolean isReferenceDefined() {
      return reference != null && !reference.isBlank();
   }
}
