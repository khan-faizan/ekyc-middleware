package com.alfardan.ekyc.api.rest;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alfardan.ekyc.EKYCApplication;
import com.alfardan.ekyc.service.CheckDeviceStatusService;
import com.alfardan.ekyc.service.LoginService;
import com.alfardan.ekyc.service.RegistrationService;
import com.alfardan.ekyc.util.EncodeWithSecretKey;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

@RestController
@RequestMapping("${url.api}")
public class RegistrationController {

	@Autowired
	private CheckDeviceStatusService checkDeviceStatusService;

	@Autowired
	private RegistrationService registrationService;

	private static final Logger log = LoggerFactory.getLogger(EKYCApplication.class);
	
	@Value("${afex.UPLOADED_FOLDER}")
	private String UPLOADED_FOLDER;
	
	
	@RequestMapping(value = "/registration", method = RequestMethod.POST, consumes = { "application/json",
			"application/xml",
			"application/x-www-form-urlencoded" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Map<String, String>> validate(@RequestBody byte[] body,
			@RequestHeader MultiValueMap<String, String> headers) throws Exception {
		log.debug("inside login request {}" + new Date());

		
		Map<String, String> deviceStatus = new HashMap<String, String>();
		
		
		try {
			Map<String, Object> map = checkDeviceStatusService.getdeviceStatus(body, headers);
			if (map.get("status").toString().endsWith("N") || map.get("status").toString().endsWith("R")) {
				deviceStatus.put("status", "false");
				deviceStatus.put("statuscode", "001");
				String message = "Device has been registered but not active, Please go to Alfardan office to activate your device";
				deviceStatus.put("message", message);
			} else {
				deviceStatus = registrationService.register(body, headers);
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
	

//	@PostMapping("/upload")
//	public ResponseEntity<Map<String, String>> uplaodImage(MultipartHttpServletRequest request,
//			//@RequestBody byte[] encodedJson
//			@RequestParam(name = "encodedJson") String encodedJson
//			) throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IntrospectionException {
//		
//		Map<String, String> status = new HashMap<String, String>();
//		
//		Map<String, MultipartFile> fileMap = request.getFileMap();
//
//		StringBuffer stBuff = new StringBuffer();
//		EncodeWithSecretKey encodeWithSecretKey = new EncodeWithSecretKey();
//		System.out.println(new Date());
//		System.out.println(new String(encodedJson));
//		System.out.println(encodeWithSecretKey.decodeString(encodedJson.getBytes()));
//		
//		for(Entry<String, MultipartFile> map : fileMap.entrySet()) {
//			System.out.println(map.getKey());
//			System.out.println(map.getValue());
//			
//			log.info("Original Image Byte Size - " + map.getValue().getBytes().length);             
//			log.info("Original Image name - " + map.getValue().getOriginalFilename());              
//			                                                                              
//			if (map.getValue().isEmpty()) {                                                         
//				status.put("message", "Please select a file to upload");                  
//				                                                                          
//				return ResponseEntity.ok().body(status);                                  
//			}                                                                             
//			try {                                                                         
//				// Get the file and save it somewhere                                     
//				byte[] bytes = map.getValue().getBytes();                                           
//				Path path = Paths.get(UPLOADED_FOLDER + map.getValue().getOriginalFilename());      
//				Files.write(path, bytes);     
//				stBuff.append(map.getValue().getOriginalFilename() +", ");
//				status.put("message",                                                     
//						"You successfully uploaded '" + stBuff.toString() + "'");
//			} catch (IOException e) {                                                     
//				e.printStackTrace();                                                      
//			}                                                                             
//		}
//		
//		return ResponseEntity.ok().body(status);
//	}

	

}
