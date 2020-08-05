package com.alfardan.ekyc.api.rest;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.websocket.server.PathParam;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/cbdapp")
public class DecryptController {
	private final Logger log = LoggerFactory.getLogger(DecryptController.class);
	private static final String UNWRAPALGO = "RSA/ECB/OAEPWithSHA256AndMGF1Padding";
	private static final String PKCSPADDING = "AES/CBC/PKCS5Padding";
	private static final String PUBLICKEYPATH = "tls/encrypttest.cbd.cer";
	private static final String X509 = "X.509";
	private static final String AESALGO ="AES";

	@PostMapping("/decrypt")
	public String decryptResponse( @RequestParam(name = "payload") String payload) throws Exception {
		log.info("payload="+payload);
		
		String encryptedKeyString, ivString, encryptedDataString;

		String[] arrOfStr = null;
		if(payload.contains(",")) {
			arrOfStr = payload.split(",");
		}else {
			return "damaged payload";
		}

		String[] encryptedKeyArrayg = null;
		
		if(arrOfStr[1].contains(":")) {
			encryptedKeyArrayg= arrOfStr[1].split(":");
		}else {
			return "damaged payload";
		}
		
		encryptedKeyString = encryptedKeyArrayg[1].replace("\"", "").trim();

		String[] ivArrayg = null;
		if(arrOfStr[3].contains(":")) {
			ivArrayg= arrOfStr[3].split(":");
		}else {
			return "damaged payload";
		}
		ivString = ivArrayg[1].replace("\"", "").replace("}", "").trim();

		String[] encryptedArrayg = arrOfStr[0].split(":");
		encryptedDataString = encryptedArrayg[1].replace("\"", "").trim();

		SecretKey decryptedKey = unwrapKeyWithPubKey(encryptedKeyString);
		byte[] decryptedData = decryptAesCbcPkcs7(decryptedKey, tobyteArray(ivString),
				tobyteArray(encryptedDataString));
		return new String(decryptedData);

	}

	private static SecretKey unwrapKeyWithPubKey(String encryptedKey) throws Exception {
		Cipher cipherdec = null;
		try {
			cipherdec = Cipher.getInstance(UNWRAPALGO);
			cipherdec.init(Cipher.UNWRAP_MODE, getServerPublicKey(PUBLICKEYPATH));
		} catch (Exception e) {
			System.out.print("Exception:- " + e.toString());
		}
		return (SecretKey) cipherdec.unwrap(tobyteArray(encryptedKey), AESALGO, Cipher.SECRET_KEY);
	}

	private static byte[] decryptAesCbcPkcs7(SecretKey key, byte[] iv, byte[] decrypted) throws Exception {
		Cipher cipher = Cipher.getInstance(PKCSPADDING);
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		return cipher.doFinal(decrypted);

	}

	public static PublicKey getServerPublicKey(String KeystoreFileName) throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
		ClassPathResource resource = new ClassPathResource(KeystoreFileName);
		InputStream inputStream = resource.getInputStream();
		
		CertificateFactory f = CertificateFactory.getInstance(X509);
		X509Certificate certificate = (X509Certificate) f.generateCertificate(inputStream);
		PublicKey pk = (RSAPublicKey) certificate.getPublicKey();
		return pk;
	}

	private static byte[] tobyteArray(String input) {
		byte[] output = null;
		try {
			output = DatatypeConverter.parseHexBinary(input);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return output;
	}
	
	
	public static void main(String[] args) {
		DecryptController cbdResponseResource = new DecryptController();
		
		String payload ="{\"encryptedData\":\"cca932068048ed602ee8266b98e65b2b0c0d43f4d0a60ba1f6918e1d8f96b49f0bbf6922cceafd9cda69cf6e053fbee3e822bd5a20b5309b8d849640f842c1ded7eb3cf79b239113e740cd58816027e78337f4f6170ae698f9d5880edbb375638602a9816761727693edf5c87dbeaabbab36b03b8d339735702f52570237a83afa99684b30d930c5b0a1b69d0ffb333d71ec3e6434b6347ee93b5825a8d36b8142179b49a8a370bc452085320a3b83a30c76fde6e0d71077fe196b6005fdca88e728a2deb4e5699f63a7cdfc0fa61089c6c2b4ffeefe18aed40242ffb8eeaa070ce3e90a5339a129801958f89b770ec2a455b68dd2d3d23a299e2c9d567f1f031b290d2b5dcc6416676bd401e74b02c1c4f8d9a4b8e8e9e32069eae9f49a93398415aa83824c8e2027d3a64676d5bf8d2eb3da3e8c2d7dfedc3117cfe866d7efa713c19e5961dc44ed8ff5c07c13a87b\",\"encryptedKey\":\"8250412b4b40a493db8c31d9d2f5cc1462b712df1d7595a28eff49422521dfa8457466691738c84b188bb88de3c4f0b7d66ef663c87ed970543c8030f1c2983d31477e3e30eee81864cf4205120bb8fdfabe9d78e0ea6ef2ec007d91891f2cd7b0d3bb56cc93fdc326cabb0ec7896a991184c023929e4ab77fe518e666257e694f20ad058c32a6c8d3f7012720c1aaf8509a7f9851a07ea0c691f3e62959d010bad460da041e87c363fa545aa05d5f1393176fed1d9ff4909bd6e1a537a948bd3d23f0b82e1eac643e8cf85721571d550313e398c8c0be2cb0c58fc194ffbdccd485076edcf692dc8134f581d2d2b85809e58239de4157468f0f9bbab1494b56\",\"oaepHashingAlgorithm\":\"SHA256\",\"iv\":\"c89fba5fc3a7809953d954d3d7bb6b8f\"}";
		
		try {
			String response = cbdResponseResource.decryptResponse(payload);
			
			System.out.println(response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
}
