package com.alfardan.ekyc.api.rest;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@RestController
@RequestMapping("/api")
public class LDSearchController  {
	
	
	@RequestMapping(value = "/ValidationGatewayService", method = RequestMethod.GET, 
			 consumes = {"application/json", "application/xml","application/x-www-form-urlencoded"},
			 produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> validate(@RequestBody String body,
			@RequestHeader MultiValueMap<String, String> headers, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		
//		byte[] decodedBytes = Base64.getDecoder().decode(body);
//		String decodedString = new String(decodedBytes);
//		System.out.println("body-->" + decodedString);
//		System.out.println("request-->" + request.getQueryString());
		
		headers.forEach((key, value) -> {
			System.out.println(String.format("Header '%s' = %s", key, value.stream().collect(Collectors.joining("|"))));
		});
		
		System.out.println("request.getQueryString()="+request.getMethod());
		
		for(Entry<String, String[]> map : request.   getParameterMap().entrySet()) {
			System.out.println("key="+map.getKey());
			
			for(String str : map.getValue()){
				System.out.println("value="+str);
			}
			
		}
		
//		if ("POST".equalsIgnoreCase(request.getMethod())) 
//		{
//		   String test = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
//		   System.out.println("BODY: "+test);
//		}
//		Enumeration attrs = request.getAttributeNames();
//		
//		while (attrs.hasMoreElements()) {
//			System.out.println("attrs-->" + attrs.nextElement());
//		}
		//restTemplate.exchange("https://172.31.1.221:9043/ValidationGatewayService", method, requestEntity, responseType)
		return ResponseEntity.ok().body(new String(ValidationGatewayService(headers,body)));
	}

	public static String ValidationGatewayService(MultiValueMap<String, String> headers, String body) {
		String responseJson = null;
		try {
			Client client = Client.create();

			WebResource webResource = client.resource("https://172.31.1.221:9043/ValidationGatewayService"
					//+"?toolkit-request-id="+headers.get("toolkit-request-id")
					//+"&toolkit-correlation-id="+headers.get("toolkit-correlation-id")
					);
			//MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
			
			
			ClientResponse response = webResource
					.header("host","172.31.3.134:8090")
					.header("content-type","application/x-www-form-urlencoded")
					.header("toolkit-request-id",headers.get("toolkit-request-id"))
					.header("toolkit-correlation-id", headers.get("toolkit-correlation-id"))
					.header("alias" ,headers.get("alias"))
					.header("user-agent","Emirates ID Card Toolkit/1.4.5/ALFARDANEXCHANGE")
					.get(ClientResponse.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException(
						"Failed : HTTP error code : " + response.getStatus() + "--" + response.getHeaders());
			}

			String output = response.
					getEntity(String.class);

			System.out.println("Output from Server .... \n");
			System.out.println("status=" + response.getStatus() + "-headers-" + response.getHeaders()
			+"map="+response.getProperties());
			System.out.println(output);

			responseJson = output;

		} catch (Exception e) {

			e.printStackTrace();

		}
		return responseJson;
	}
	@RequestMapping("/secured")
	public String secured(){
		System.out.println("Inside secured()");
		return "Hello user !!! : " + new Date();
	}
	static {
		disableSslVerification();
	}

	private static void disableSslVerification() {
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}
}
