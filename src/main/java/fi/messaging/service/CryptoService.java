package fi.messaging.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;

public class CryptoService
{
	
	public static byte[] signWithPrivateKey(Signature sig,PrivateKey pvts, byte[] challenge) throws NoSuchAlgorithmException, SignatureException {
		 
	        try {
				sig.initSign(pvts);
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				sig.update(challenge);
			} catch (SignatureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      return  sig.sign();
	}
	
	public static SecretKey generateSecretKey() throws NoSuchAlgorithmException
	{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		SecretKey originalKey = keyGenerator.generateKey();
		return originalKey;
		
	}
	
}