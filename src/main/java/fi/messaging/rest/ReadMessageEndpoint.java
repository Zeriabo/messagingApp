package fi.messaging.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import fi.messaging.pojos.MessagesPojo;
import fi.messaging.pojos.SignInfo;
import javax.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.messaging.service.MessageService;
import fi.messaging.service.TokenService;
import io.jsonwebtoken.Claims;

@Path("/readmessages")
public class ReadMessageEndpoint {

	MessageService messageService;
	TokenService tokenService;
	Claims userVerified;

	@SuppressWarnings("static-access")
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public Response readMessages(String info) throws Exception {

		Response response;
		MessagesPojo messages = new MessagesPojo();
		ObjectMapper mapper = new ObjectMapper();

		SignInfo user = mapper.readValue(info, SignInfo.class);

		try {

			userVerified = tokenService.verifyJWT(user.getToken());

		} catch (Exception ex) {
			response = Response.serverError().entity(ex.getMessage()).build();
			response.status(500);
			return response;
		}
		try {
			messages = messageService.getMessages(userVerified.getIssuer(), messages);

		} catch (Exception e) {

			response = Response.serverError().entity(e.getMessage()).build();
			response.status(500);
			return response;
		}
		response = Response.status(Response.Status.ACCEPTED).build();
		response.status(200);

		if (messages.getMessages().size() > 0) {

			response = Response.ok(messages).build();

			return response;
		}
		
		return response.status(204).entity("No messages").build();
	}

}