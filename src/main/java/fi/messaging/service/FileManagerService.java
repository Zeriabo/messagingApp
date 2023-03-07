package fi.messaging.service;


import javax.crypto.SecretKey;
import java.security.PrivateKey;

public interface FileManagerService {
	

	public SecretKey savingMessageToFile(PrivateKey privateKey) throws Exception;
	
	
}
