package net.ihe.gazelle.maestro.api.business.recording;

public class TestReportRecordException extends RuntimeException {

    public TestReportRecordException(String message) {
        super(message);
    }

    public TestReportRecordException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestReportRecordException(Throwable cause) {
        super(cause);
    }

}
