package fi.messaging.service;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.PrivateKey;
import fi.messaging.security.RSAUtil;

public class FileManager {

	SecretKey symmetricKey;
	
	public FileManager()
	{
		
	}
	

	public SecretKey savingMessageToFile(PrivateKey privateKey) throws Exception
	{
		
			
			// Get Symmetric key from the file
			File symmetricKeyFile = new File("./semetrickey.key");

			try (FileInputStream fin = new FileInputStream(symmetricKeyFile)) {
				byte[] secretKeyEncryptedRetrieved = fin.readAllBytes();
				symmetricKey = RSAUtil.unWrapKey(privateKey, secretKeyEncryptedRetrieved);

			} 
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, symmetricKey);
			File inputFile = new File("GFGsheetEncrypted.xlsx");
			FileInputStream inputStream = new FileInputStream(inputFile);
			byte[] inputBytes = new byte[(int) inputFile.length()];
			inputStream.read(inputBytes);

			byte[] outputBytes = cipher.doFinal(inputBytes);
			FileOutputStream outputStream = new FileOutputStream("./GFGsheetDecrypted.xlsx");
			outputStream.write(outputBytes);
			inputStream.close();
			outputStream.close();

			return symmetricKey;
		
	}
	
}
