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
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import fi.invian.codingassignment.pojos.Response;

//try (Connection c = DatabaseConnection.getConnection()) {
//    try (PreparedStatement p = c.prepareStatement("SELECT messagebody FROM messages")) {
//        ResultSet r = p.executeQuery();
//        if (r.next()) {
//            return new HelloPojo(r.getString(1));
//        } else {
//            throw new NotFoundException("Database did not contain the expected message.");
//        }
//    }
//}

@Path("/readmessage")

public class ReadMessageEndpoint {
	int row = 0;

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@GET

	public Response readMessage(String email) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		ReceivePojo receive = mapper.readValue(email, ReceivePojo.class);
	
		int columnsNumber = 0;

		ArrayList<String> resultsArray = new ArrayList<String>();
		System.out.println("the email is: " + receive.getEmail());
		Response response = new Response();

		try (Connection c = DatabaseConnection.getConnection()) {
			System.out.println("Connection succeeeded");
			PreparedStatement p1 = c.prepareStatement("SELECT * FROM users where email=?");
			p1.setString(1, receive.getEmail());
			System.out.println(receive.getEmail());
			ResultSet r = p1.executeQuery();
			r.first();
			System.out.println(r.getInt(1));
			if (r.first()) {
				int userId = r.getInt(1);
				System.out.println("the id is: " + userId);
				r.first();
				if (userId > -1) {
					System.out.println("enterring the big query");
					PreparedStatement statement2 = c.prepareStatement(
							"SELECT messaging.receiver.receiver_idusers,messaging.messages.datetime,messaging.messages.idmessages\n"
									+ ",sender.name senderName,sender.email senderEmail, messaging.messages.messagebody,receivers.name,receivers.email\n"
									+ "  FROM \n"
									+ "messaging.receiver, messaging.messages , messaging.users sender, messaging.users receivers\n"
									+ "where messaging.receiver.messages_idmessages =  messaging.messages.idmessages\n"
									+ "And sender.idusers= messaging.messages.idsender\n"
									+ "And receivers.idusers=messaging.receiver.receiver_idusers\n"
									+ "And messaging.receiver.receiver_idusers=?");

					statement2.setInt(1, userId);

					ResultSet rs = statement2.executeQuery();

					while (rs.next()) {
						ResultSetMetaData rsmd = rs.getMetaData();
						columnsNumber = rsmd.getColumnCount();
						String columnValue = "";
						System.out.println("the column number is " + columnsNumber);

						for (int i = 1; i <= columnsNumber; i++) {

							if (i == 2 || i == 4 || i == 5 || i == 6) {
								columnValue = rs.getString(i);
								System.out.println(rsmd.getColumnName(i) + " : " + columnValue);
								resultsArray.add(rsmd.getColumnName(i) + " : " + columnValue);
							}
							if (i == 7 || i == 8) {
								columnValue = rs.getString(i);
								System.out.println(rsmd.getColumnName(i) + " Of receiver : " + columnValue);
								resultsArray.add(rsmd.getColumnName(i) + " Of receiver : " + columnValue);
							}

							if (i == 1 || i == 3) {
								columnValue = Integer.toString(rs.getInt(i));
								resultsArray.add(rsmd.getColumnName(i) + " : " + columnValue);

								System.out.println(rsmd.getColumnName(i) + " : " + columnValue);
							}
						}

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
