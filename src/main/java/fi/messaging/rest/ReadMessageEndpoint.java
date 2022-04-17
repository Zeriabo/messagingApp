package fi.messaging.rest;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.Iterator;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.messaging.app.DatabaseConnection;
import fi.messaging.pojos.Message;
import fi.messaging.pojos.MessagesPojo;
import fi.messaging.pojos.ReceivePojo;
import fi.messaging.pojos.Response;
import fi.messaging.security.RSAUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

@Path("/readmessage")
public class ReadMessageEndpoint {

	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@GET

	public Response readMessage(String email) throws Exception {

		/*
		 * 	byte[] bytes1 = Files.readAllBytes(Paths.get("./key.pub"));
						X509EncodedKeySpec ks11 = new X509EncodedKeySpec(bytes1);
						KeyFactory kf1 = KeyFactory.getInstance("RSA");
						pubkey = kf1.generatePublic(ks11);

						byte[] pbytes = Files.readAllBytes(Paths.get("./key.key"));
						PKCS8EncodedKeySpec pks = new PKCS8EncodedKeySpec(pbytes);
						KeyFactory pkf = KeyFactory.getInstance("RSA");
						 pvtfile = pkf.generatePrivate(pks);
		 
		 * 
		 */
		ObjectMapper mapper = new ObjectMapper();
		ReceivePojo receive = mapper.readValue(email, ReceivePojo.class);
		InputStream inputStream =new FileInputStream("./OutGFGsheet.xlsx");
		FileOutputStream outStream = new FileOutputStream("./GFGsheetDecrypted.xlsx");
		Response response = new Response();
		MessagesPojo messages = new MessagesPojo();
		
		// get the private key from "./private/file" and decrypt the file which is written by byte[] and decrypt the secret key
		
		File privateKeyFile = new File("./private/file");
		FileInputStream fileInputStream = new FileInputStream(privateKeyFile);
		byte[] privateKeyBytes=fileInputStream.readAllBytes(); // reading byte[] private\Key
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	    EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
	    PrivateKey privateKeyFromFile = keyFactory.generatePrivate(privateKeySpec); //privatekey to decrypt the secret key
		
	    
		java.nio.file.Path keypath = Paths.get("./semetrickey.key");
		byte[] encodedKey = Files.readAllBytes(keypath); 
		
		Key key= RSAUtil.unWrapKey(privateKeyFromFile, encodedKey);//right one
		
		byte[] secretEncoded=key.getEncoded();
		
		SecretKey secretKey=new SecretKeySpec(secretEncoded,0,secretEncoded.length,"DES");

		 Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	    	cipher.init(Cipher.DECRYPT_MODE, secretKey);
	    	 RSAUtil.processFile(cipher, inputStream, outStream);
	    	
		inputStream = new FileInputStream("./GFGsheetDecrypted.xlsx");
		 try (XSSFWorkbook workBook = new XSSFWorkbook (inputStream)) {
			XSSFSheet mySheet = workBook.getSheetAt(0);
			Iterator<Row> rowIterator = mySheet.iterator();
			byte[] privateKey = null;
			boolean skip = false;
			try (Connection c = DatabaseConnection.getConnection()) {

				PreparedStatement p1 = c.prepareStatement("SELECT * FROM users where email=?");
				p1.setString(1, receive.getEmail());
				ResultSet r = p1.executeQuery();
				r.first();
				if (r.first()) {
					int userId = r.getInt(1);

					messages.setUserid(userId);
					r.first();
					if (userId > -1) {

						PreparedStatement statement2 = c.prepareStatement(
								"SELECT messages.idmessages,messages.messagebody,messages.title,messages.datetime,messages.idsender "
										+ "  FROM receiver, messages , users sender, users receivers "
										+ "where receiver.messages_idmessages =  messages.idmessages\n"
										+ "And sender.idusers=  messages.idsender\n"
										+ "And receivers.idusers= receiver.users_idusers\n"
										+ "And  receiver.users_idusers=?");

						statement2.setInt(1, userId);

						ResultSet rs = statement2.executeQuery();
			              
						while (rs.next()) {

							PreparedStatement statementsecret = c.prepareStatement(
									"SELECT * FROM messaging.secret_keys\n" + "where messages_idmessages=?;");

							statementsecret.setInt(1, rs.getInt(1));
							ResultSet res = statementsecret.executeQuery();
							res.first();

							try {

								privateKey = res.getBytes(2);

							} catch (SQLDataException exp) {

								skip = true;
								while (rowIterator.hasNext()) {

									Row row = rowIterator.next();
									Iterator<Cell> cellIterator = row.cellIterator();

									cellIterator.forEachRemaining((cellItem -> {
										try {

											if (cellItem.getRowIndex() + 1 == userId
													&& cellItem.getColumnIndex() + 1 == rs.getInt(1)) {
												Message m1 = new Message();
												m1.setMessagebody(cellItem.getStringCellValue());
												m1.setDatetime((rs.getDate(4)));
												m1.setIdUser(rs.getInt(5));
												m1.setTitle(rs.getString(3));
												messages.addMessage(m1);
											}
										} catch (SQLException e1) {
											response.setErrorMessage(e1.getMessage());
										}
										try {
									
										} catch (Exception e) {
											response.setErrorMessage(e.getMessage());
										}

									}));

								}
								inputStream.close();
							}

							if (!skip) {
								Message m = new Message();

								m.setMessagebody(getMessageDecrypted(rs.getBytes(2), privateKey));

								m.setDatetime((rs.getDate(4)));
								m.setIdUser(rs.getInt(5));
								m.setTitle(rs.getString(3));
								messages.addMessage(m);

							}
							skip = false;
						}

					}

				} else {
					response.setStatus(true);
					response.setErrorMessage("no messages");
					response.setCode(444);

				}
			} catch (Exception e) {

				response.setStatus(false);
				response.setErrorMessage(e.getMessage());
				response.setCode(500);

			}
		}
		if (messages.getMessages().size() > 0) {
  messages.printMessages();
			response.setStatus(true);
			response.setMessages(messages);

		}
		return response;
	}

	private String getMessageDecrypted(byte[] message, byte[] privateKeyBytes)
			throws SQLException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidParameterSpecException, InvalidAlgorithmParameterException, BadPaddingException,
			IllegalBlockSizeException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
		PrivateKey privateKey2 = keyFactory.generatePrivate(privateKeySpec);
		String ms = RSAUtil.decrypt(message, privateKey2);

		return ms;
	}

}
