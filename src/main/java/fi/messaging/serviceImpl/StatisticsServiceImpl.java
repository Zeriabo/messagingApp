package fi.messaging.serviceImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

import javax.ws.rs.core.Response;

import fi.messaging.app.DatabaseConnection;
import fi.messaging.service.StatisticsService;

public class StatisticsServiceImpl implements StatisticsService {

	public Response getStatistics() throws Exception
	{

		ArrayList<String> resultsArray = new ArrayList<String>(10);
		String columnValue = "";
		int columnsNumber = 0;
		Response response = null;
		
		try (Connection c = DatabaseConnection.getConnection()) {

			PreparedStatement statement2 = c
					.prepareStatement("	SELECT u.name,u.email,count(messages.idmessages) as count\n"
							+ "    FROM  messages, users u\n" + "    where messages.idsender= u.idusers\n"
							+ "    And  messages.created_at < DATE_ADD(NOW(), INTERVAL 1 MONTH)\n" + "    group by idsender\n"
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
			
		
			response=	Response.status(200).entity(resultsArray).build();
			
				
		

		} else {
		
			response=	Response.status(404).entity("no statistics yet!").build();
		}
		return response;

	}
}
