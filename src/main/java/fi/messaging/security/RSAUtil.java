package fi.messaging.security;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import fi.messaging.app.DatabaseConnection;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

public class RSAUtil {


    public static PublicKey getPublicKey(String base64PublicKeym,int messageid,int senderid) throws SQLException{
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKeym.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            getPrivateKey(keySpec, messageid, senderid);
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public static PrivateKey getPrivateKey(X509EncodedKeySpec keySpec,int messageid,int senderid ) throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException{
    	KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    	PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
    	try (Connection c = DatabaseConnection.getConnection()) {
    		PreparedStatement statementsecret = c.prepareStatement(
					"INSERT INTO `messaging`.`secret_keys`\n"
					+ "(`secret_key`,\n"
					+ "`messages_idmessages`,\n"
					+ "`messages_idsender`)\n"
					+ "VALUES\n"
					+ "(?,?,?);"
					+ "",
					Statement.RETURN_GENERATED_KEYS);
			
			statementsecret.setBytes(1,privateKey.getEncoded());
			statementsecret.setInt(2,messageid);
			statementsecret.setInt(3, senderid);
			
			statementsecret.executeQuery();
			
    	}

        return privateKey;
    }

    public static byte[] encrypt(String data, PublicKey publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data.getBytes());
    }

    public static String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

   

    public static PrivateKey convertArrayToPriKey(byte encoded[]) throws Exception {
    	      String algorithm = "RSA";
    		  PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
    		  KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
    		  PrivateKey priKey = keyFactory.generatePrivate(keySpec);
    		  return priKey;
    		 }
	public static  boolean processFile(Cipher ci,InputStream in,OutputStream out)
		    throws javax.crypto.IllegalBlockSizeException,
		           javax.crypto.BadPaddingException,
		           java.io.IOException
		{
		    byte[] ibuf = new byte[1024];
		    int len;
		    while ((len = in.read(ibuf)) != -1) {
		        byte[] obuf = ci.update(ibuf, 0, len);
		        if ( obuf != null ) {
		        	out.write(obuf);
		        }
		    }
	
		    byte[] obuf = ci.doFinal(in.readAllBytes());
		    if ( obuf != null ) 
		    	{
		    	out.write(obuf);
		    	}else {
		    		return false;
		    	}
		    in.close();
		    out.close();
		    out.flush();
		    return true;
		}
	public static byte[] wrapKey(PublicKey pubKey, SecretKey symKey)
	        throws InvalidKeyException, IllegalBlockSizeException {
	    try {
	        final Cipher cipher = Cipher
	                .getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
	        cipher.init(Cipher.WRAP_MODE, pubKey);
	        final byte[] wrapped = cipher.wrap(symKey);
	        return wrapped;
	    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
	        throw new IllegalStateException(
	                "Java runtime does not support RSA/ECB/OAEPWithSHA1AndMGF1Padding",
	                e);
	    }
	}
	
	public static  SecretKey unWrapKey(PrivateKey key, byte[] keybytes)
	        throws InvalidKeyException, IllegalBlockSizeException {
		
	    try {
	        final Cipher cipher = Cipher
	                .getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
	        cipher.init(Cipher.UNWRAP_MODE,key);
	        final Key unwrapped = cipher.unwrap(keybytes,
	        		"RSA/ECB/OAEPWithSHA1AndMGF1Padding",
	        		Cipher.SECRET_KEY);
	        SecretKey originalKey = new SecretKeySpec(unwrapped.getEncoded(),0,unwrapped.getEncoded().length, "DES");
	        return originalKey;
	    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
	        throw new IllegalStateException(
	                "Java runtime does not support RSA/ECB/OAEPWithSHA1AndMGF1Padding",
	                e);
	    }
	}

	

}
