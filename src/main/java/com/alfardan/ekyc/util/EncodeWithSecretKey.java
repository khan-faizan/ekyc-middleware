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

	public byte[]  encodeBeanToString(String jsonString)
			throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		byte[] result = null;
		//String jsonString = JSONUtils.toJSON(student);
		byte[] text = jsonString.getBytes();
		cipher.init(Cipher.ENCRYPT_MODE, myDesKey);
		byte[] textEncrypted = cipher.doFinal(text);
		result = Base64.encode(textEncrypted);

		return result;
	}

	public String decodeString(byte [] param) throws IOException, InvalidKeyException, IllegalBlockSizeException,
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
		
		String data = "{\n    \"idnumber\": \"784123456789012\",\n    \"cardnumber\": \"12345678\",\n    \"cardserialnumber\": \"\",\n    \"idtype\": \"\",\n    \"issuedate\": \"02/06/2010\",\n    \"expirydate\": \"02/06/2020\",\n    \"titlearabic\": \"\",\n    \"fullnamearabic\": \"افسر محمد\",\n    \"titleenglish\": \"\",\n    \"fullnameenglish\": \"Afsar Mohamed\",\n    \"gender\": \"M\",\n    \"nationalitycode\": \"IND\",\n    \"dateofbirth\": \"19830101\",\n    \"placeofbirthenglish\": \"Dubai\",\n    \"occupationcode\": \"\",\n    \"occupationarabic\": \"\",\n    \"occupationenglish\": \"Programmer\",\n    \"familyid\": \"\",\n    \"occupationtypearabic\": \"\",\n    \"occupationtypeenglish\": \"Computer\",\n    \"occupationfieldcode\": \"\",\n    \"companynamearabic\": \"\",\n    \"companynameenglish\": \"Al Fardan Exchange\",\n    \"maritalstatuscode\": \"M\",\n    \"husbandidnumber\": \"\",\n    \"sponsortypecode\": \"\",\n    \"sponsorunifiednumber\": \"\",\n    \"sponsorname\": \"\",\n    \"residencytypecode\": \"\",\n    \"residencynumber\": \"\",\n    \"residencyexpirydate\": \"\",\n    \"passportnumber\": \"A23456\",\n    \"passporttypecode\": \"\",\n    \"passportcountrycode\": \"IND\",\n    \"passportcountryarabic\": \"\",\n    \"passportcountryenglish\": \"\",\n    \"passportissuedate\": \"01/06/1985\",\n    \"passportexpirydate\": \"02/06/2010\",\n    \"qualificationlevelcode\": \"\",\n    \"qualificationlevelarabic\": \"\",\n    \"qualificationlevelenglish\": \"\",\n    \"degreedescriptionarabic\": \"\",\n    \"degreedescriptionenglish\": \"\",\n    \"fieldofstudycode\": \"\",\n    \"fieldofstudyarabic\": \"\",\n    \"fieldofstudyenglish\": \"\",\n    \"placeofstudyarabic\": \"\",\n    \"placeofstudyenglish\": \"\",\n    \"dateofgraduation\": \"\",\n    \"motherfullnamearabic\": \"\",\n    \"motherfullnameenglish\": \"\",\n    \"home_addresstypecode\": \"\",\n    \"home_locationcode\": \"\",\n    \"home_emiratescode\": \"\",\n    \"home_emiratesdescarabic\": \"\",\n    \"home_emiratesdescenglish\": \"\",\n    \"home_citycode\": \"\",\n    \"home_citydescarabic\": \"\",\n    \"home_citydescenglish\": \"\",\n    \"home_streetarabic\": \"\",\n    \"home_streetenglish\": \"\",\n    \"home_pobox\": \"\",\n    \"home_areacode\": \"\",\n    \"home_areadescarabic\": \"\",\n    \"home_areadescenglish\": \"\",\n    \"home_buildingnamearabic\": \"\",\n    \"home_buildingnameenglish\": \"\",\n    \"home_mobilephonenumber\": \"\",\n    \"home_email\": \"\",\n    \"home_flatno\": \"\",\n    \"home_residentphonenumber\": \"\",\n    \"work_addresstypecode\": \"\",\n    \"work_locationcode\": \"\",\n    \"work_emiratescode\": \"\",\n    \"work_emiratesdescarabic\": \"\",\n    \"work_emiratesdescenglish\": \"\",\n    \"work_citycode\": \"\",\n    \"work_citydescarabic\": \"\",\n    \"work_citydescenglish\": \"\",\n    \"work_streetarabic\": \"\",\n    \"work_streetenglish\": \"\",\n    \"work_pobox\": \"\",\n    \"work_areacode\": \"\",\n    \"work_areadescarabic\": \"\",\n    \"work_areadescenglish\": \"\",\n    \"work_buildingnamearabic\": \"\",\n    \"work_buildingnameenglish\": \"\",\n    \"work_mobilephonenumber\": \"\",\n    \"work_email\": \"\",\n    \"work_companynamearabic\": \"\",\n    \"work_companynameenglish\": \"\",\n    \"work_landphonenumber\": \"\",\n    \"cardholderphoto\": \"\",\n    \"holdersignatureimage\": \"\",\n    \"countryofbirth\": \"IND\",\n    \"annual_activity_count\": \"10\",\n    \"annual_activity_amount\": \"1000\",\n    \"id_front_image_uri\":\"https://xyc.com:8081/files/ordercompletefiles/600344204_photo.jpg\",\n    \"id_back_image_uri\":\"https://xyc.com:8081/files/ordercompletefiles/600344204_photo.jpg\",\n    \"selfie_image_uri\":\"https://xyc.com:8081/files/ordercompletefiles/600344204_photo.jpg\"\n}";

		try {
			byte[] encoded = encodeBeanToString(data);
			System.out.println("encoded: " + encoded);
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
