package fi.messaging.exceptions;


@SuppressWarnings("serial")
public class EncryptionDecryptionException extends Exception {
	 
    public EncryptionDecryptionException() {
    }
 
    public EncryptionDecryptionException(String message, Throwable throwable) {
        super(message, throwable);
    }
}