package fi.messaging.service;


import fi.messaging.pojos.SignedUser;
import fi.messaging.pojos.User;

public interface UserService {


	public abstract User register(User body) throws Exception;
	
	public abstract User createRequest(User body) throws Exception;
		
	public abstract SignedUser signIn(String username, String password) throws Exception;
	
	public abstract boolean changePassword(String email, String password) throws Exception;
	
	public abstract boolean validateSignIn(String token) throws Exception;
	
	public abstract int checkRequestsCounts() throws Exception;
	
	public abstract void createRequestedContracts() throws Exception;
	
	public abstract User getSecretQuestionAnswer(String email) throws Exception;
}