package com.alfardan.ekyc.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.Key;
import java.security.KeyPair;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.core.io.ClassPathResource;

public class EncryptionFromPublicKey {
	
	
	
	private static final String PUBLIC_PATH = "tls/public_key.pem";
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	public EncryptionFromPublicKey() {
		
	}
	public static void main(String[] args) {
		
		String data = "{\r\n" + 
				"    \"login\": [{\r\n" + 
				"				\"username\": \"SWAPNIL\",\r\n" + 
				"				\"password\": \"Swap23$38200\"\r\n" + 
				"			 }],\r\n" + 
				"			 \r\n" + 
				"	\"device\":[{\r\n" + 
				"				\"uuid\": \"AB:89:FA:80:DA\",\r\n" + 
				"				\"iemi\": \"234234324234234\"\r\n" + 
				"			 }] 		 \r\n" + 
				"    \r\n" + 
				"}";
				
				EncryptionFromPublicKey.encrypt(Base64.encode(data.getBytes()));
	}
	
	public static byte[] encrypt(byte[] text) {
	    try {
	    	Key publickey = readPublicKey(PUBLIC_PATH); 
	        Cipher rsa;
	        rsa = Cipher.getInstance("RSA");
	        rsa.init(Cipher.ENCRYPT_MODE, publickey);
	        return rsa.doFinal(text);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	private static Key readPublicKey(String privateKey) throws IOException {
		ClassPathResource resource = new ClassPathResource(privateKey);
		InputStream inputStream = resource.getInputStream();
		
		Reader fileReader = new InputStreamReader(inputStream);
	    PEMReader r = new PEMReader( fileReader);
	    
	    try {
	        return (RSAPublicKey) r.readObject();
	    } catch (IOException ex) {
	        throw ex;
	    } finally {
	        r.close();
	        fileReader.close();
	    }
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
