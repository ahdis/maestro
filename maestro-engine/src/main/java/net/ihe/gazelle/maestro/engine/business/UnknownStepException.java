package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.test.Step;

import java.io.Serial;

/**
 * Exception thrown when a {@link Step} is not recognized or cannot be found.
 */
public class UnknownStepException extends RuntimeException {

   @Serial
   private static final long serialVersionUID = -2327363841694476903L;

   /**
    * Creates a new {@code UnknownStepException} with the specified message.
    *
    * @param message the detail message
    */
   public UnknownStepException(String message) {
      super(message);
   }
}
