package fi.messaging.rest;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.messaging.app.DatabaseConnection;
import fi.messaging.pojos.Message;
import fi.messaging.pojos.MessagesPojo;
import fi.messaging.pojos.ReceivePojo;
import fi.messaging.pojos.Response;
import fi.messaging.security.RSAUtil;

@Path("/readmessage")

public class ReadMessageEndpoint {

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@GET

	public Response readMessage(String email) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		ReceivePojo receive = mapper.readValue(email, ReceivePojo.class);

		Response response = new Response();
		MessagesPojo messages = new MessagesPojo();

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

					PreparedStatement statement2 = c.prepareStatement(
							"SELECT messages.idmessages,messages.messagebody,messages.title,messages.datetime,messages.idsender "
									+ "  FROM receiver, messages , users sender, users receivers "
									+ "where receiver.messages_idmessages =  messages.idmessages\n"
									+ "And sender.idusers=  messages.idsender\n"
									+ "And receivers.idusers= receiver.users_idusers\n"
									+ "And  receiver.users_idusers=?");

					statement2.setInt(1, userId);

					ResultSet rs = statement2.executeQuery();

					while (rs.next()) {

						PreparedStatement statementsecret = c.prepareStatement(
								"SELECT * FROM messaging.secret_keys\n" + "where messages_idmessages=?;" + "",
								Statement.RETURN_GENERATED_KEYS);

						statementsecret.setInt(1, rs.getInt(1));
						ResultSet res = statementsecret.executeQuery();

						res.first();
						byte[] privateKey = res.getBytes(2);

						Message m = new Message();
						m.setMessagebody(getMessageDecrypted(rs.getBytes(2), privateKey));
						m.setDatetime((rs.getDate(4)));
						m.setIdUser(rs.getInt(5));
						m.setTitle(rs.getString(3));
						System.out.println(m.toString());
						messages.addMessage(m);
						messages.getMessages().forEach((mess) -> {
							System.out.print(mess);
						});
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

	private String getMessageDecrypted(byte[] message, byte[] privateKeyBytes)
			throws SQLException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidParameterSpecException, InvalidAlgorithmParameterException, BadPaddingException,
			IllegalBlockSizeException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
		PrivateKey privateKey2 = keyFactory.generatePrivate(privateKeySpec);
		String ms = RSAUtil.decrypt(message, privateKey2);

		return ms;
	}
	
}
