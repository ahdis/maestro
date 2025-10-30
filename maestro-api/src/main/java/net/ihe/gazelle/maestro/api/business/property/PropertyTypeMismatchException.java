package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;

public class PropertyTypeMismatchException extends RuntimeException {
   @Serial
   private static final long serialVersionUID = -2678237272670144383L;

   public PropertyTypeMismatchException(String message) {
      super(message);
   }
}
