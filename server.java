import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class server {
	
    public static void main(String args[]) throws Exception
    {
	ServerSocket s1 = new ServerSocket(1024);
	Socket ss = s1.accept();
	Scanner sc = new Scanner(ss.getInputStream());
	
	//Read in file for encryption***************************************
	FileInputStream inFile = new FileInputStream("plaintext.txt");
	
	//Designate output file
	FileOutputStream outFile = new FileOutputStream("encryptedfile.aes");
	
	//Password-based encryption so that client and server can reliably duplicate the key
	//this must be the same as the decryption file
	String password = "hellothisisakey";
	
	//generate salt<<This must be sent along with the encrypted file!
	byte[] salt = new byte[8];
	SecureRandom secureRandom = new SecureRandom();
	secureRandom.nextBytes(salt);
	FileOutputStream saltOutFile = new FileOutputStream("salt.enc");
	saltOutFile.write(salt);
	saltOutFile.close();
	//generate key
	SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
	SecretKey secretKey = factory.generateSecret(keySpec);
	SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
	
	//generate cipher text
	Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	cipher.init(Cipher.ENCRYPT_MODE, secret);
	AlgorithmParameters params = cipher.getParameters();
	
	//generate IV <<This must be sent along with the encrypted file!
	FileOutputStream ivOutFile = new FileOutputStream("iv.enc");
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
	
	System.out.println("File Encrypted.");

	//TRANSFER TO CLIENT***************************************
	File f1 = new File("encryptedfile.aes");
	
	byte [] bytearray  = new byte [(int)f1.length()];
	FileInputStream fin = new FileInputStream(f1);
	BufferedInputStream bin = new BufferedInputStream(fin);
	bin.read(bytearray,0,bytearray.length);
	OutputStream os = ss.getOutputStream();
	System.out.println("Sending File...");
	os.write(bytearray,0,bytearray.length);
	os.flush();
	ss.close();
	System.out.println("the file has been transferred!");
		
    }//end static void main
    
}//end public class server
