package fi.messaging.service.impl;

import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import fi.messaging.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class TokenServiceImpl implements TokenService {
	public String createJWT(String id, String issuer, String subject, long ttlMillis) throws Exception {
		 
	    //The JWT signature algorithm we will be using to sign the token
	    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
	    
	//    SecretKey secret = CryptoService.generateSecretKey(); secret.getEncoded().toString()
	    
	    long nowMillis = System.currentTimeMillis();
	    Date now = new Date(nowMillis);
	 
	    //We will sign our JWT with our ApiKey secret
	    byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("TOKEN");
	    Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
	 
	    //Let's set the JWT Claims
	    JwtBuilder builder = Jwts.builder().setId(id)
	                                .setIssuedAt(now)
	                                .setSubject(subject)
	                                .setIssuer(issuer)
	                                .signWith(signatureAlgorithm, signingKey);
	    //if it has been specified, let's add the expiration
	    if (ttlMillis >= 0) {
	    long expMillis = nowMillis + ttlMillis;
	        Date exp = new Date(expMillis);
	        builder.setExpiration(exp);
	    }
	 
	    //Builds the JWT and serializes it to a compact, URL-safe string
	    return builder.compact();
	}
	
	public  Claims verifyJWT(String jwt) throws  ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException  {
	    //This line will throw an exception if it is not a signed JWS (as expected)
	    Claims claims = Jwts.parser()
	            .setSigningKey("TOKEN")
	            .parseClaimsJws(jwt).getBody();

	    
	    return claims;
	}
}
