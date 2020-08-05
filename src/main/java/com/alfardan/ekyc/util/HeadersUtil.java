package com.alfardan.ekyc.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HeadersUtil {

	@Value("${afex.sharedKey}")
	private String sharedKey;

	private static HeadersUtil instance;
	private String secureKey;
	private String messageId;

	private HeadersUtil() throws Exception {
		try {
			Calendar cal = Calendar.getInstance();
			String encryption = null;
			try {
				String data = getMessageId() + sharedKey;
				encryption = new String(
						Base64.getEncoder().encode(MessageDigest.getInstance("SHA-256").digest(data.getBytes())));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.secureKey = encryption;
			this.messageId = cal.getTime().getYear() + "" + cal.getTime().getMonth() + cal.getTime().getDay()
					+ cal.getTime().getHours() + cal.getTime().getMinutes() + cal.getTime().getSeconds()
					+ cal.getTimeInMillis();
		} catch (Exception ex) {
			System.out.println("Failed to create messageID and SecureKey: " + ex.getMessage());
		}
	}

	public String getSecureKey() {
		System.out.println(secureKey);
		return secureKey;
	}

	public String getMessageId() {
		System.out.println(messageId);
		return messageId;
	}

	public static HeadersUtil getInstance() throws Exception {
		if (instance == null) {
			instance = new HeadersUtil();
		} 

		return instance;
	}

}
