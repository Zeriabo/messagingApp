package fi.messaging.rest;



import fi.messaging.service.UserService;
import java.util.ServiceLoader;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.messaging.pojos.SignInfo;
import fi.messaging.pojos.SignedUser;
import fi.messaging.pojos.User;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("/user")
public class UserEndpoints {

	
	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/signin")
	@POST
	public Response signIn(String userInfo ) throws Exception {
        
	      ObjectMapper mapper = new ObjectMapper();
		
		SignInfo user = mapper.readValue(userInfo, SignInfo.class);
		
		Response response = null;
		UserService userService = getUserService();
		
		SignedUser signedUser;
		try {
			
			signedUser = userService.signIn(user.getEmail(),user.getPassword());

			if(signedUser instanceof SignedUser)
			{
				if(signedUser.isActive()) {
					
					 response = Response.ok(signedUser).build();
					 
				}else {
					
					 response = Response.status(204).entity("user is not active").build();
				}
			
			}else {
		     response = Response.status(404).build();
			}
			
		} catch (Exception e) {
			throw e;
		}
	
		
	
		return response;
	
	}
	
	
	
	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/register")
	@POST
	public Response register(String registerDetails) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		User user = mapper.readValue(registerDetails, User.class);
		Response response = null;
		UserService userService = getUserService();
		
		User userCreated;
		try {
			userCreated = userService.register(user);

			if(userCreated instanceof User)
			{
				return Response.ok(userCreated).build();
			}
			
		} catch (Exception e) {
			
			return Response.status(500).entity(e.getMessage()).build();
		}
	
		
	
		return response;
	
	}
	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/change_password")
	@POST
	public Response changePassword(String body) throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		User userToEdit = mapper.readValue(body, User.class);

		UserService userService = getUserService();
		
		boolean changed;
		try {
			changed = userService.changePassword(userToEdit.getEmail(),userToEdit.getPassword());

			if(changed)
			{
				return Response.ok().build();
			}else {
				return Response.status(500).build();
			}
			
			
		} catch (Exception e) {
			return Response.status(500).entity(e.getMessage()).build();
		}
	
	
	}
	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/validate")
	@GET
    public Response validateIfSigned(@QueryParam("token")  String  token) throws Exception {
  
      UserService userService = getUserService();
      
      boolean valid=false;
      try {
    	  
    	  valid =userService.validateSignIn(token);
      }
       catch(Exception exp)
      {
    	 
    	   return Response.status(500).entity(exp.getMessage()).build();
      }
      if(valid)
      {
    	  return Response.ok(valid).build();
    	  
      }else {
    	  
    	  return Response.ok(false).build(); 
      }
      
    	
    }
	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getsecrets")
	@POST
	public Response getSecretQuestionAnswer(String user) throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		User userToEdit = mapper.readValue(user, User.class);
		Response response = null;
		UserService userService = getUserService();

		try {
			userToEdit = userService.getSecretQuestionAnswer(userToEdit.getEmail());

			if(userToEdit instanceof User)
			{
				return Response.ok(userToEdit).build();
			}
			
		} catch (Exception e) {
		
			return Response.status(500).entity(e.getMessage()).build();
		}
	
		return response;
	
	}
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/request")
	@POST
	public Response createUserRequest(String UserDetails) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		User user = mapper.readValue(UserDetails, User.class);
		
		user.setActive(false);
		
		Response response = null;
		UserService userService = getUserService();
		
		User userCreated;
		try {
			userCreated = userService.createRequest(user);

			if(userCreated instanceof User)
			{
				return Response.ok(userCreated).build();
			}
			
		} catch (Exception e) {
		
			return Response.status(500).entity(e.getMessage()).build();
		}
	
		
	
		return response;
	
	}
	public static UserService getUserService() {
	     // load our plugin
      ServiceLoader<UserService> serviceLoader =ServiceLoader.load(UserService.class);
      for (UserService provider : serviceLoader) {
          return provider;
      }
      throw new NoClassDefFoundError("Unable to load a driver "+UserService.class.getName());
	}

}