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
import javax.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import fi.messaging.app.DatabaseConnection;
import fi.messaging.pojos.Message;
import fi.messaging.pojos.MessagesPojo;
import fi.messaging.pojos.SignInfo;
import fi.messaging.security.RSAUtil;
import fi.messaging.service.MessageService;
import fi.messaging.service.TokenService;
import io.jsonwebtoken.Claims;


@Path("/readmessagefromsender")
public class ReadMessageFromSenderEndpoint {

	@SuppressWarnings("static-access")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	
	public Response readMessage(@QueryParam("senderEmail") String senderEmail, String info) throws Exception {

		Claims userVerified;
		MessageService messageService = null;
		TokenService   tokenService = new TokenService();
		Response response = null;
		MessagesPojo messages = new MessagesPojo();
	    ObjectMapper mapper = new ObjectMapper();
		
		SignInfo user = mapper.readValue(info, SignInfo.class);
		
		
		try {
			userVerified =	tokenService.verifyJWT(user.getToken());
		}
		catch(Exception ex)
		{
			response = Response.serverError().entity(ex.getMessage()).build();
			response.status(500);
			return response;
		}
		try {
			messages = messageService.getMessagesFromSender(info, senderEmail);	
		}
		catch (Exception e) {
			response= Response.serverError().entity(e.getMessage()).build();
			 response.status(500);
		}
		if(messages==null)
		{
			 response =Response.status(Response.Status.ACCEPTED).build();

		}
	if (messages.getMessages().size() > 0) {
			
			
			response = Response.ok(messages).build();
		

		}else if (messages.getMessages().size() == 0) {
			 response =Response.status(Response.Status.ACCEPTED).build();
			
		}
	
	
		return response;
	}
	
	
	
}
