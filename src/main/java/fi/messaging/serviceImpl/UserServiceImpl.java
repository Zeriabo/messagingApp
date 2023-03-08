package fi.messaging.serviceImpl;

import java.sql.Connection;
import io.jsonwebtoken.SignatureException;
import java.util.ServiceLoader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import fi.messaging.app.DatabaseConnection;
import fi.messaging.exceptions.IncorrectCredentialsException;
import fi.messaging.exceptions.UserDoesNotExistsException;
import fi.messaging.exceptions.UserExistsException;
import fi.messaging.pojos.SignedUser;
import fi.messaging.pojos.User;
import fi.messaging.service.TokenService;
import fi.messaging.service.UserService;

public class UserServiceImpl implements UserService {

	
	TokenService tokenService = getTokenService();
	public User register(User user) throws Exception {

		try (Connection c = DatabaseConnection.getConnection()) {
			PreparedStatement s = c.prepareStatement("SELECT * FROM users where idusers=?");
			s.setInt(1, user.getIdUser());
			ResultSet result = s.executeQuery();
			if (result.next()) {

				throw new UserExistsException();
			}

			PreparedStatement statement = c.prepareStatement(
					"INSERT INTO users(name,email,password,dateofbirth,secretquestion,secretanswer,active) "
							+ " VALUES(?,?,?,?,?,?,?)");
			statement.setString(1, user.getName());
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getPassword());
			statement.setDate(4, new java.sql.Date(user.getDateofbirth().getTime()));
			statement.setString(5, user.getSecretquestion());
			statement.setString(6, user.getSecretanswer());
			statement.setBoolean(7, user.isActive());

			int insertresult = statement.executeUpdate();

			if (insertresult > 0) {
				return user;
			} else {
				return null;
			}
		} catch (Exception e) {
			throw e;
		}

	}

	@Override
	public SignedUser signIn(String email, String password) throws Exception,IncorrectCredentialsException {

		SignedUser user = new SignedUser();
		String bearerToken = "";
	
		try (Connection c = DatabaseConnection.getConnection()) {
			PreparedStatement s = c.prepareStatement("SELECT * FROM users where email=? and password=?");
			s.setString(1, email);
			s.setString(2, password);
			ResultSet result = s.executeQuery();
			
		

			if (result.next()) {
				user.setIdUser(result.getInt(1));
				user.setEmail(result.getString("email"));
				user.setName(result.getString("name"));
				user.setDateofbirth(result.getDate("dateofbirth"));
				user.setActive(result.getBoolean("active"));
				user.setToken(bearerToken);
				//Create a JWTToken
				try {
					bearerToken = tokenService.createJWT(Integer.toString(user.getIdUser()), user.getEmail(), "bearerToken",
							7 * 24 * 60 * 60 * 1000);
				    user.setToken(bearerToken);
				} catch (Exception exp) {
					throw exp;
				}
				
				//Verify token:
				try {

					 tokenService.verifyJWT(bearerToken);

				} catch (SignatureException sexp) {
					throw sexp;
				}
				return user;
			}else {
			   throw  new IncorrectCredentialsException("Incorrect input !");
			}

		}
	
	}

	@Override
	public boolean changePassword(String email, String password) throws Exception {
	
		
		try (Connection c = DatabaseConnection.getConnection()) {
			PreparedStatement s = c.prepareStatement("UPDATE users set password=? where email=?");
			s.setString(1, password);
			s.setString(2, email);
			int result = s.executeUpdate();
			 if(result>0){
		
			   return true;
			 }else {
				 return false;
			 }
			 
		}
		
	}

	public boolean validateSignIn(String token) throws Exception
	{
		
		try {

			tokenService.verifyJWT(token);

		} catch (SignatureException sexp) {
			
			throw sexp;
		}
		
		return true;
		
	}

	@Override
	public User createRequest(User user) throws Exception {
	
		try (Connection c = DatabaseConnection.getConnection()) {
			PreparedStatement s = c.prepareStatement("SELECT * FROM users where email=?");
			s.setString(1, user.getEmail());
			ResultSet result = s.executeQuery();
			if (result.next()) {

				throw new UserExistsException("User already exists!");
			}

			PreparedStatement statement = c.prepareStatement(
					"INSERT INTO user_requests(name,email,password,dateofbirth,secretquestion,secretanswer)"
							+ " VALUES(?,?,?,?,?,?)");
			statement.setString(1, user.getName());
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getPassword());
			statement.setDate(4, new java.sql.Date(user.getDateofbirth().getTime()));
			statement.setString(5, user.getSecretquestion());
			statement.setString(6, user.getSecretanswer());

			int insertresult = statement.executeUpdate();

			if (insertresult > 0) {
				return user;
			}else {
				
				return null;
			}
		} catch (Exception e) {
			throw e;
		}

	}

	@Override
	public int checkRequestsCounts() throws Exception {
		int count = 0;
		try (Connection c = DatabaseConnection.getConnection()) {
			
		PreparedStatement statement = c.prepareStatement(
				
				"Select count(*) as count from  user_requests");
		  ResultSet rs = statement.executeQuery();
		  
		  while (rs.next()) {
			  count = rs.getInt(1);
		    }
		}catch (Exception e) {
			throw e;
		}
		return count;
	}

	@Override
	public void createRequestedContracts() throws Exception {

		try (Connection c = DatabaseConnection.getConnection()) {
	PreparedStatement statement = c.prepareStatement(
				
				"Select *  from  user_requests");
		  ResultSet rs = statement.executeQuery();
		  
		while(rs.next())
		{
			
			User user= new User();
			user.setIdUser(rs.getInt("id"));
			user.setName(rs.getString("name"));
			user.setDateofbirth(rs.getDate("dateofbirth"));
			user.setEmail(rs.getString("email"));
			user.setActive(true);
			user.setPassword(rs.getString("password"));
			user.setSecretanswer(rs.getString("secretanswer"));
			user.setSecretquestion(rs.getString("secretanswer"));
			
			PreparedStatement insertStatement = c.prepareStatement("INSERT INTO users(name,email,password,dateofbirth,secretquestion,secretanswer,active)"
					+ "     values(?,?,?,?,?,?,?)");			
			insertStatement.setString(1, user.getName());
			insertStatement.setString(2, user.getEmail());
			insertStatement.setString(3, user.getPassword());
			insertStatement.setDate(4, new java.sql.Date(user.getDateofbirth().getTime()));
			insertStatement.setString(5, user.getSecretquestion());
			insertStatement.setString(6, user.getSecretanswer());
			insertStatement.setBoolean(7, user.isActive());
			
			int rowsaffected = insertStatement.executeUpdate();
			if(rowsaffected>0)
			{
				PreparedStatement deleteStatement = c.prepareStatement("DELETE FROM user_requests where id=?");			
				
				deleteStatement.setInt(1, user.getIdUser());
				deleteStatement.executeUpdate();
			}

		}
		  
		}		  
	}

	@Override
	public User getSecretQuestionAnswer(String email) throws Exception {
		
		
		try (Connection c = DatabaseConnection.getConnection()) {
			PreparedStatement s = c.prepareStatement("SELECT * FROM users where email=?");
			s.setString(1, email);
			ResultSet result = s.executeQuery();
            User user = new User();
            System.out.println(result.getRow());
				if (result.next()) {
					user.setIdUser(result.getInt(1));
					user.setEmail(result.getString("email"));
					user.setName(result.getString("name"));
					user.setDateofbirth(result.getDate("dateofbirth"));
					user.setSecretanswer(result.getString("secretanswer"));
					user.setSecretquestion(result.getString("secretQuestion"));
					user.setActive(result.getBoolean("active"));
					

					return user;
				}else {
				   throw  new UserDoesNotExistsException("User does not exists!");
				}

		} catch (Exception e) {
			throw e;
		}
		
		
		
	}
	public static TokenService getTokenService() {
	   
	 ServiceLoader<TokenService> serviceLoader =ServiceLoader.load(TokenService.class);
	 for (TokenService provider : serviceLoader) {
	     return provider;
	 }
	 throw new NoClassDefFoundError("Unable to load a driver "+TokenService.class.getName());
	}
}
