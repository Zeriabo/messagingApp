package fi.messaging.exceptions;

@SuppressWarnings("serial")
public class IncorrectCredentialsException extends Exception {
	 
    public IncorrectCredentialsException() {
    }
 
    public IncorrectCredentialsException(String message) {
    	super(message);
    }
    public IncorrectCredentialsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}