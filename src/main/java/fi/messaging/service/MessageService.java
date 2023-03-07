
package fi.messaging.service;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.sql.SQLException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.ws.rs.core.Response;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import fi.messaging.pojos.Email;
import fi.messaging.pojos.MessagesPojo;

public interface MessageService {

 public  abstract Response sendMessages(List<String> receiversArray, Email email, MessagesPojo messages, SecretKey secretKeyDecrypted) throws Exception;
 
 public abstract  MessagesPojo getMessages(String email, MessagesPojo messages) throws Exception;
 
 public abstract MessagesPojo getMessagesFromSender(String info, String senderEmail) throws Exception;
 
 public abstract String getMessageDecrypted(byte[] message, byte[] privateKeyBytes) throws SQLException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException;
 
 public abstract  XSSFSheet getSheet() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException;
}