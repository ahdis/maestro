package net.ihe.gazelle.maestro.engine.business;

import java.io.Serial;

public class UnknownStepException extends RuntimeException {

   @Serial
   private static final long serialVersionUID = -2327363841694476903L;

   public UnknownStepException(String message) {
      super(message);
   }
}
