package fi.messaging.rest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
import javax.ws.rs.core.Response;
import fi.messaging.security.RSAUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Path("/readmessage")
public class ReadMessageEndpoint {

	@SuppressWarnings("static-access")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	
	public Response readMessage(String email) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		ReceivePojo receive = mapper.readValue(email, ReceivePojo.class);
		 InputStream inputStream = null;
		Response response = null;
		MessagesPojo messages = new MessagesPojo();	
		  String path = "./GFGsheet.xlsx";
		  inputStream = new FileInputStream(path);
		  XSSFWorkbook workBook = (XSSFWorkbook) WorkbookFactory.create(new PushbackInputStream(inputStream));
		  XSSFSheet mySheet = workBook.getSheetAt(0);
		  Iterator<Row> rowIterator = mySheet.iterator();
		  byte[] bytes =null;
		  byte[] decode = null;
		  byte[] privateKey = null;
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
								"SELECT * FROM messaging.secret_keys\n" + "where messages_idmessages=?;" + "",
								Statement.RETURN_GENERATED_KEYS);

						statementsecret.setInt(1, rs.getInt(1));
						ResultSet res = statementsecret.executeQuery();

						
						if (res.next() == false) { 
							
							System.out.println("No secret key for this message");
					
						 while (rowIterator.hasNext()) {
							 
				              Row row = rowIterator.next();
				              Iterator<Cell> cellIterator = row.cellIterator();
				              
				              cellIterator.forEachRemaining((cellItem->{
				            	  try {
									if(cellItem.getRowIndex() +1 == userId && cellItem.getColumnIndex()+1==rs.getInt(1))
									  {
									  Message m = new Message();
										m.setMessagebody(cellItem.getStringCellValue());
										m.setDatetime((rs.getDate(4)));
										m.setIdUser(cellItem.getRowIndex() +1);
										m.setTitle(rs.getString(3));
										m.setId(rs.getInt(1));
										messages.addMessage(m);
									  }
								} catch (SQLException e1) {
									
									Response response2= Response.serverError().entity(e1.getMessage()).build();
									 response2.status(500);
								}
				            	  try {
								
								} catch (Exception e) {
									Response response2= Response.serverError().entity(e.getMessage()).build();
									 response2.status(500);
								} 

				              }));
	
						  }
						}else {

						
						try {
							
						privateKey = res.getBytes(2);
							Message m = new Message();
							m.setMessagebody(getMessageDecrypted(rs.getBytes(2), privateKey));
							m.setDatetime((rs.getDate(4)));
							m.setIdUser(rs.getInt(5));
							m.setTitle(rs.getString(3));
							m.setId(rs.getInt(1));
							messages.addMessage(m);
					
						}catch(SQLDataException s)
						{
							System.out.println("SQL exception "+s.getMessage());
							
						}
					}
						System.out.println("SQL exception "+rs.getInt(1));
					}
           	  inputStream.close();
					
				

				}

			} else {
				 response =Response.status(Response.Status.ACCEPTED).build();
				 response.status(200);

			}
		} catch (Exception e) {

			response= Response.serverError().entity(e.getMessage()).build();
			 response.status(500);

		}

		if (messages.getMessages().size() > 0) {

			response= Response.ok(messages).build();
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