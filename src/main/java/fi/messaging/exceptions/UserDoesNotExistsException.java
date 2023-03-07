package fi.messaging.exceptions;

@SuppressWarnings("serial")
public class UserDoesNotExistsException extends Exception {
	 
    public UserDoesNotExistsException() {
    }
 
    public UserDoesNotExistsException(String message) {
    	super(message);
    }
    public UserDoesNotExistsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}