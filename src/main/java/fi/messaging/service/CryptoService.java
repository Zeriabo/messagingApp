package fi.messaging.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import javax.crypto.SecretKey;

public interface CryptoService
{
	
	public abstract byte[] signWithPrivateKey(Signature sig,PrivateKey pvts, byte[] challenge) throws NoSuchAlgorithmException, SignatureException,InvalidKeyException;
	
	public abstract  SecretKey generateSecretKey() throws NoSuchAlgorithmException;
}