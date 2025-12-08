package net.ihe.gazelle.maestro.engine.business;

import java.io.Serial;

public class TestReportRecordingException extends RuntimeException {

   @Serial
   private static final long serialVersionUID = 3719443258062405706L;

   public TestReportRecordingException(String message) {
        super(message);
    }

    public TestReportRecordingException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestReportRecordingException(Throwable cause) {
        super(cause);
    }

}
