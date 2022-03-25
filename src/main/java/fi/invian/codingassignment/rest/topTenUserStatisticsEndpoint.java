package fi.invian.codingassignment.rest;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.invian.codingassignment.app.DatabaseConnection;
import fi.invian.codingassignment.pojos.Email;
import fi.invian.codingassignment.pojos.HelloPojo;
import fi.invian.codingassignment.pojos.Message;
import fi.invian.codingassignment.pojos.ReceivePojo;
import fi.invian.codingassignment.pojos.Receiver;
import fi.invian.codingassignment.pojos.SendPojo;
import fi.invian.codingassignment.pojos.Sender;

import java.io.Console;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import fi.invian.codingassignment.pojos.Response;
import java.util.ArrayList;
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

@Path("/readuserstatistics")

public class topTenUserStatisticsEndpoint {
	int row =0;
     @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    @GET                     
		
    public Response readStatistics() throws Exception {
	 

    	  ArrayList<String> resultsArray = new ArrayList<String>(); 
    	  String columnValue ="";
    	  int columnsNumber=0;
		Response response = new Response();
	
	
			try(Connection c = DatabaseConnection.getConnection())
			{
			System.out.println("Connection succeeeded");	
				
  PreparedStatement statement2 = c.prepareStatement("	SELECT u.name,u.email,count(messages.idmessages) as count\n"
  		+ "    FROM  messages, users u\n"
  		+ "    where messages.idsender= u.idusers\n"
  		+ "    And  datetime < DATE_ADD(NOW(), INTERVAL -1 MONTH)\n"
  		+ "    group by idsender\n"
  		+ "    order by count DESC\n"
  		+ "    limit 10");  

					
						 
	   	            ResultSet rs = statement2.executeQuery();
	   	         if (!rs.next()) {                           
                 System.out.println("No records found");

                   }else {
                	   do {
                		   System.out.println(" record found");
                		   
                	    } while (rs.next());
                   }
	   	         rs.first();
	   	         ResultSetMetaData rsmd = rs.getMetaData();
		   	       columnsNumber = rsmd.getColumnCount();
		   		System.out.println("column number "+columnsNumber);	
		   	   while(rs.next())
		   	      {       
		   		for (int i = 1; i <= columnsNumber; i++) {
		   			if(i==3)
		   			{
		   				columnValue = Integer.toString(rs.getInt(i));
		   				System.out.println(columnValue);	
		   			}else {
		   				columnValue = rs.getString(i);
		   				System.out.println(columnValue);
		   			}
		   			resultsArray.add(columnValue);
		   		
		   		}
		   	       }
		   	      }
			if(resultsArray.size()>0) {
				response.setStatus(true);
				resultsArray.forEach((mess) -> 
				{
					response.setMessage(response.getMessage()+" "+mess);
				});
			
			}else {
				response.setStatus(false);
				response.setMessage("didnt get Anything");
			}
			return response;

				}
   
				

			
	
				
			
			

		
		
		
}
