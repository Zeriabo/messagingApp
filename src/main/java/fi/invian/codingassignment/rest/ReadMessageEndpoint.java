package fi.invian.codingassignment.rest;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.invian.codingassignment.app.DatabaseConnection;
import fi.invian.codingassignment.pojos.Encryption;
import fi.invian.codingassignment.pojos.Message;
import fi.invian.codingassignment.pojos.MessagesPojo;
import fi.invian.codingassignment.pojos.ReceivePojo;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.invian.codingassignment.pojos.Response;

@Path("/readmessage")

public class ReadMessageEndpoint {

	@SuppressWarnings({ "deprecation" })
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@GET

	public Response readMessage(String email) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		ReceivePojo receive = mapper.readValue(email, ReceivePojo.class);
		ArrayList<String> resultsArray = new ArrayList<String>();
		Response response = new Response();
		MessagesPojo messages=new MessagesPojo();
		
	

		try (Connection c = DatabaseConnection.getConnection()) {

			PreparedStatement p1 = c.prepareStatement("SELECT * FROM users where email=?");
			p1.setString(1, receive.getEmail());
			ResultSet r = p1.executeQuery();
			r.first();
			if (r.first()) {
				int userId = r.getInt(1);
				
				 messages.setUserid(userId);
				r.first();
				if (userId > -1) {

					PreparedStatement statement2 = c.prepareStatement("SELECT messages.idmessages,messages.messagebody,messages.title,messages.datetime,messages.idsender "
							+ "  FROM receiver, messages , users sender, users receivers "
							+ "where receiver.messages_idmessages =  messages.idmessages\n"
							+ "And sender.idusers=  messages.idsender\n"
							+ "And receivers.idusers= receiver.users_idusers\n"
							+ "And  receiver.users_idusers=?");

					statement2.setInt(1, userId);

					ResultSet rs = statement2.executeQuery();

					while (rs.next()) {
          //String ms=Encryption.decrypt(ecipher.getAlgorithm(),rs.getString(1), secretKey, new IvParameterSpec(iv));
						Message m = new Message();
						m.setMessagebody(getMessageDecrypted(rs.getInt(1),rs.getString(2),c));
						m.setDatetime((rs.getDate(4)));
						m.setIdUser(rs.getInt(5));
						m.setTitle(rs.getString(3));
						System.out.println(m.toString());
						messages.addMessage(m);
					messages.getMessages().forEach((mess)->{System.out.print(mess);});
					
					}

				}

			} else {
				response.setStatus(true);
				response.setErrorMessage("no messages");
		        response.setCode(444);
		 
			}
		} catch (Exception e) {
			response.setStatus(false);
			response.setErrorMessage(e.getMessage());
			response.setCode(500);
			
		}

		if (messages.getMessages().size() > 0) {
			
			response.setStatus(true);
			response.setMessages(messages);

		} 
		return response;

	}

	private String getMessageDecrypted(int i,String message, Connection c) throws SQLException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
		
		String ms=null;
		PreparedStatement pGetkey = c.prepareStatement("SELECT * FROM messaging.secret_keys where messages_idmessages=?");
		pGetkey.setInt(1, i);
		
		ResultSet rs = pGetkey.executeQuery();
		while(rs.next()) {
			byte[] secretKeyBytes=rs.getBytes(2);
			SecretKey key = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, "AES");
			Cipher dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			
			AlgorithmParameters params = dcipher.getParameters();
		 byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
		  dcipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		 ms=Encryption.decrypt("AES/CBC/PKCS5Padding",message, key, new IvParameterSpec(iv));
			
			
		}
		
		return ms;
	}
}
