package net.ihe.gazelle.business.step;

public class NotProvidedStep extends RuntimeException{
    public NotProvidedStep(){super();}
    public NotProvidedStep(String message){super(message);}
    public NotProvidedStep(String message, Throwable cause){super(message, cause);}
    public NotProvidedStep(Throwable cause) {
        super(cause);
    }
}
