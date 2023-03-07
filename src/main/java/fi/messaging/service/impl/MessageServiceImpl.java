package fi.messaging.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.ws.rs.core.Response;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.messaging.app.DatabaseConnection;
import fi.messaging.pojos.Email;
import fi.messaging.pojos.Message;
import fi.messaging.pojos.MessagesPojo;
import fi.messaging.pojos.Receiver;
import fi.messaging.pojos.Sender;
import fi.messaging.pojos.SignInfo;
import fi.messaging.security.RSAKeyPairGenerator;
import fi.messaging.security.RSAUtil;
import fi.messaging.service.MessageService;
import fi.messaging.service.TokenService;
import io.jsonwebtoken.Claims;

public class MessageServiceImpl implements MessageService
{
	@SuppressWarnings("resource")
	public Response sendMessages(List<String> receiversArray, Email email, MessagesPojo messages, SecretKey secretKeyDecrypted) throws Exception
	{

			
			Claims claims =	TokenService.verifyJWT(email.getToken());
			String senderemail=claims.getIssuer();
			
		int rQuery = 0;
		int sQuery = 0;
		// creating a row object
				XSSFRow row;
				// spreadsheet object
				XSSFSheet spreadsheet;
				
				XSSFWorkbook workbook;
				
				// Decrypted File
				File file = new File("./GFGsheetDecrypted.xlsx");
				

				if (file.exists()) {
					FileInputStream fis = new FileInputStream(file);

					workbook = new XSSFWorkbook(fis);
					spreadsheet = workbook.getSheet("secret keys");
					if (spreadsheet == null) {
						spreadsheet = workbook.createSheet("secret keys");
					}

					fis.close();
				} else if (!file.exists()) {

					throw new Exception("File does not exists");

				} else {
					workbook = new XSSFWorkbook();
					spreadsheet = workbook.createSheet("secret keys");
				}
					
		// This data needs to be written (Object[])
		List<byte[]> keyData = new ArrayList<byte[]>();
		
		int	messageId = 0;
		for (String receiverEmail : receiversArray) {
			try (Connection c = DatabaseConnection.getConnection()) {
				PreparedStatement p1 = c.prepareStatement("SELECT * FROM users where email=?");
				p1.setString(1, receiverEmail);
				ResultSet receiverResult = p1.executeQuery();
				if (receiverResult.next()) {
                   
					PreparedStatement p2 = c.prepareStatement("SELECT * FROM users where email=?");
                   
					p2.setString(1,senderemail);
					ResultSet senderResult = p1.executeQuery();
					senderResult.first();
					int senderId = senderResult.getInt(1);
					messages.setUserid(senderId);
					
					java.sql.Timestamp datetime = new java.sql.Timestamp(email.getDatetime().getTime());
					Message message = new Message(email.getId(), email.getTitle(), email.getMessagebody(), datetime,
							senderId,senderemail);

					RSAKeyPairGenerator rSAKeyPairGenerator = new RSAKeyPairGenerator();
			
					PublicKey publicKey = rSAKeyPairGenerator.getPublicKey();

					byte[] encryptedMessage = RSAUtil.encrypt(message.getMessagebody(), publicKey);

					byte[] privateKey = rSAKeyPairGenerator.getPrivateKey().getEncoded();

					messages.addMessage(message);
					
					keyData.add(privateKey);

					PreparedStatement s1 = c.prepareStatement("SELECT * FROM users where idusers=?");
					s1.setInt(1, senderId);

					ResultSet rsusers = s1.executeQuery();

					if (rsusers.next()) {
						rsusers.first();
						PreparedStatement statement = c.prepareStatement(
								"INSERT INTO messages(`title`, `messagebody`, `nbrofrecipients`, `idsender`) VALUES(?,?,?,?)",
								Statement.RETURN_GENERATED_KEYS);

						statement.setString(1, message.getTitle());
						statement.setBytes(2, encryptedMessage);
						statement.setInt(3, receiversArray.size());
						statement.setInt(4, senderId);

						statement.executeQuery();
						
						ResultSet keys = statement.getGeneratedKeys();

						while (keys.next()) {
						  	messageId = keys.getInt(1);
						}
						message.setId(messageId);
						PreparedStatement statementsecret = c
								.prepareStatement("INSERT INTO `messaging`.`secret_keys`\n" + "(`secret_key`,\n"
										+ "`messages_idmessages`,\n" + "`messages_idsender`)\n" + "VALUES\n"
										+ "(?,?,?);" + "", Statement.RETURN_GENERATED_KEYS);

						statementsecret.setBytes(1, privateKey);
						statementsecret.setInt(2, messageId);
						statementsecret.setInt(3, senderId);

						statementsecret.executeQuery();

						Sender sender = new Sender(senderId, rsusers.getString(2), rsusers.getString(3));
						rsusers.first();

						PreparedStatement ins1 = c
								.prepareStatement("INSERT INTO sender(`users_idusers`) VALUES(?) ");
						ins1.setInt(1, sender.getIdUser());
			

						 sQuery = ins1.executeUpdate();

						Receiver rec = new Receiver(receiverResult.getInt(1), receiverResult.getString(2),
								receiverResult.getString(3));

						// writing to file
						row = spreadsheet.getRow(rec.getIdUser() - 1); // row user
						if (row == null) {
							row = spreadsheet.createRow(rec.getIdUser() - 1); // row user
						}
						Cell cell = row.createCell(messageId - 1); // column message id
						cell.setCellValue(message.getMessagebody());

						FileOutputStream out = new FileOutputStream(file);
						try {
							workbook.write(out);

							try (FileInputStream in = new FileInputStream(file)) {
								Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
								cipher.init(Cipher.ENCRYPT_MODE, secretKeyDecrypted);
								byte[] outputBytes = cipher.doFinal(in.readAllBytes());
								FileOutputStream outputStream = new FileOutputStream("./GFGsheetEncrypted.xlsx");
								outputStream.write(outputBytes);
								file.delete();
							}
						} catch (IOException ex) {
							return Response.status(500).entity(ex.getMessage()).build();
						} finally {
							out.flush();
							out.close();

						}
						if (messageId >= 0) {
							PreparedStatement ins2 = c.prepareStatement(
									"INSERT INTO receiver(`users_idusers`, `messages_idmessages`, `messages_idsender`) VALUES(?,?,?) ");
							ins2.setInt(1, rec.getIdUser());
							ins2.setInt(2, messageId);
							ins2.setInt(3, senderId);
						 rQuery = ins2.executeUpdate();

						}

					} else {

						return Response.status(500).entity("sender  is not found "+senderemail).build();
					}

				} else {

					return Response.status(Response.Status.NOT_ACCEPTABLE).entity("check receiver email: "+receiverEmail).build();
				}
			} catch (Exception e) {
				return Response.serverError().entity(e.getMessage()).build();
			}
		}
		if (messageId != 0 && rQuery > 0 && sQuery > 0) {
			return Response.ok(messages).build();
		}else {
			return  Response.status(500).build();
		}

	}
	

	public MessagesPojo getMessages(String email, MessagesPojo messages) throws Exception
	{
		byte[] privateKey = null;
		
		try (Connection c = DatabaseConnection.getConnection()) {

			PreparedStatement p1 = c.prepareStatement("SELECT * FROM users where email=?");
			p1.setString(1, email);
			ResultSet r = p1.executeQuery();
			r.first(); //check this
			if (r.first()) {
				int userId = r.getInt(1);

				messages.setUserid(userId);
				r.first();
				if (userId > -1) {

					PreparedStatement statement2 = c.prepareStatement(
							"SELECT messages.idmessages,messages.messagebody,messages.title,messages.created_at,users.email\n"
							+ "FROM receiver receiver, messages , sender, users\n"
							+ "where receiver.messages_idmessages =  messages.idmessages\n"
							+ "And sender.messages_idmessages=  messages.idmessages\n"
							+ "And  receiver.users_idusers=?\n"
							+ "And  sender.users_idusers= users.idusers\n"
							+ "ORDER BY messages.created_at DESC"
									);

					statement2.setInt(1, userId);

					ResultSet rs = statement2.executeQuery();
                    
					while (rs.next()) {
						
						PreparedStatement statementsecret = c.prepareStatement(
								"SELECT * FROM messaging.secret_keys\n" + "where messages_idmessages=?;" + "",
								Statement.RETURN_GENERATED_KEYS);
						statementsecret.setInt(1, rs.getInt(1));
						ResultSet res = statementsecret.executeQuery();

						if (res.next() == false) {
							Message message = new Message();
							Row row = getSheet().getRow(userId - 1);
							for (int cellIndex = row.getFirstCellNum(); cellIndex < row.getLastCellNum(); cellIndex++) {
								Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
								
									if (cell.getRowIndex() + 1 == userId && cell.getColumnIndex() + 1 == rs.getInt(1)) {
										
										java.util.Date date = rs.getTimestamp(4);
										message.setMessagebody(cell.getStringCellValue());
										message.setDatetime(date);
										message.setIdUser(cell.getRowIndex() + 1);
										message.setTitle(rs.getString(3));
										message.setId(rs.getInt(1));
										
										messages.addMessage(message);
										break;
									}
								}

						} else {
							Message message = new Message();
								privateKey = res.getBytes(2);
								message.setMessagebody(getMessageDecrypted(rs.getBytes(2), privateKey));
								java.util.Date date = rs.getTimestamp(4);
								message.setDatetime(date);
								message.setIdUser(rs.getInt(5));
								message.setTitle(rs.getString(3));
								message.setId(rs.getInt(1));
								message.setSender(rs.getString(6));
								messages.addMessage(message);
								
						}

					}
					

				}

			} 
		} catch (Exception e) {

		  throw e;

		}

		return messages;
	}
	public MessagesPojo getMessagesFromSender(String info, String senderEmail) throws Exception
	{
		Claims userVerified;
		MessagesPojo messages = new MessagesPojo();
	    ObjectMapper mapper = new ObjectMapper();
		SignInfo user = mapper.readValue(info, SignInfo.class);

		try {
			userVerified =	TokenService.verifyJWT(user.getToken());
		}catch(Exception ex)
		{

			throw ex;
		}
		
		try (Connection c = DatabaseConnection.getConnection()) {

			PreparedStatement p1 = c.prepareStatement("SELECT * FROM users where email=?");
			p1.setString(1, userVerified.getIssuer());
			ResultSet reciever = p1.executeQuery();
			
			PreparedStatement s1 = c.prepareStatement("SELECT * FROM users where email=?");
			s1.setString(1, senderEmail);
			ResultSet rsender = s1.executeQuery();
			
			reciever.first();
			rsender.first();
			if (reciever.first() && rsender.first()) {
				
				int userId = reciever.getInt("idusers");
                int senderId=rsender.getInt("idusers");
				messages.setUserid(userId);
				reciever.first();
				if (userId > -1) {

					PreparedStatement statement2 = c.prepareStatement(
							"SELECT messages.idmessages,messages.messagebody,messages.title,messages.created_at,messages.idsender "
									+ "  FROM receiver, messages , users sender, users receivers "
									+ "where receiver.messages_idmessages =  messages.idmessages\n"
									+ "And sender.idusers=  messages.idsender\n"
									+ "And receivers.idusers= receiver.users_idusers\n"
									+ "And  receiver.users_idusers=? "
									+ "And sender.idusers=?"
									+ "ORDER BY messages.created_at ASC");

					statement2.setInt(1, userId);
					statement2.setInt(2, senderId);

					ResultSet rs = statement2.executeQuery();
					rs.first();
					while (rs.next()) {

						PreparedStatement statementsecret = c.prepareStatement(
								"SELECT * FROM messaging.secret_keys\n" + "where messages_idmessages=?;" + "",
								Statement.RETURN_GENERATED_KEYS);

						statementsecret.setInt(1, rs.getInt(1));
						ResultSet res = statementsecret.executeQuery();

						res.first();
						byte[] privateKey = res.getBytes(2);

						Message m = new Message();
						m.setMessagebody(getMessageDecrypted(rs.getBytes(2), privateKey));
						m.setDatetime((rs.getDate(4)));
						m.setIdUser(rs.getInt(5));
						m.setTitle(rs.getString(3));
						messages.addMessage(m);
			
					}

				}

			}else {
				return null;
			}
			
		} catch (Exception e) {

		  throw e;

		}


		return messages;
	}
	public String getMessageDecrypted(byte[] message, byte[] privateKeyBytes)
			throws SQLException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidParameterSpecException, InvalidAlgorithmParameterException, BadPaddingException,
			IllegalBlockSizeException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
		PrivateKey privateKey2 = keyFactory.generatePrivate(privateKeySpec);
		String ms = RSAUtil.decrypt(message, privateKey2);

		return ms;
	}
	
public XSSFSheet getSheet() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException
{
	InputStream inputStream;
	KeyFactory kf = KeyFactory.getInstance("RSA");
	File file = new File("./GFGsheetDecrypted.xlsx");
	String path = "./GFGsheetEncrypted.xlsx";
	

	// Getting the private key
	byte[] privatebytes = Files.readAllBytes(Paths.get("./key.key"));
	PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privatebytes);
	PrivateKey pvts = kf.generatePrivate(ks);
	File inputFile = new File(path);
	// Get Symmetric key from the file
	File symmetricKeyFile = new File("./semetrickey.key");

	try (FileInputStream fin = new FileInputStream(symmetricKeyFile)) {
		byte[] secretKeyEncryptedRetrieved = fin.readAllBytes();
		SecretKey secretKeyDecrypted = RSAUtil.unWrapKey(pvts, secretKeyEncryptedRetrieved);
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKeyDecrypted);
		try (FileInputStream inputStream1 = new FileInputStream(inputFile)) {
			byte[] inputBytes = new byte[(int) inputFile.length()];
			inputStream1.read(inputBytes);
			byte[] outputBytes = cipher.doFinal(inputBytes);
			FileOutputStream outputStream = new FileOutputStream(file);
			outputStream.write(outputBytes);
			inputStream1.close();
			outputStream.close();
		}

	}

	inputStream = new FileInputStream(file);
	XSSFWorkbook workBook = (XSSFWorkbook) WorkbookFactory.create(new PushbackInputStream(inputStream));
	XSSFSheet mySheet = workBook.getSheetAt(0);
	inputStream.close();
	
	return mySheet;
}

}