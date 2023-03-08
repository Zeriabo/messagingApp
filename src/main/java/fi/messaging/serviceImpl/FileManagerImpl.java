package fi.messaging.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.PrivateKey;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import fi.messaging.security.RSAUtil;
import fi.messaging.service.FileManagerService;

public class FileManagerImpl implements FileManagerService {

	
	public SecretKey savingMessageToFile(PrivateKey privateKey) throws Exception
	{
		
			
			// Get Symmetric key from the file
			File symmetricKeyFile = new File("./semetrickey.key");

			try (FileInputStream fin = new FileInputStream(symmetricKeyFile)) {
				byte[] secretKeyEncryptedRetrieved = fin.readAllBytes();
				SecretKey		symmetricKey = RSAUtil.unWrapKey(privateKey, secretKeyEncryptedRetrieved);

			
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
}
