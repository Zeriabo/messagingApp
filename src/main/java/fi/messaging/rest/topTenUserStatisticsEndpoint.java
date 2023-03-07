package fi.messaging.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.ServiceLoader;

import fi.messaging.app.DatabaseConnection;
import fi.messaging.service.StatisticsService;
import fi.messaging.service.TokenService;

import javax.ws.rs.core.Response;
@Path("/readuserstatistics")

public class topTenUserStatisticsEndpoint {
	int row = 0;
	
	StatisticsService statisticsService=getStatisticsService();
	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@GET

	public Response readStatistics() throws Exception {

		
		return statisticsService.getStatistics();
}
	
	public static StatisticsService getStatisticsService() {
	    // load our plugin
	 ServiceLoader<StatisticsService> serviceLoader =ServiceLoader.load(StatisticsService.class);
	 for (StatisticsService provider : serviceLoader) {
	     return provider;
	 }
	 throw new NoClassDefFoundError("Unable to load a driver "+StatisticsService.class.getName());
	}
}
