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
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alfardan.ekyc.util.EncodeWithSecretKey;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@Component
public class CheckDeviceStatusService {

	private static final Logger log = LoggerFactory.getLogger(CheckDeviceStatusService.class);

	@Value("${afex.url.devicestatus}")
	private String url;
	
	@Value("${afex.sharedKey}")
	private String sharedKey;
	@Value("${afex.acquirerid}")
	private String acquirerid;
	
	private String secureKey;
	private String messageId;
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public CheckDeviceStatusService() {
	}

	public Map<String, Object> getdeviceStatus(byte[] body, MultiValueMap<String, String> headers)
			throws JsonSyntaxException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
			IOException, IntrospectionException {
		log.debug("inside getdeviceStatus service {}" + new Date());
		Map<String, Object> map = null;
		EncodeWithSecretKey encodeWithSecretKey = new EncodeWithSecretKey();
		//log.info("inside login service {}" + encodeWithSecretKey.decodeString(body));
		Gson gson = new Gson();
		try {
			map = gson.fromJson(encodeWithSecretKey.decodeString(body), new TypeToken<Map<String, Object>>() {
			}.getType());
			String deviceInfo = null;
			for (Entry<String, Object> amp : map.entrySet()) {
				if (amp.getKey().toString().toLowerCase().equalsIgnoreCase("device")) {
					deviceInfo = gson.toJson(amp.getValue());
					break;

				}
			}
			gson = new Gson();
			map = null;
			map = gson.fromJson(deviceInfo, new TypeToken<Map<String, Object>>() {
			}.getType());
			disableSSLVerification ();
			Client client = Client.create();
			MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
			queryParams.add("uuid", map.get("uuid").toString());
			queryParams.add("iemi", map.get("iemi").toString());
			queryParams.add("version", map.get("version").toString());
			queryParams.add("model", map.get("model").toString());
			queryParams.add("name", map.get("name").toString());
			queryParams.add("acquirer_id", acquirerid);
			log.info("Check device status url : "+url);
			log.info("queryParams-->"+queryParams);
			WebResource webResource = client.resource(url);
			ClientResponse response = webResource.queryParams(queryParams)
					.header("messageid", getMessageId())
					.header("securekey", getSecureKey())
					.header("acquirerid", acquirerid)
					.post(ClientResponse.class);
			
			if (response.getStatus() != 200) {
				log.info("Failed : HTTP error code : " + response.getStatus() + "--" + response.getHeaders());
				
			}else {
				String output = response.getEntity(String.class);
				if (output != null) {
					map = null;
					gson = new Gson();
					System.out.println(response.getStatus());
					map = gson.fromJson(output, new TypeToken<Map<String, Object>>() {
					}.getType());

				}
			}
			
		} catch (Exception e) {
			log.error("Error : "+e.getMessage());
			throw e;
		}
		log.info("Device Status : " + map);
		return map;
	}

	public String getSecureKey() {

		try {
			String data = messageId + sharedKey;
			secureKey = new String(
					Base64.getEncoder().encode(MessageDigest.getInstance("SHA-256").digest(data.getBytes())));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
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
	public static void main(String[] args) throws Exception {
		String data = "{\r\n" + "    \"login\": {\r\n" + "				\"username\": \"SWAPNIL\",\r\n"
				+ "				\"password\": \"Swap23$38200\"\r\n" + "			 },\r\n" + "			 \r\n"
				+ "	\"device\":{\r\n" + "				\"uuid\": \"AB:89:FA:80:DA\",\r\n"
				+ "				\"iemi\": \"234234324234234\",\r\n" + "                \"version\": \"10\",\r\n"
				+ "                \"model\": \"A40\",\r\n" + "                \"name\":\"Lenovo\"\r\n" + "\r\n"
				+ "			 }		     \r\n" + "}";

		try {
			EncodeWithSecretKey encodeWithSecretKey = new EncodeWithSecretKey();
			byte[] encoded = encodeWithSecretKey.encodeBeanToString(data);
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

			headers.add("messageid", "");
			headers.add("securekey", "");
			headers.add("acquirerid", "");
			CheckDeviceStatusService checkDeviceStatusService = new CheckDeviceStatusService();
			checkDeviceStatusService.getdeviceStatus(encoded, headers);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
