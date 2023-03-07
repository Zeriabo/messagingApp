package fi.messaging.rest;

import javax.crypto.SecretKey;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.core.Response;
import fi.messaging.exceptions.EncryptionDecryptionException;
import fi.messaging.pojos.Email;
import fi.messaging.pojos.Message;
import fi.messaging.pojos.MessagesPojo;
import fi.messaging.service.CryptoService;
import fi.messaging.service.FileManager;
import fi.messaging.service.MessageService;
import fi.messaging.service.TokenService;
import io.jsonwebtoken.Claims;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ThreadLocalRandom;

@Path("/sendmessage")
public class SendMessageEndpoint {
	int row = 0;
	byte[] secretKeyEncrypted;
	PrivateKey pvtfile;
	FileManager fileManager = new FileManager();
	SecretKey symmetricKey;

	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public Response sendMessage(String messageDetails) throws Exception {


		ArrayList<Message> messagesList = new ArrayList<Message>();
		Response response;
		ObjectMapper mapper = new ObjectMapper();
	   
		MessageService messageService = getMessageService();
  
		Email email = mapper.readValue(messageDetails, Email.class);
		try {
			
			Claims claims =	TokenService.verifyJWT(email.getToken());
	} catch (Exception ex) {
		response = Response.serverError().entity(ex.getMessage()).build();
		return response;
	}
		// if the date which is set is bigger than Date then postpones the message to be
		// sent on that date which means needed a service which send messages in
		// Specific date
		if (email.getDatetime() == null) {
			email.setDatetime(new Date());
		}
		if (email.getReceivers().size() == 0) {
			return Response.status(Response.Status.BAD_REQUEST).status(500, "there is not receivers").build();
		}
		if (email.getToken() == null) {
			return Response.status(Response.Status.BAD_REQUEST).status(500, "there is not sender").build();
		}
	
		// getting senderId here
		int senderId = 1;

		// getting the receivers in array
		List<String> receiversArray = email.getReceivers();

		KeyFactory kf = KeyFactory.getInstance("RSA");

		// Getting the public key
		byte[] publicbytes = Files.readAllBytes(Paths.get("./key.pub"));
		X509EncodedKeySpec ks1 = new X509EncodedKeySpec(publicbytes);
		// public key to encrypt the secret key
		PublicKey pubkey = kf.generatePublic(ks1);

		// Getting the private key
		byte[] privatebytes = Files.readAllBytes(Paths.get("./key.key"));
		PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privatebytes);
		PrivateKey pvts = kf.generatePrivate(ks);

		// create a challenge
		byte[] challenge = new byte[10000];
		ThreadLocalRandom.current().nextBytes(challenge);

		Signature sig = Signature.getInstance("SHA256withRSA");
		// sign using the private key
		byte[] signature = CryptoService.signWithPrivateKey(sig, pvts, challenge);

		// verify signature using the public key
		sig.initVerify(pubkey);
		sig.update(challenge);

		boolean keyPairMatches = sig.verify(signature);

		if (keyPairMatches) {
			try {


				symmetricKey = fileManager.savingMessageToFile(pvts);


			} catch (Exception ex) {
				throw new EncryptionDecryptionException("Error encrypting/decrypting file", ex);
			}
		}

		MessagesPojo messages = new MessagesPojo(senderId, messagesList);

		if (receiversArray.size() <= 5 && symmetricKey != null) {

			return messageService.sendMessages(receiversArray, email, messages, symmetricKey);

		} else if (symmetricKey == null) {

			return Response.status(500, "SymerticKey is null").build();

		} else if (receiversArray.size() > 5) {
			return Response.status(500, "receivers are more than 5").build();

		} else {
			return Response.status(Response.Status.NOT_ACCEPTABLE).build();

		}

	}
	
	public static MessageService getMessageService() {
	     // load our plugin
     ServiceLoader<MessageService> serviceLoader =ServiceLoader.load(MessageService.class);
     for (MessageService provider : serviceLoader) {
         return provider;
     }
     throw new NoClassDefFoundError("Unable to load a driver "+MessageService.class.getName());
	}
	

}
