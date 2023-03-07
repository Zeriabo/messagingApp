package fi.messaging.exceptions;

@SuppressWarnings("serial")
public class UserExistsException extends Exception {
	 
    public UserExistsException() {
    }
 
    public UserExistsException(String message) {
    	super(message);
    }
    public UserExistsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}