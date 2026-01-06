package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;

/**
 * Exception thrown when a property is missing
 */
public class MissingPropertyException extends RuntimeException {

   @Serial
   private static final long serialVersionUID = 3899927895039751597L;

   /**
    * Constructs a new MissingPropertyException with the specified detail message.
    *
    * @param message the detail message, providing more information about the exception
    */
   public MissingPropertyException(String message) {
      super(message);
   }
}
