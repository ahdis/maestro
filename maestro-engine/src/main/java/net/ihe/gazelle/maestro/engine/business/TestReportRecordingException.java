package net.ihe.gazelle.maestro.engine.business;

import java.io.Serial;

/**
 * Exception thrown when an error occurs during the recording of a test report.
 */
public class TestReportRecordingException extends RuntimeException {

   @Serial
   private static final long serialVersionUID = 3719443258062405706L;

   /**
    * Creates a new {@code TestReportRecordingException} with the specified message.
    *
    * @param message the detail message
    */
   public TestReportRecordingException(String message) {
      super(message);
   }

   /**
    * Creates a new {@code TestReportRecordingException} with the specified message and cause.
    *
    * @param message the detail message
    * @param cause the cause of the exception
    */
   public TestReportRecordingException(String message, Throwable cause) {
      super(message, cause);
   }

   /**
    * Creates a new {@code TestReportRecordingException} with the specified cause.
    *
    * @param cause the cause of the exception
    */
   public TestReportRecordingException(Throwable cause) {
      super(cause);
   }

}
