package fi.messaging.rest;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.messaging.app.DatabaseConnection;
import fi.messaging.pojos.User;
import fi.messaging.pojos.UserPojo;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;

@Path("/login")
public class LoginEndpoint {

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	
	public Response login(String loginDetails) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		UserPojo user = mapper.readValue(loginDetails, UserPojo.class);
		Response response = null;
		
		try (Connection c = DatabaseConnection.getConnection()) {

			PreparedStatement p1 = c.prepareStatement("SELECT * FROM users where email=? and password=?");
			p1.setString(1, user.getEmail());
			p1.setString(2, user.getPassword());
			ResultSet r = p1.executeQuery();
			if (r.next()) {
				
				 NewCookie cookie = new NewCookie("user", user.getEmail());
			
				User loggedInUser= new User();
				
				int userId = r.getInt(1);
       			loggedInUser.setIdUser(userId);
       			loggedInUser.setName(r.getString(2));
       			loggedInUser.setEmail(r.getString(3));
       			loggedInUser.setPassword(r.getString(4));
				
		        response= Response.ok(loggedInUser).cookie(cookie).build();
			}else {
				response= Response.notAcceptable(null).build();
			}
		}
		return response;
	
	}
}