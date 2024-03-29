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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import com.alfardan.ekyc.util.EncodeWithSecretKey;
import com.alfardan.ekyc.util.HeadersUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@Component
public class LoginService {
	private static final Logger log = LoggerFactory.getLogger(LoginService.class);
	@Value("${afex.url.login}")
	private String url;

	@Value("${afex.sharedKey}")
	private String sharedKey;

//	@Value("${afex.acquirerid}")
//	private String acquirerid;

	private String secureKey;
	private String messageId;

	public Map<String, String> login(byte[] body, MultiValueMap<String, String> headers) throws JsonSyntaxException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, IntrospectionException {
		log.info("inside login service {}" + new Date());
		Gson gson = new Gson();
		Map<String, Object> map = null;
		String output = null;
		Map<String, String> loginStatus = null;

		try {
			EncodeWithSecretKey encodeWithSecretKey = new EncodeWithSecretKey();

			map = gson.fromJson(encodeWithSecretKey.decodeString(body), new TypeToken<Map<String, Object>>() {
			}.getType());
			String loginInfo = null;
			for (Entry<String, Object> amp : map.entrySet()) {
				if (amp.getKey().toString().toLowerCase().equalsIgnoreCase("login")) {
					loginInfo = gson.toJson(amp.getValue());
					break;

				}
			}
			map = gson.fromJson(loginInfo, new TypeToken<Map<String, Object>>() {
			}.getType());
			disableSSLVerification();
			Client client = Client.create();
			MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();

			queryParams.add("username", map.get("username").toString());
			queryParams.add("password", map.get("password").toString());
			log.info("Login url : "+url);
			WebResource webResource = client.resource(url);

			ClientResponse response = webResource.queryParams(queryParams).header("content-Type", "application/json")
					.header("messageid", getMessageId()).header("securekey", getSecureKey())
					.header("acquirerid", headers.getFirst("acquirerid")).get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException(
						"Failed : HTTP error code : " + response.getStatus() + "--" + response.getHeaders());
			}

			output = response.getEntity(String.class);
			gson = new Gson();
			map = null;
			map = gson.fromJson(output, new TypeToken<Map<String, Object>>() {
			}.getType());
			// System.out.println(map.toString());
			if (map != null) {
				loginStatus = new HashMap<String, String>();
				if (map.get("responsecode").toString().equalsIgnoreCase("0000")) {
					loginStatus.put("status", "true");
					loginStatus.put("statuscode", "000");
					loginStatus.put("message", map.get("responsedata").toString());

				} else {
					loginStatus.put("status", "false");
					loginStatus.put("statuscode", map.get("responsecode").toString());
					loginStatus.put("message", map.get("responsedata").toString());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

		return loginStatus;
	}

//	public Map<String, String> logout(byte[] body, MultiValueMap<String, String> headers) throws JsonSyntaxException,
//			InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, IntrospectionException {
//		log.info("inside login service {}" + new Date());
//		Gson gson = new Gson();
//		Map<String, Object> map = null;
//		String output = null;
//		Map<String, String> logoutStatus = null;
//		try {
//			EncodeWithSecretKey encodeWithSecretKey = new EncodeWithSecretKey();
//			map = gson.fromJson(encodeWithSecretKey.decodeString(body), new TypeToken<Map<String, Object>>() {
//			}.getType());
//			String deviceInfo = null;
//			for (Entry<String, Object> amp : map.entrySet()) {
//				if (amp.getKey().toString().toLowerCase().equalsIgnoreCase("device")) {
//					deviceInfo = gson.toJson(amp.getValue());
//					break;
//
//				}
//			}
//			gson = new Gson();
//			map = null;
//			map = gson.fromJson(deviceInfo, new TypeToken<Map<String, Object>>() {
//			}.getType());
//			disableSSLVerification ();
//			Client client = Client.create();
//			MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
//			queryParams.add("uuid", map.get("uuid").toString());
//			queryParams.add("iemi", map.get("iemi").toString());
//			queryParams.add("version", map.get("version").toString());
//			queryParams.add("model", map.get("model").toString());
//			queryParams.add("name", map.get("name").toString());
//			queryParams.add("acquirer_id", headers.getFirst("acquirerid"));
//			log.info(url+ "/tabletstatus");
//			log.info("queryParams-->"+queryParams);
//			WebResource webResource = client.resource(url + "/tabletstatus");
//			ClientResponse response = webResource.queryParams(queryParams)
//					.header("messageid", getMessageId())
//					.header("securekey", getSecureKey())
//					.header("acquirerid", headers.getFirst("acquirerid"))
//					.post(ClientResponse.class);
//			
//			if (response.getStatus() != 200) {
//				log.info("Failed : HTTP error code : " + response.getStatus() + "--" + response.getHeaders());
//				
//			}else {
//				output = response.getEntity(String.class);
//				if (output != null) {
//					map = null;
//					gson = new Gson();
//					System.out.println(response.getStatus());
//					map = gson.fromJson(output, new TypeToken<Map<String, Object>>() {
//					}.getType());
//
//				}
//			}
//			
//		} catch (Exception e) {
//			log.error("Error : "+e.getMessage());
//			throw e;
//		}
//
//		return logoutStatus;
//	}

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

	public static void disableSSLVerification() {

		TrustManager[] trustAllCerts = new TrustManager[] { new X509ExtendedTrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {

			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {

			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {

			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {

			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}

		} };

		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

}
