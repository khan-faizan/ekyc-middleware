package com.alfardan.ekyc.service;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import com.alfardan.ekyc.util.EncodeWithSecretKey;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@Component
public class RegistrationService {
	private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);
	@Value("${afex.url.register}")
	private String url;
	
	@Value("${afex.sharedKey}")
	private String sharedKey;
	
	@Value("${afex.acquirerid}")
	private String acquirerid;
	
	
	private String secureKey;
	private String messageId;

	
	

	public Map<String, String> register(byte[] body,MultiValueMap<String, String> headers) throws JsonSyntaxException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, IntrospectionException {
		log.info("inside register service {}" + new Date());
		Gson gson = new Gson();
		Map<String, Object> map = null;
		String output = null;
		Map<String, String> registerationStatus = null;
		try {
		EncodeWithSecretKey encodeWithSecretKey = new EncodeWithSecretKey();
		map = gson.fromJson(encodeWithSecretKey.decodeString(body),
				new TypeToken<Map<String, Object>>() {
				}.getType());
		String ekycInfo = null;
		for (Entry<String, Object> amp : map.entrySet()) {
			if (amp.getKey().toString().toLowerCase().equalsIgnoreCase("ekyc")) {
				ekycInfo = gson.toJson(amp.getValue());
				break;

			}
		}
		
		map = gson.fromJson(ekycInfo, new TypeToken<Map<String, Object>>() {
		}.getType());
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		String documentbinary ="";
		for(Entry<String, Object> o : map.entrySet()) {
			
			if (o.getKey().toString().toLowerCase().equalsIgnoreCase("documentbinary")) {
				documentbinary = gson.toJson(o.getValue());

			}
			else {
				if(o.getKey().toString().toLowerCase().equalsIgnoreCase("fullnamearabic")) {
					queryParams.add(o.getKey().toString(),  headers.getFirst("arabicname"));
				}else {
					queryParams.add(o.getKey().toString(), o.getValue().toString());
				}
			}
		}
		
		//log.info("queryParams-->"+queryParams);
		//log.info("documentbinary-->"+documentbinary);
		disableSSLVerification ();
		Client client = Client.create();

		log.info("Registration url : "+url);
		
		WebResource webResource = client.resource( url);
		ClientResponse response = webResource
				.queryParams(queryParams)
				//.type(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.header("verification_area", headers.getFirst("verification_area"))
				.header("verification_channel", headers.getFirst("verification_channel"))
				.header("verification_ref_no", headers.getFirst("verification_ref_no"))
				.header("verification_coordinates", headers.getFirst("verification_coordinates"))
				.header("verification_time", headers.getFirst("verification_time"))
				.header("mti", headers.getFirst("mti"))
				.header("verification_user", headers.getFirst("verification_user"))
				.header("Content-Type", "application/json")
				.header("messageid", getMessageId())
				.header("securekey", getSecureKey())
				//.header("acquirerid", acquirerid)
				.header("acquirerid", headers.getFirst("acquirerid"))
				.post(ClientResponse.class,documentbinary);
		

		
		output = response.getEntity(String.class);
		
		log.info("output-->"+output);
		gson = new Gson();
		map = null;
		map = gson.fromJson(output, new TypeToken<Map<String, Object>>() {
		}.getType());
		if(map != null) {
			registerationStatus = new HashMap<String, String>();
			if(map.get("responsecode").toString().equalsIgnoreCase("0000")) {
				registerationStatus.put("status", "true");
				registerationStatus.put("statuscode", "000");
				registerationStatus.put("message",  map.get("responsedata").toString());
				
			}else {
				registerationStatus.put("status", "false");
				registerationStatus.put("statuscode", map.get("responsecode").toString());
				if(map.get("reasons") !=null) {
					registerationStatus.put("message",  map.get("responsedata").toString()+", "+map.get("reasons"));
				}else {
					registerationStatus.put("message",  map.get("responsedata").toString());
				}
				
			}
		}
		}catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		
		return registerationStatus;
	}

	public String getSecureKey() {

		try {
			String data = messageId + sharedKey;
			secureKey = new String(
					Base64.getEncoder().encode(MessageDigest.getInstance("SHA-256").digest(data.getBytes())));
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return secureKey;
	}

	public String getMessageId() {

		Calendar cal = Calendar.getInstance();
		messageId = cal.getTime().getYear() + "" + cal.getTime().getMonth() + cal.getTime().getDay()
				+ cal.getTime().getHours() + cal.getTime().getMinutes() + cal.getTime().getSeconds()
				+ cal.getTimeInMillis();
		 
		return messageId;
	}
	
	
	public static void disableSSLVerification () {

	      TrustManager [] trustAllCerts = new TrustManager [] {new X509ExtendedTrustManager () {
	         @Override
	         public void checkClientTrusted (X509Certificate [] chain, String authType, Socket socket) {

	         }

	         @Override
	         public void checkServerTrusted (X509Certificate [] chain, String authType, Socket socket) {

	         }

	         @Override
	         public void checkClientTrusted (X509Certificate [] chain, String authType, SSLEngine engine) {

	         }

	         @Override
	         public void checkServerTrusted (X509Certificate [] chain, String authType, SSLEngine engine) {

	         }

	         @Override
	         public java.security.cert.X509Certificate [] getAcceptedIssuers () {
	            return null;
	         }

	         @Override
	         public void checkClientTrusted (X509Certificate [] certs, String authType) {
	         }

	         @Override
	         public void checkServerTrusted (X509Certificate [] certs, String authType) {
	         }

	      }};

	      SSLContext sc = null;
	      try {
	         sc = SSLContext.getInstance ("SSL");
	         sc.init (null, trustAllCerts, new java.security.SecureRandom ());
	      } catch (KeyManagementException | NoSuchAlgorithmException e) {
	         e.printStackTrace ();
	      }
	      HttpsURLConnection.setDefaultSSLSocketFactory (sc.getSocketFactory ());
	   }

}
