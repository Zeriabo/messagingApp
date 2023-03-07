package fi.messaging.rest;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.messaging.app.DatabaseConnection;
import fi.messaging.pojos.TripleKeys;
import fi.messaging.pojos.Email;
import fi.messaging.pojos.Message;
import fi.messaging.pojos.MessagesPojo;
import fi.messaging.pojos.Receiver;
import fi.messaging.pojos.Response;
import fi.messaging.pojos.Sender;
import fi.messaging.security.RSAKeyPairGenerator;
import fi.messaging.security.RSAUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
@Path("/sendmessage")


public class SendMessageEndpoint {
	int row = 0;
	byte[] secretKeyEncrypted;
	 PrivateKey pvtfile;
	@SuppressWarnings({ "resource" })
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST

	public Response sendMessage(String messageDetails) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		Response response = new Response();
		XSSFWorkbook workbook;
        XSSFSheet spreadsheet;
        TripleKeys tripleKeys;
		Email email = mapper.readValue(messageDetails, Email.class);
		int messageId = -1;
		int sQuery=0;
		int rQuery=0;
		
		ArrayList<Message> messagesList = new ArrayList<Message>();
		List<String> receiversArray = email.getReceivers();
		
		int senderId = Integer.parseInt(email.getSenderId());
		
		
		/*
		 * Files
		 */
		File file = new File("./OutGFGsheet.xlsx"); 
		File privateFile = new File("./private/file"); //file to save the private key to
		boolean created=privateFile.createNewFile();
		if(created) {
			tripleKeys  =  generateKeys();
		}else {
			   byte[] privatebytes = Files.readAllBytes(Paths.get("./key.key"));
		        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privatebytes);
		        byte[] publicbytes = Files.readAllBytes(Paths.get("./key.pub"));
		        X509EncodedKeySpec ks1 = new X509EncodedKeySpec(publicbytes); 
		        KeyFactory kf = KeyFactory.getInstance("RSA");
		        
		        PrivateKey pvts = kf.generatePrivate(ks); //private key to decrypt the secret key
		        PublicKey pubkey= kf.generatePublic(ks1); // public key to encrypt the secret key   
		        //Secret key
		        byte[] encodedsecretkeybytes = Files.readAllBytes(Paths.get("./semetrickey.key"));
		        Key key= RSAUtil.unWrapKey(pvts, encodedsecretkeybytes);//right one
		        byte[] secretEncoded=key.getEncoded();
		        
		        
		        SecretKey secretKey=new SecretKeySpec(secretEncoded,0,secretEncoded.length,"DES");

		        tripleKeys = new TripleKeys();
		        tripleKeys.setPrivateKey(pvts);
		        tripleKeys.setPublicKey(pubkey);
		        tripleKeys.setSecretKey(secretKey);
		}
        if(file.exists())
        {    
        FileOutputStream outStream = new FileOutputStream("./GFGsheetDecrypted.xlsx");
    	FileInputStream fis = new FileInputStream(file);
        	
       	 Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	    	cipher.init(Cipher.DECRYPT_MODE, tripleKeys.getSecretKey());
	    	 RSAUtil.processFile(cipher, fis, outStream);
       
	    	 FileInputStream fileInputStream=new FileInputStream("./GFGsheetDecrypted.xlsx");
	    	 
	    	 
        	workbook = new XSSFWorkbook(fileInputStream);
        	spreadsheet=workbook.getSheet("secret keys");
        	if (spreadsheet == null) {
        		spreadsheet = workbook.createSheet("secret keys");
        	}
        	
        	fis.close();
        }else {
        	 workbook = new XSSFWorkbook();
                spreadsheet
                = workbook.createSheet("secret keys");	
     }
        // creating a row object
        XSSFRow row;
        // This data needs to be written (Object[])
        List< byte[]> keyData
            = new ArrayList< byte[]>();
            
		MessagesPojo messages=new MessagesPojo(senderId,messagesList);
		if (receiversArray.size()<= 5) {

			for (String receiverEmail : receiversArray) {
				try (Connection c = DatabaseConnection.getConnection()) {
					PreparedStatement p1 = c.prepareStatement("SELECT * FROM users where email=?");
					p1.setString(1, receiverEmail);
					ResultSet receiverResult = p1.executeQuery();
					if (receiverResult.next()) {

						PreparedStatement p2 = c.prepareStatement("SELECT * FROM users where idusers=?");

					p2.setInt(1, Integer.parseInt(email.getSenderId()));
						java.sql.Timestamp datetime=  new java.sql.Timestamp(email.getDatetime().getTime());
						Message message = new Message(email.getId(), email.getTitle(), email.getMessagebody(),
								datetime , senderId);
		
						RSAKeyPairGenerator	rSAKeyPairGenerator = new RSAKeyPairGenerator();
						
						
						
						PublicKey publicKey	=rSAKeyPairGenerator.getPublicKey();
			          
						byte[] encryptedMessage=RSAUtil.encrypt(message.getMessagebody(),publicKey);
						
				        byte[] privateKey = rSAKeyPairGenerator.getPrivateKey().getEncoded();
				        
						messages.addMessage(message);
						
						keyData.add(privateKey);
					
					
						  
						PreparedStatement s1 = c.prepareStatement("SELECT * FROM users where idusers=?");
						s1.setInt(1, senderId);

						ResultSet rsusers = s1.executeQuery();
						
                        if(rsusers.next()) {
                        	rsusers.first();
                        	PreparedStatement statement = c.prepareStatement(
    								"INSERT INTO messages(`title`, `messagebody`, `datetime`, `nbrofrecipients`, `idsender`) VALUES(?,?,?,?,?)",
    								Statement.RETURN_GENERATED_KEYS);
    				
    						statement.setString(1, message.getTitle());
    						statement.setBytes(2, encryptedMessage);
    						statement.setTimestamp(3,datetime);
    						statement.setInt(4, receiversArray.size());
    						statement.setInt(5, senderId);

    						statement.executeQuery();
    						ResultSet keys = statement.getGeneratedKeys();

    						while (keys.next()) {
    							messageId = keys.getInt(1);
    						}
    						PreparedStatement statementsecret = c.prepareStatement(
    								"INSERT INTO `messaging`.`secret_keys`\n"
    								+ "(`secret_key`,\n"
    								+ "`messages_idmessages`,\n"
    								+ "`messages_idsender`)\n"
    								+ "VALUES\n"
    								+ "(?,?,?);"
    								+ "",
    								Statement.RETURN_GENERATED_KEYS);
    						
    						statementsecret.setBytes(1,privateKey);
    						statementsecret.setInt(2,messageId);
    						statementsecret.setInt(3, senderId);
    						
    					
    						statementsecret.executeQuery();
    						
    				
    						
    						
    						
    						
						Sender sender = new Sender(rsusers.getInt(1), rsusers.getString(2), rsusers.getString(3));
						rsusers.first();

						PreparedStatement ins1 = c
								.prepareStatement("INSERT INTO sender(`users_idusers`, `datetime`) VALUES(?,?) ");
						ins1.setInt(1, sender.getIdUser());
						ins1.setTimestamp(2, datetime);

						 sQuery=ins1.executeUpdate();

						Receiver rec = new Receiver(receiverResult.getInt(1), receiverResult.getString(2), receiverResult.getString(3));
						
						//writing to file
						 row = spreadsheet.getRow(rec.getIdUser()-1); // row user 
						if(row==null)
						{
							 row = spreadsheet.createRow(rec.getIdUser()-1); // row user 
						}
						 Cell cell = row.createCell(messageId-1); //column message id 
						 cell.setCellValue(message.getMessagebody());
						 
						 
						 FileOutputStream out = new FileOutputStream(file);
						 try {
						 workbook.write(out);
						 FileOutputStream outfile = new FileOutputStream("./OutGFGsheet.xlsx");
						 RSAUtil.wrapKey(tripleKeys.getPublicKey(), tripleKeys.getSecretKey());
					    	try (FileInputStream in = new FileInputStream(file) ) {
					    		 Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
					 	    	cipher.init(Cipher.ENCRYPT_MODE, tripleKeys.getSecretKey());
					        RSAUtil.processFile(cipher, in, outfile);
					    	}
						 }catch (IOException ex) {
							System.out.println(ex.getMessage());
						}finally {
							File fileToDelete=new File("./GFGsheetDecrypted.xlsx");
							fileToDelete.delete();
							out.flush();
							out.close();
							
						}
						if (messageId >= 0) {
							PreparedStatement ins2 = c.prepareStatement(
									"INSERT INTO receiver(`users_idusers`, `datetime`, `messages_idmessages`, `messages_idsender`) VALUES(?,?,?,?) ");
							ins2.setInt(1, rec.getIdUser());
							ins2.setTimestamp(2,datetime);
							ins2.setInt(3, messageId);
							ins2.setInt(4, senderId);
							 rQuery=ins2.executeUpdate();
							
							
						}
                        }else {
                        	response.setStatus(false);
    						response.setErrorMessage("ERROR: Wrong input sender is not found!");
    						response.setCode(500);
    						return response;
                        }

					}else {
						response.setStatus(false);
						response.setErrorMessage("ERROR: Wrong input");
						response.setCode(500);
						return response;
					}
				}catch(Exception e)
				{
					response.setStatus(false);
					response.setErrorMessage(e.getMessage());
					response.setCode(e.hashCode());
					return response;
				}
			}
			if(messageId!=0 && rQuery>0 && sQuery>0 )
			{
				Response successResponse = new Response(200,true,messages);
				return successResponse;
			}

	
		} else if (receiversArray.size() > 5) {
			response.setStatus(false);
			response.setCode(500);
			response.setErrorMessage("more than 5 receipient");
		} else {
			response.setStatus(false);
	     	response.setErrorMessage("ERROR : not inserted");
			response.setCode(500);
		}
	
    	
		return response;

	}
	private TripleKeys generateKeys() throws Exception{
		/*
		 * keys to code and decode the semetric key for encryting the file  
		 */
        byte[] privatebytes = Files.readAllBytes(Paths.get("./key.key"));
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privatebytes);
        byte[] publicbytes = Files.readAllBytes(Paths.get("./key.pub"));
        X509EncodedKeySpec ks1 = new X509EncodedKeySpec(publicbytes); 
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey pvts = kf.generatePrivate(ks); //private key to decrypt the secret key
        PublicKey pubkey= kf.generatePublic(ks1); // public key to encrypt the secret key
        /*
		 * end of getting the keys 
		 */
        File privateFile = new File("./private/file");
         //saving the private key to decrypt the secured file
        try (FileOutputStream fos = new FileOutputStream(privateFile)) {
        	
            fos.write(pvts.getEncoded());
            fos.flush();
            fos.close();
        }
       
        // Generate a DES key
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        SecretKey key = keyGen.generateKey();   //secret key to code the file
        secretKeyEncrypted= RSAUtil.wrapKey( pubkey,key); //encrypted by "RSA/ECB/OAEPWithSHA1AndMGF1Padding"
      
        File file2 = new File("./semetrickey.key");
        FileOutputStream oute=new FileOutputStream(file2);
        oute.write(secretKeyEncrypted);
        
        TripleKeys dualKeys = new TripleKeys();
        dualKeys.setPrivateKey(pvts);
        dualKeys.setSecretKey(key);
        
		return dualKeys;
		
	}

	   
	  

	   
}
