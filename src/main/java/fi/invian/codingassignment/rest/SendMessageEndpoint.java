package fi.invian.codingassignment.rest;

import javax.inject.Inject;
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
import fi.invian.codingassignment.pojos.Receiver;
import fi.invian.codingassignment.pojos.SendPojo;
import fi.invian.codingassignment.pojos.Sender;

import java.io.Console;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
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

@Path("/sendmessage")
public class SendMessageEndpoint {
	int row =0;
     @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST                      
//	    public SendPojo sendMessage(@PathParam("id") long id,@PathParam("senderId") long senderId,
//	    		@PathParam("title") String title, @PathParam("messagebody") String messagebody,
//	    		@PathParam("datetime") String datetime,@PathParam("receiptsnbr") int nbr, @PathParam("receivers")  String receivers) throws Exception {
//		
    public Response sendMessage(String messageDetails) throws Exception {
	 
    	 ObjectMapper mapper = new ObjectMapper();
    	Email email= mapper.readValue(messageDetails,Email.class);
    	int messageId = -1;
    	
  
		Response response = new Response();
		 int senderId=Integer.parseInt(email.getSenderId());
		String[] receiversArray= email.getReceivers().split(","); // if length >5 gives error
		for(String e : receiversArray) {
		System.out.print(e);
		}
		for(String receiverEmail : receiversArray) {
			try(Connection c = DatabaseConnection.getConnection())
			{
				 PreparedStatement p1 = c.prepareStatement("SELECT * FROM users where email=?");
				 p1.setString(1,receiverEmail);
				  ResultSet r = p1.executeQuery();
				  if (r.next()) {
					 
			           PreparedStatement p2 = c.prepareStatement("SELECT * FROM users where idusers=?");
			   
		            	p2.setInt(1,Integer.parseInt(email.getSenderId()));
		            	
		            	Message message= new Message(email.getId(), email.getTitle(), email.getMessagebody(),email.getDatetime(),  senderId);
		            	 String sql="INSERT INTO messages(`title`, `messagebody`, `datetime`, `nbrofrecipients`, `idsender`) VALUES(?,?,?,?,?) ";
			            	
		            PreparedStatement statement = c.prepareStatement("INSERT INTO messages(`title`, `messagebody`, `datetime`, `nbrofrecipients`, `idsender`) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
		            	 statement.setString(1, message.getTitle());
		            	 statement.setString(2, message.getMessagebody());
		            	 statement.setString(3, message.getDatetime().toString());
		            	 statement.setInt(4,receiversArray.length);
		            	 statement.setInt(5,senderId);

		            ResultSet rs = statement.executeQuery();
		            	 ResultSet keys = statement.getGeneratedKeys();
		            	 
		                 while (keys.next()) {
		                     messageId = keys.getInt(1); 
		                 }
		                 System.out.println("Last Key: " + messageId);
		             
		            
						
						
						  
							 PreparedStatement s1 = c.prepareStatement("SELECT * FROM users where idusers=?");
							 s1.setInt(1, senderId);
						
							 ResultSet s = s1.executeQuery();
							s.first();

							 Sender sender= new Sender(s.getInt(1),s.getString(2),s.getString(3));	
							 s.first();
					
						 PreparedStatement ins1 = c.prepareStatement("INSERT INTO sender(`users_idusers`, `datetime`) VALUES(?,?) ");
						 ins1.setInt(1, senderId);
						 ins1.setString(2, email.getDatetime().toString());
						 
					    ins1.executeUpdate(); 
					             
			 Receiver rec= new Receiver(r.getInt(1),r.getString(2),r.getString(3));
			 if(messageId>=0)
			 {
				 PreparedStatement ins2 = c.prepareStatement("INSERT INTO receiver(`receiver_idusers`, `datetime`, `messages_idmessages`, `messages_idsender`) VALUES(?,?,?,?) "); 
				 ins2.setInt(1, r.getInt(1));
					ins2.setString(2, message.getDatetime().toString());
					System.out.println(message.getId()+message.getTitle());
					ins2.setInt(3, messageId); 
					ins2.setInt(4, senderId);
				 ins2.executeUpdate();
			 }
			
			}
			}
		}

	     
		if(messageId>-1) {
			response.setStatus(true);
			response.setMessage("Submitted data "+messageDetails);
		}else {
			response.setStatus(false);
			response.setMessage("didnt insert");
		}
		return response;

		
		}
}
