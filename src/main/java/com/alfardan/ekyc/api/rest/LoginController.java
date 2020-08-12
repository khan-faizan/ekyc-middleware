package com.alfardan.ekyc.api.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.alfardan.ekyc.EKYCApplication;
import com.alfardan.ekyc.service.CheckDeviceStatusService;
import com.alfardan.ekyc.service.LoginService;
import com.alfardan.ekyc.util.EncodeWithSecretKey;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

@RestController
@RequestMapping("${url.api}")
public class LoginController {

	@Autowired
	private CheckDeviceStatusService checkDeviceStatusService;

	@Autowired
	private LoginService loginService;

	private static final Logger log = LoggerFactory.getLogger(EKYCApplication.class);

	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = { "application/json", "application/xml",
			"application/x-www-form-urlencoded" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Map<String, String>> validate(@RequestBody byte[] body,@RequestHeader MultiValueMap<String, String> headers) throws Exception {
		log.debug("inside login request {}" + new Date());

		
		Map<String, String> deviceStatus = new HashMap<String, String>();
		try {
			Map<String, Object> map = checkDeviceStatusService.getdeviceStatus(body,headers);
			if (map.get("status").toString().endsWith("N") || map.get("status").toString().endsWith("R")) {
				deviceStatus.put("status", "false");
				deviceStatus.put("statuscode", "001");
				String message = "Device has been registered but not active, Please go to Alfardan office to activate your device";
				deviceStatus.put("message", message);
			} else {
				deviceStatus = loginService.login(body,headers);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			deviceStatus.put("status", "false");
			deviceStatus.put("statuscode", "002");
			deviceStatus.put("message", "Something went wrong at server side");
			return ResponseEntity.ok().body(deviceStatus);
		}
		return ResponseEntity.ok().body(deviceStatus);
	}

//	@RequestMapping(value = "/encrypt", method = RequestMethod.POST, consumes = { "application/json", "application/xml",
//			"application/x-www-form-urlencoded" }, produces = { "application/json", "application/xml" })
//	@ResponseStatus(HttpStatus.CREATED)
//	public ResponseEntity<byte[]> encrypt(@RequestBody String body,
//			@RequestHeader MultiValueMap<String, String> headers, HttpServletRequest request,
//			HttpServletResponse response) throws Exception {
//
//		headers.forEach((key, value) -> {
//			System.out.println(String.format("Header '%s' = %s", key, value.stream().collect(Collectors.joining("|"))));
//		});
//		;
//		System.out.println("body:" + body);
//		EncodeWithSecretKey encodeWithSecretKey = new EncodeWithSecretKey();
//		byte[] encrypt = encodeWithSecretKey.encodeBeanToString(body);
//
//		return ResponseEntity.ok().body(encrypt);
//	}
//
//	@RequestMapping(value = "/decrypt", method = RequestMethod.POST, 
//			 consumes = {"application/json", "application/xml","application/x-www-form-urlencoded"},
//			 produces = {"application/json", "application/xml"})
//	@ResponseStatus(HttpStatus.CREATED)
//	public ResponseEntity<String> decrypt(@RequestBody byte[] body,
//			@RequestHeader MultiValueMap<String, String> headers) throws Exception {
//		
//		headers.forEach((key, value) -> {
//			System.out.println(String.format("Header '%s' = %s", key, value.stream().collect(Collectors.joining("|"))));
//		});
//		
//		System.out.println("body:"+body);
//		EncodeWithSecretKey encodeWithSecretKey = new EncodeWithSecretKey();
//		
//		//System.out.println(encodeWithSecretKey.decodeString(body));
//		
//		return ResponseEntity.ok().body(new String(encodeWithSecretKey.decodeString(body)));
//	}

}
