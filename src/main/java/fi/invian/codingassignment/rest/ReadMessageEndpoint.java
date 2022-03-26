package fi.invian.codingassignment.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.invian.codingassignment.app.DatabaseConnection;
import fi.invian.codingassignment.pojos.ReceivePojo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import fi.invian.codingassignment.pojos.Response;

@Path("/readmessage")

public class ReadMessageEndpoint {

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@GET

	public Response readMessage(String email) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		ReceivePojo receive = mapper.readValue(email, ReceivePojo.class);
		ArrayList<String> resultsArray = new ArrayList<String>();
		String columnValue = "";
		Response response = new Response();

		try (Connection c = DatabaseConnection.getConnection()) {

			PreparedStatement p1 = c.prepareStatement("SELECT * FROM users where email=?");
			p1.setString(1, receive.getEmail());
			ResultSet r = p1.executeQuery();
			r.first();
			if (r.first()) {
				int userId = r.getInt(1);
				r.first();
				if (userId > -1) {

					PreparedStatement statement2 = c.prepareStatement("SELECT messaging.messages.messagebody\n"
							+ "  FROM messaging.receiver, messaging.messages , messaging.users sender, messaging.users receivers\n"
							+ "where messaging.receiver.messages_idmessages =  messaging.messages.idmessages\n"
							+ "And sender.idusers= messaging.messages.idsender\n"
							+ "And receivers.idusers=messaging.receiver.receiver_idusers\n"
							+ "And messaging.receiver.receiver_idusers=?");

					statement2.setInt(1, userId);

					ResultSet rs = statement2.executeQuery();

					while (rs.next()) {

						
						columnValue = rs.getString(1);
						resultsArray.add("message: " + columnValue);
						resultsArray.add(",");
					}

				}

			} else {
				response.setStatus(false);
				response.setMessage("Wrong email");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		if (resultsArray.size() > 0) {
			response.setStatus(true);
			resultsArray.forEach((mess) -> {
				response.setMessage(response.getMessage() + " " + mess);
			});

		} else {
			response.setStatus(false);
			response.setMessage("didnt get Anything");
		}
		return response;

	}
}
