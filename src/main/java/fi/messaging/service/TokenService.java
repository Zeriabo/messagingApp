package fi.messaging.service;




import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

import io.jsonwebtoken.MalformedJwtException;

import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public interface TokenService {


	public  String createJWT(String id, String issuer, String subject, long ttlMillis) throws Exception;
	
	
	public  Claims verifyJWT(String jwt) throws  ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException;
	  
}
