package com.alfardan.ekyc.util;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.bouncycastle.util.encoders.Base64;

public class EncodeWithSecretKey {
	Cipher cipher;
	SecretKey myDesKey;

	private static final String secretkey = "NTQwZDFhMDBkZWIwMjFmYjhkNWRkNTdkNzRhODlhZTEwNjBlNWE1ZjlmY2JmOTg5OWJhZjAwYmRiN2I1YmRjOTQ2NTMyY2U5YTQzNGRhMmM4MGU1M2Y4MjVkMjkxMjkzMGNiMWZkM2FhZGY0YzRiOGFmZmQxOTM5YzE4MTljOTE=";

	public EncodeWithSecretKey() {
		try {
			DESKeySpec dks = new DESKeySpec(secretkey.getBytes());
			SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
			myDesKey = skf.generateSecret(dks);
			cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] encodeBeanToString(String jsonString)
			throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		byte[] result = null;
		// String jsonString = JSONUtils.toJSON(student);
		byte[] text = jsonString.getBytes();
		cipher.init(Cipher.ENCRYPT_MODE, myDesKey);
		byte[] textEncrypted = cipher.doFinal(text);
		result = Base64.encode(textEncrypted);

		return result;
	}

	public String decodeString(byte[] param) throws IOException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, IntrospectionException {
		byte[] decodedBytes = Base64.decode(param);
		cipher.init(Cipher.DECRYPT_MODE, myDesKey);
		byte[] textDecrypted = cipher.doFinal(decodedBytes);

		return new String(textDecrypted);
	}

	private void test() {
//		String data = "{\r\n" + 
//				"    \"login\": [{\r\n" + 
//				"				\"username\": \"SWAPNIL\",\r\n" + 
//				"				\"password\": \"Swap23$38200\"\r\n" + 
//				"			 }],\r\n" + 
//				"			 \r\n" + 
//				"	\"device\":[{\r\n" + 
//				"				\"uuid\": \"AB:89:FA:80:DA\",\r\n" + 
//				"				\"iemi\": \"234234324234234\"\r\n" + 
//				"			 }] 		 \r\n" + 
//				"    \r\n" + 
//				"}";

		String data = "{\"device\":{\"uuid\":\"10d8ecbfbd59606b\",\"iemi\":\"\",\"version\":\"28\",\"model\":\"OnePlus ONEPLUS A3003\",\"name\":\"OnePlus\"},\"ekyc\":{\"idnumber\":\"784123456789012\",\"cardnumber\":\"12345678\",\"issuedate\":\"02\\/06\\/2010\",\"expirydate\":\"02\\/06\\/2020\",\"fullnamearabic\":\"افسر محمد\",\"fullnameenglish\":\"Afsar\",\"gender\":\"M\",\"nationalitycode\":\"IND\",\"dateofbirth\":\"19830101\",\"placeofbirthenglish\":\"POB\",\"occupationenglish\":\"Programmer\",\"occupationtypeenglish\":\"Computer\",\"companynameenglish\":\"Al Fardan Exchnage\",\"maritalstatuscode\":\"M\",\"passportnumber\":\"A23456\",\"passportcountrycode\":\"IND\",\"passportissuedate\":\"01\\/06\\/1985\",\"passportexpirydate\":\"02\\/06\\/2010\",\"countryofbirth\":\"IND\",\"annual_activity_count\":\"11\",\"annual_activity_amount\":\"1000\"}}";

		try {
			byte[] encoded = encodeBeanToString(data);
			System.out.println("encoded: " + new String(encoded));
			String decoded = decodeString(encoded);
			System.out.println("decoded: " + new String(decoded));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		new EncodeWithSecretKey().test();
	}
}
