package fi.invian.codingassignment.security;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {

    public CryptoUtil() {
    }
    public static PublicKey getPublicKey(String base64PublicKey){
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }
    public static KeyPair generateRsaKeyPair() throws NoSuchAlgorithmException {
    	 KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
         //1024 bit long key
    	
         keyGen.initialize(1024);
         //generating RSA key pair (public and private)
         return keyGen.genKeyPair();
    }

    public static PrivateKey getRsaPrivateKey(String base64PrivateKey) throws Exception {
        byte[] privateKey = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
    public static PublicKey getRsaPublicKey(String base64PrivateKey) throws Exception {
        byte[] privateKey = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
    
    public static byte[] encryptWithPublicRsaKey(byte[] data, String rsaPublicKeyBase64) throws Exception {
  	  Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getRsaPublicKey(rsaPublicKeyBase64));
        return cipher.doFinal(data);
  }
    public static byte[] decryptWithPrivateRsaKey(byte[] data, String rsaPrivateKeyBase64) throws Exception {
    	  Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
          cipher.init(Cipher.DECRYPT_MODE, getRsaPrivateKey(rsaPrivateKeyBase64));
          return cipher.doFinal(data);
    }

    public static byte[] decryptWithAes(byte[] data, byte[] aesKey, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new IvParameterSpec(iv));
        return cipher.doFinal(data);
    }
}
