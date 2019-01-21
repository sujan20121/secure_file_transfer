import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import java.io.FileInputStream;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class client {

	public static void main(String args[]) throws Exception{		
	    int filesize=1022386;
	    int bytesRead;
	    
	    int currentTot = 0;
	    
	    Scanner scan1 = new Scanner(System.in);
	    
	    Socket new_socket = new Socket("localhost",1024);
	    
	    Scanner scan2 = new Scanner(new_socket.getInputStream());

	    byte [] bytearray  = new byte [filesize];
	    InputStream is = new_socket.getInputStream();
	    FileOutputStream fos = new FileOutputStream("encryptedfile.aes");
	    BufferedOutputStream bos = new BufferedOutputStream(fos);
	    bytesRead = is.read(bytearray,0,bytearray.length);
	    currentTot = bytesRead;
	    do {
		bytesRead =
		    
		    is.read(bytearray, currentTot, (bytearray.length-currentTot));
		if(bytesRead >= 0) currentTot += bytesRead;
		
	    } while(bytesRead > -1);
	    bos.write(bytearray, 0 , currentTot);
	    bos.flush();
	    bos.close();
	    new_socket.close();

	    //DECRYPTION**********************************************
	    //must be the same as the encryption password
	    String password = "hellothisisakey";
	    //import salt file generated from encryption process
	    FileInputStream saltFis = new FileInputStream("salt.enc");
	    byte[] salt = new byte[8];
	    saltFis.read(salt);
	    saltFis.close();
	    
	    // reading the iv from the encryption process
	    FileInputStream ivFis = new FileInputStream("iv.enc");
	    byte[] iv = new byte[16];
	    ivFis.read(iv);
	    ivFis.close();
	    //regenerate key
	    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	    KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
	    SecretKey tmp = factory.generateSecret(keySpec);
	    SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
	    
	    // file decryption
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
	    FileInputStream fis = new FileInputStream("encryptedfile.aes");
	    FileOutputStream fos2 = new FileOutputStream("plainfile_decrypted.txt");
	    byte[] in = new byte[64];
	    int read;
	    while ((read = fis.read(in)) != -1) {
		byte[] output = cipher.update(in, 0, read);
		if (output != null)
		    fos2.write(output);
	    }
	    
	    byte[] output = cipher.doFinal();
	    if (output != null)
		fos2.write(output);
	    fis.close();
	    fos2.flush();
	    fos2.close();
	    System.out.println("File Decrypted.");

	}
}
