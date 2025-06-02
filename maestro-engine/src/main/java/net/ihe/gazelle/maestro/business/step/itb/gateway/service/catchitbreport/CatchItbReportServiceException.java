package net.ihe.gazelle.maestro.business.step.itb.gateway.service.catchitbreport;

public class CatchItbReportServiceException extends RuntimeException {
    public CatchItbReportServiceException(String message) {
        super(message);
    }

    public CatchItbReportServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
