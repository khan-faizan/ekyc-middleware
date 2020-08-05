package com.alfardan.ekyc.service;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
	@Value("${afex.url}")
	private String url;
	
	@Value("${afex.sharedKey}")
	private String sharedKey;
	
	@Value("${afex.acquirerid}")
	private String acquirerid;
	
	
	private String secureKey;
	private String messageId;

	
	

	public Map<String, String> register(byte[] body,MultiValueMap<String, String> headers) throws JsonSyntaxException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, IntrospectionException {
		log.info("inside login service {}" + new Date());
		Gson gson = new Gson();
		Map<String, Object> map = null;
		String output = null;
		Map<String, String> loginStatus = null;
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

		Client client = Client.create();
//		if (url != null) {
//			url = url + "/register";
//		} else {
//			url = "https://172.31.1.221:9043/ords/afexremit/ekyc/v1/register";
//
//		}
		
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		for(Entry<String, Object> request : map.entrySet()) {
			queryParams.add(request.getKey(), map.get(request.getKey()).toString());
		}
		//queryParams.add("username", map.get("username").toString());
		//queryParams.add("password", map.get("password").toString());
		
		//System.out.println(queryParams.toString());
		WebResource webResource = client.resource( url + "/register");
		System.out.println(ekycInfo);
		ClientResponse response = webResource
				.queryParams(queryParams)
				.header("verification_area", headers.get("verification_area"))
				.header("verification_channel", headers.get("verification_channel"))
				.header("verification_ref_no", headers.get("verification_ref_no"))
				.header("verification_coordinates", headers.get("verification_coordinates"))
				.header("verification_time", headers.get("verification_time"))
				.header("mti", headers.get("mti"))
				.header("verification_user", headers.get("verification_user"))
				.header("Content-Type", "application/json")
				.header("messageid", getMessageId())
				.header("securekey", getSecureKey())
				.header("acquirerid", acquirerid)
				.post(ClientResponse.class);
		
		
		
		output = response.getEntity(String.class);
		System.out.println(output);
		gson = new Gson();
		map = null;
		map = gson.fromJson(output, new TypeToken<Map<String, Object>>() {
		}.getType());
		System.out.println(map.toString());
		if(map != null) {
			loginStatus = new HashMap<String, String>();
			if(map.get("responsecode").toString().equalsIgnoreCase("0000")) {
				loginStatus.put("status", "true");
				loginStatus.put("statuscode", "000");
				loginStatus.put("message",  map.get("responsedata").toString());
				
			}else {
				loginStatus.put("status", "false");
				loginStatus.put("statuscode", map.get("responsecode").toString());
				loginStatus.put("message",  map.get("responsedata").toString());
			}
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return loginStatus;
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

}
