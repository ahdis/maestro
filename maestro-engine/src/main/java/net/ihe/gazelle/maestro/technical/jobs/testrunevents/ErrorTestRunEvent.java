package net.ihe.gazelle.maestro.technical.jobs.testrunevents;

/**
 * A message to Describe an error in TestRun Execution
 */
public class ErrorTestRunEvent implements TestRunEvent {

    private final Exception exception;

    public ErrorTestRunEvent(Exception exception){
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

}
