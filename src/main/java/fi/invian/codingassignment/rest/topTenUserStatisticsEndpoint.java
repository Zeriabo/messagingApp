package fi.invian.codingassignment.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import fi.invian.codingassignment.app.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import fi.invian.codingassignment.pojos.Response;

@Path("/readuserstatistics")

public class topTenUserStatisticsEndpoint {
	int row = 0;

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@GET

	public Response readStatistics() throws Exception {

		ArrayList<String> resultsArray = new ArrayList<String>(10);
		String columnValue = "";
		int columnsNumber = 0;
		Response response = new Response();

		try (Connection c = DatabaseConnection.getConnection()) {

			PreparedStatement statement2 = c
					.prepareStatement("	SELECT u.name,u.email,count(messages.idmessages) as count\n"
							+ "    FROM  messages, users u\n" + "    where messages.idsender= u.idusers\n"
							+ "    And  datetime < DATE_ADD(NOW(), INTERVAL -1 MONTH)\n" + "    group by idsender\n"
							+ "    order by count DESC\n" + "    limit 10");

			ResultSet rs = statement2.executeQuery();

			ResultSetMetaData rsmd = rs.getMetaData();
			columnsNumber = rsmd.getColumnCount();

			while (rs.next()) {

				for (int i = 1; i <= columnsNumber; i++) {
					if (i == 3) {
						columnValue = Integer.toString(rs.getInt(i));

					} else {
						columnValue = rs.getString(i);

					}
					resultsArray.add(columnValue);

				}
			}
		}
		if (resultsArray.size() > 0) {
			response.setStatus(true);
			resultsArray.forEach((mess) -> {
				if (mess != null) {
					
					response.setMessage(response.getMessage() + " " + mess);
				}
			});

		} else {
			response.setStatus(false);
			response.setMessage("didnt get Anything");
		}
		return response;

	}

}
