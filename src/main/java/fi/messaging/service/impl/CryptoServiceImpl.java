package fi.messaging.service.impl;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import fi.messaging.service.CryptoService;

public class CryptoServiceImpl implements CryptoService
{
	public byte[] signWithPrivateKey(Signature sig,PrivateKey pvts, byte[] challenge) throws NoSuchAlgorithmException, SignatureException,InvalidKeyException {
		 
        try {
			sig.initSign(pvts);
		} catch (InvalidKeyException e) {

			throw e;
		}
        try {
			sig.update(challenge);
		} catch (SignatureException e) {
			throw e;
		}
      return  sig.sign();
}

public  SecretKey generateSecretKey() throws NoSuchAlgorithmException
{
	KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
	keyGenerator.init(128);
	SecretKey originalKey = keyGenerator.generateKey();
	return originalKey;
	
}

}