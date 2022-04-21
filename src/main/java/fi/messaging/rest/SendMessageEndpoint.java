package fi.messaging.rest;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.core.Response;
import fi.messaging.app.DatabaseConnection;
import fi.messaging.pojos.Email;
import fi.messaging.pojos.Message;
import fi.messaging.pojos.MessagesPojo;
import fi.messaging.pojos.Receiver;
import fi.messaging.pojos.Sender;
import fi.messaging.security.RSAKeyPairGenerator;
import fi.messaging.security.RSAUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
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
		Email email = mapper.readValue(messageDetails, Email.class);
		int messageId = -1;
		int sQuery=0;
		int rQuery=0;
		Response response =null;
		ArrayList<Message> messagesList = new ArrayList<Message>();
		int senderId = Integer.parseInt(email.getSenderId());
		List<String> receiversArray = email.getReceivers();
		XSSFWorkbook workbook;
		  // spreadsheet object
        XSSFSheet spreadsheet;
        File file = new File("./GFGsheet.xlsx"); 
        byte[] privatebytes = Files.readAllBytes(Paths.get("./key.key"));
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privatebytes);
        byte[] publicbytes = Files.readAllBytes(Paths.get("./key.pub"));
        X509EncodedKeySpec ks1 = new X509EncodedKeySpec(publicbytes); 
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey pvts = kf.generatePrivate(ks); //private key to decrypt the secret key
        PublicKey pubkey= kf.generatePublic(ks1); // public key to encrypt the secret key
        //saving the private key to file
        File privateFile = new File("./private/file"); //file to save the private key to
         privateFile.createNewFile(); 
         //saving the private key to decrypt the secured file
        try (FileOutputStream fos = new FileOutputStream("./private/file")) {
        	
            fos.write(pvts.getEncoded());
            fos.flush();
            fos.close();
        }
       
        // Generate a DES key
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        SecretKey key = keyGen.generateKey();   //secret key to code the file
        secretKeyEncrypted= RSAUtil.wrapKey( pubkey,key); //encrypted by "RSA/ECB/OAEPWithSHA1AndMGF1Padding" public key
      
        File file2 = new File("./semetrickey.key");
        FileOutputStream oute=new FileOutputStream(file2);
        oute.write(secretKeyEncrypted);

        if(file.exists())
        {   
        	
        	FileInputStream fis = new FileInputStream(file);

        	workbook = new XSSFWorkbook(fis);
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
					
					    	try (FileInputStream in = new FileInputStream(file) ) {
					    		 Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
					 	    	cipher.init(Cipher.ENCRYPT_MODE, key);
					        RSAUtil.processFile(cipher, in, outfile);
					    	}
						 }catch (IOException ex) {
							System.out.println(ex.getMessage());
						}finally {
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

                        	response= Response.status(Response.Status.NOT_FOUND).build();
                        }

					}else {
				
						//response.setErrorMessage("ERROR: Wrong input");
						
						response= Response.status(Response.Status.NOT_ACCEPTABLE).build();
					}
				}catch(Exception e)
				{
					response= Response.serverError().entity(e.getMessage()).build();
				}
			}
			if(messageId!=0 && rQuery>0 && sQuery>0 )
			{
				response= Response.ok(messages).build();
			}

	
		} else if (receiversArray.size() > 5) {
			return Response.status(Response.Status.NOT_ACCEPTABLE).build();
			//response.setErrorMessage("more than 5 receipient");
		} else {
			 response =Response.status(Response.Status.NOT_ACCEPTABLE).build();
	   
		}
		return response;
	
    	
	

	}

	   
	  

	   
}
