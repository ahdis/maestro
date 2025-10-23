package net.ihe.gazelle.maestro.engine.business;

import java.io.Serial;

public class ReportRecordingException extends RuntimeException {

   @Serial
   private static final long serialVersionUID = 1L;

   public ReportRecordingException(String message, Throwable cause) {
      super(message, cause);
   }

}
