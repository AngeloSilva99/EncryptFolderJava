import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;


public class AES256 {

	  @SuppressWarnings("deprecation")
	public static void encrypt(String arquivoSerializado, String outputarquivoencriptado) {
		  try {
		  // file to be encrypted
			FileInputStream inFile = new FileInputStream(arquivoSerializado);

			// encrypted file
			FileOutputStream outFile = new FileOutputStream(outputarquivoencriptado);

			// password to encrypt the file
			String password = MainWindow.textField_1.getText();

			// password, iv and salt should be transferred to the other end
			// in a secure manner

			// salt is used for encoding
			// writing it to a file
			// salt should be transferred to the recipient securely
			// for decryption
			byte[] salt = new byte[8];
			SecureRandom secureRandom = new SecureRandom();
			secureRandom.nextBytes(salt);
			FileOutputStream saltOutFile = new FileOutputStream(MainWindow.textField.getText()+"/salt.enc");
			saltOutFile.write(salt);
			saltOutFile.close();

			SecretKeyFactory factory = SecretKeyFactory
					.getInstance("PBKDF2WithHmacSHA1");
			KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536,
					256);
			SecretKey secretKey = factory.generateSecret(keySpec);
			SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

			//
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			AlgorithmParameters params = cipher.getParameters();

			// iv adds randomness to the text and just makes the mechanism more
			// secure
			// used while initializing the cipher
			// file to store the iv
			FileOutputStream ivOutFile = new FileOutputStream(MainWindow.textField.getText()+"/iv.enc");
			byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			ivOutFile.write(iv);
			ivOutFile.close();

			//file encryption
			byte[] input = new byte[64];
			int bytesRead;

			while ((bytesRead = inFile.read(input)) != -1) {
				byte[] output = cipher.update(input, 0, bytesRead);
				if (output != null)
					outFile.write(output);
			}

			byte[] output = cipher.doFinal();
			if (output != null)
				outFile.write(output);

			inFile.close();
			outFile.flush();
			outFile.close();
			
			
			if (isEncryptedFileCorrupted(outputarquivoencriptado, MainWindow.textField.getText()+"/salt.enc", MainWindow.textField.getText()+"/iv.enc", password)) {
	            System.out.println("O ficheiro encriptado está corrompido ou a chave é inválida.");
	        } else {
	            System.out.println("O ficheiro encriptado não está corrompido.");
	            System.out.println("File Encrypted.");
	        }

			
		  }catch(Exception e) {
			  System.out.println(e);
		  }
	    
	  }
	  
	  
	  @SuppressWarnings("deprecation")
	public static void decrypt(String pastaTemporaria) throws IOException {
		  
		  try {

		  	String password = MainWindow.textField_1.getText();

			// reading the salt
			// user should have secure mechanism to transfer the
			// salt, iv and password to the recipient
			FileInputStream saltFis = new FileInputStream(MainWindow.textField.getText()+"/salt.enc");
			byte[] salt = new byte[8];
			saltFis.read(salt);
			saltFis.close();

			// reading the iv
			FileInputStream ivFis = new FileInputStream(MainWindow.textField.getText()+"/iv.enc");
			byte[] iv = new byte[16];
			ivFis.read(iv);
			ivFis.close();

			SecretKeyFactory factory = SecretKeyFactory
					.getInstance("PBKDF2WithHmacSHA1");
			KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536,
					256);
			SecretKey tmp = factory.generateSecret(keySpec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

			// file decryption
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
			FileInputStream fis = new FileInputStream(pastaTemporaria+"/lockedData.enc");
			FileOutputStream fos = new FileOutputStream(pastaTemporaria+"/lockedData.ser");
			byte[] in = new byte[64];
			int read;
			while ((read = fis.read(in)) != -1) {
				byte[] output = cipher.update(in, 0, read);
				if (output != null)
					fos.write(output);
			}

			byte[] output = cipher.doFinal();
			if (output != null)
				fos.write(output);
			fis.close();
			fos.flush();
			fos.close();
			
			
			System.out.println("File Decrypted.");
		  }catch(Exception e) {
			  File ficheiroapagarerro = new File(pastaTemporaria+"/lockedData.ser");
			  Path pastaerro = Paths.get(pastaTemporaria);
			  Files.delete(ficheiroapagarerro.toPath());
			  Files.move(Paths.get(pastaTemporaria+"/lockedData.enc"), Paths.get(MainWindow.textField.getText()+"/lockedData.enc"), StandardCopyOption.REPLACE_EXISTING);
			  Files.delete(pastaerro);
			  JOptionPane.showMessageDialog(null, "Password de encriptação incorreta",
	            		"Erro", JOptionPane.ERROR_MESSAGE);
			  
			  //System.out.println(e);
		  }
		  
	  }
	  
	  
	  @SuppressWarnings("unused")
	public static boolean isEncryptedFileCorrupted(String encryptedFilePath, String saltFilePath, String ivFilePath, String password) {
	        try {
	            // Read the salt
	            FileInputStream saltFis = new FileInputStream(saltFilePath);
	            byte[] salt = new byte[8];
	            saltFis.read(salt);
	            saltFis.close();

	            // Read the iv
	            FileInputStream ivFis = new FileInputStream(ivFilePath);
	            byte[] iv = new byte[16];
	            ivFis.read(iv);
	            ivFis.close();

	            // Generate the key from the password and salt
	            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
	            SecretKey secretKey = factory.generateSecret(keySpec);
	            SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

	            // Initialize the cipher for decryption
	            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));

	            // Decrypt the file
	            FileInputStream fis = new FileInputStream(encryptedFilePath);
	            CipherInputStream cis = new CipherInputStream(fis, cipher);
	            ObjectInputStream ois = new ObjectInputStream(cis);

	            try {
	                // Try to read the object from the decrypted stream
	                Object obj = ois.readObject();
	                ois.close();
	                fis.close();
	                return false; // File is not corrupted
	            } catch (ClassNotFoundException | IOException e) {
	                System.err.println("Erro ao desserializar o objeto: " + e.getMessage());
	                return true; // File is corrupted
	            }
	        } catch (Exception e) {
	            System.err.println("Erro ao desencriptar o ficheiro: " + e.getMessage());
	            return true; // File is corrupted or decryption error
	        }
	    }
}
