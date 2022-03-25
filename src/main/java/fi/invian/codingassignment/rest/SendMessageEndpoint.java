package fi.invian.codingassignment.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.invian.codingassignment.app.DatabaseConnection;
import fi.invian.codingassignment.pojos.Email;
import fi.invian.codingassignment.pojos.Message;
import fi.invian.codingassignment.pojos.Receiver;
import fi.invian.codingassignment.pojos.Sender;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import fi.invian.codingassignment.pojos.Response;


@Path("/sendmessage")
public class SendMessageEndpoint {
	int row = 0;

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST

	public Response sendMessage(String messageDetails) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		Email email = mapper.readValue(messageDetails, Email.class);
		int messageId = -1;

		Response response = new Response();
		int senderId = Integer.parseInt(email.getSenderId());
		String[] receiversArray = email.getReceivers().split(",");
		if (receiversArray.length <= 5) {
			for (String receiverEmail : receiversArray) {
				try (Connection c = DatabaseConnection.getConnection()) {
					PreparedStatement p1 = c.prepareStatement("SELECT * FROM users where email=?");
					p1.setString(1, receiverEmail);
					ResultSet r = p1.executeQuery();
					if (r.next()) {

						PreparedStatement p2 = c.prepareStatement("SELECT * FROM users where idusers=?");

						p2.setInt(1, Integer.parseInt(email.getSenderId()));

						Message message = new Message(email.getId(), email.getTitle(), email.getMessagebody(),
								email.getDatetime(), senderId);
			
						PreparedStatement statement = c.prepareStatement(
								"INSERT INTO messages(`title`, `messagebody`, `datetime`, `nbrofrecipients`, `idsender`) VALUES(?,?,?,?,?)",
								Statement.RETURN_GENERATED_KEYS);
						statement.setString(1, message.getTitle());
						statement.setString(2, message.getMessagebody());
						statement.setString(3, message.getDatetime().toString());
						statement.setInt(4, receiversArray.length);
						statement.setInt(5, senderId);

						statement.executeQuery();
						ResultSet keys = statement.getGeneratedKeys();

						while (keys.next()) {
							messageId = keys.getInt(1);
						}
						System.out.println("Last Key: " + messageId);

						PreparedStatement s1 = c.prepareStatement("SELECT * FROM users where idusers=?");
						s1.setInt(1, senderId);

						ResultSet s = s1.executeQuery();
						s.first();

						Sender sender = new Sender(s.getInt(1), s.getString(2), s.getString(3));
						s.first();

						PreparedStatement ins1 = c
								.prepareStatement("INSERT INTO sender(`users_idusers`, `datetime`) VALUES(?,?) ");
						ins1.setInt(1, sender.getIdUser());
						ins1.setString(2, email.getDatetime().toString());

						ins1.executeUpdate();

						Receiver rec = new Receiver(r.getInt(1), r.getString(2), r.getString(3));
						if (messageId >= 0) {
							PreparedStatement ins2 = c.prepareStatement(
									"INSERT INTO receiver(`receiver_idusers`, `datetime`, `messages_idmessages`, `messages_idsender`) VALUES(?,?,?,?) ");
							ins2.setInt(1, rec.getIdUser());
							ins2.setString(2, message.getDatetime().toString());
							System.out.println(message.getId() + message.getTitle());
							ins2.setInt(3, messageId);
							ins2.setInt(4, senderId);
							ins2.executeUpdate();
						}

					}
				}
			}
			response.setStatus(true);
			response.setMessage("Submitted data " + messageDetails);
		} else if (receiversArray.length > 5) {
			response.setStatus(false);
			response.setMessage("Maximum 5 recipients");
		} else {
			response.setStatus(false);
			response.setMessage("ERROR : not inserted");
		}

		return response;

	}
}
