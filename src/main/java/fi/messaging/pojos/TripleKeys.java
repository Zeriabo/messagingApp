package fi.messaging.pojos;


import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

public class TripleKeys {

	private PrivateKey privateKey;
	private PublicKey publicKey;
	private SecretKey secretKey;


	public TripleKeys(PrivateKey privateKey,PublicKey publicKey,SecretKey secretKey) {

		this.privateKey=privateKey;
		this.publicKey=publicKey;
		this.secretKey=secretKey;
	}

	public TripleKeys() {
		// TODO Auto-generated constructor stub
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public SecretKey getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

}
