package com.alfardan.ekyc.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.core.io.ClassPathResource;

/**
 * @author visruthcv
 *
 */
public class CryptographyUtil {
	
	private static final String PRIVATE_PATH = "tls/private_key.pem";
	private static final String PUBLIC_PATH = "tls/public_key.pem";
	
	public static void main(String[] args) throws IOException {

	    Security.addProvider(new BouncyCastleProvider());

	    KeyPair keyPair = readKeyPair(PRIVATE_PATH); 
	    // if the private key is not encripted, pass can be anything.
	    Key publickey = readPublicKey(PUBLIC_PATH); 
	    //Base64 base64 = new Base64();
	    String text = "this is the input text";
	    byte[] encripted;
	    System.out.println("input:\n" + text);
	    encripted = encrypt(publickey, text);
	    System.out.println("cipher:\n" + Base64.encode(encripted));
	    System.out.println("decrypt:\n" + decrypt(keyPair.getPrivate(), encripted));        
	}

	private static byte[] encrypt(Key pubkey, String text) {
	    try {
	        Cipher rsa;
	        rsa = Cipher.getInstance("RSA");
	        rsa.init(Cipher.ENCRYPT_MODE, pubkey);
	        return rsa.doFinal(text.getBytes());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}


	private static String decrypt(Key decryptionKey, byte[] buffer) {
	    try {
	        Cipher rsa;
	        rsa = Cipher.getInstance("RSA");
	        rsa.init(Cipher.DECRYPT_MODE, decryptionKey);
	        byte[] utf8 = rsa.doFinal(buffer);
	        return new String(utf8, "UTF8");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	private static KeyPair readKeyPair(String privateKey) throws IOException {
	    //FileReader fileReader = new FileReader(privateKey);
	    
	    ClassPathResource resource = new ClassPathResource(privateKey);
		InputStream inputStream = resource.getInputStream();
		
		Reader fileReader = new InputStreamReader(inputStream);
	    PEMReader r = new PEMReader( fileReader);//, new DefaultPasswordFinder(keyPassword.toCharArray()));
	    try {
	        return (KeyPair) r.readObject();
	    } catch (IOException ex) {
	        throw ex;
	    } finally {
	        r.close();
	        fileReader.close();
	    }
	}

	private static Key readPublicKey(String privateKey) throws IOException {
		ClassPathResource resource = new ClassPathResource(privateKey);
		InputStream inputStream = resource.getInputStream();
		
		Reader fileReader = new InputStreamReader(inputStream);
	    PEMReader r = new PEMReader( fileReader);//, new DefaultPasswordFinder(keyPassword.toCharArray()));
	    
	    try {
	        return (RSAPublicKey) r.readObject();
	    } catch (IOException ex) {
	        throw ex;
	    } finally {
	        r.close();
	        fileReader.close();
	    }
	}
	

//    private static final String ALGORITHM = "RSA";
//
//    public static byte[] encrypt(byte[] publicKey, byte[] inputData)
//            throws Exception {
//
//        PublicKey key = KeyFactory.getInstance(ALGORITHM)
//                .generatePublic(new X509EncodedKeySpec(publicKey));
//
//        Cipher cipher = Cipher.getInstance(ALGORITHM);
//        cipher.init(Cipher.ENCRYPT_MODE, key);
//
//        byte[] encryptedBytes = cipher.doFinal(inputData);
//
//        return encryptedBytes;
//    }
//
//    public static byte[] decrypt(byte[] privateKey, byte[] inputData)
//            throws Exception {
//
//        PrivateKey key = KeyFactory.getInstance(ALGORITHM)
//                .generatePrivate(new PKCS8EncodedKeySpec(privateKey));
//
//        Cipher cipher = Cipher.getInstance(ALGORITHM);
//        cipher.init(Cipher.DECRYPT_MODE, key);
//
//        byte[] decryptedBytes = cipher.doFinal(inputData);
//
//        return decryptedBytes;
//    }
//
//    public static KeyPair generateKeyPair()
//            throws NoSuchAlgorithmException, NoSuchProviderException {
//
//        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
//
//        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
//
//        // 512 is keysize
//        keyGen.initialize(512, random);
//
//        KeyPair generateKeyPair = keyGen.generateKeyPair();
//        return generateKeyPair;
//    }
//
//    public static void main(String[] args) throws Exception {
//
//        KeyPair generateKeyPair = generateKeyPair();
//
//        byte[] publicKey = generateKeyPair.getPublic().getEncoded();
//        byte[] privateKey = generateKeyPair.getPrivate().getEncoded();
//        System.out.println(new String(publicKey));
//        byte[] encryptedData = encrypt(publicKey, "SWAPNIL".getBytes());
//
//        byte[] decryptedData = decrypt(privateKey, encryptedData);
//
//        System.out.println(new String(decryptedData));
//
//    }

}
