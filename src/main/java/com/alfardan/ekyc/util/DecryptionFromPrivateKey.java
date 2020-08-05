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
import org.springframework.core.io.ClassPathResource;

public class DecryptionFromPrivateKey {
	
	
	private static final String PRIVATE_PATH = "tls/private_key.pem";
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public DecryptionFromPrivateKey() {

	}
	
	public static String decrypt(byte[] buffer) {
		try {
			KeyPair keyPair = readKeyPair(PRIVATE_PATH);
			Cipher rsa;
			rsa = Cipher.getInstance("RSA");
			rsa.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
			byte[] utf8 = rsa.doFinal(buffer);
			return new String(utf8, "UTF8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static KeyPair readKeyPair(String privateKey) throws IOException {
		ClassPathResource resource = new ClassPathResource(privateKey);
		InputStream inputStream = resource.getInputStream();

		Reader fileReader = new InputStreamReader(inputStream);
		PEMReader r = new PEMReader(fileReader);
		try {
			return (KeyPair) r.readObject();
		} catch (IOException ex) {
			throw ex;
		} finally {
			r.close();
			fileReader.close();
		}
	}


}
