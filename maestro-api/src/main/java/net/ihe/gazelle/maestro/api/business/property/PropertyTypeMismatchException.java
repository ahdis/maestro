package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;

/**
 * Exception thrown when a property type mismatch occurs
 */
public class PropertyTypeMismatchException extends RuntimeException {

   @Serial
   private static final long serialVersionUID = -2678237272670144383L;

   /**
    * Constructs a new {@code PropertyTypeMismatchException} with the specified detail message.
    *
    * @param message the detail message that provides information about the exception
    */
   public PropertyTypeMismatchException(String message) {
      super(message);
   }
}
