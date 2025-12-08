package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;

public class MissingPropertyException extends RuntimeException {

   @Serial
   private static final long serialVersionUID = 3899927895039751597L;

   public MissingPropertyException(String message) {
      super(message);
   }
}
