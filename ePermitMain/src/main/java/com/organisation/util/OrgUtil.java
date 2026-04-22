package com.organisation.util;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.register.model.EncryptedRequest;
import com.register.model.RegisterBasicInfo;

public class OrgUtil {

	private static final Logger log = LoggerFactory.getLogger(OrgUtil.class);

	// it returns true if object is not empty
	public static boolean isNeitherNullNorEmpty(Object obj) {
		boolean isNeitherNullNorEmpty = true;

		if (obj == null || "".equals(obj.toString().trim())) {
			isNeitherNullNorEmpty = false;
		}

		return isNeitherNullNorEmpty;
	}

	// Function for generating OTP
	public static String getOtp(int size) {
		StringBuilder generatedToken = new StringBuilder();
		try {
			SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
			for (int i = 0; i < size; i++) {
				generatedToken.append(number.nextInt(9));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return generatedToken.toString();
	}

	// function for generating a random default passsword : in the time of Enrollment
	public static String getAlphaNumericOtp(int size) {
		String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder otp = new StringBuilder();
		try {
			SecureRandom random = SecureRandom.getInstanceStrong(); // More secure than SHA1PRNG
			for (int i = 0; i < size; i++) {
				int index = random.nextInt(CHARACTERS.length());
				otp.append(CHARACTERS.charAt(index));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return otp.toString();
	}

	// Return current time
	public static Timestamp getCurrentTimestamp() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return timestamp;
	}

	// This function return time of current + 10 minutes for activating the otp
	public static Timestamp getOTPExpiryTimestamp() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis() + 10 * 60 * 1000);
		return timestamp;
	}

	private static final String SECRET_KEY = "EpermitStqc12345";
	private static final String AES_MODE = "AES/GCM/NoPadding";
	private static final int GCM_TAG_SIZE = 128; // 128-bit authentication tag

	// This is main decryption function
	public static <T> T decrypt(EncryptedRequest encryptedRequest, Class<T> targetClass) {
		T result = null;
		try {
			if (Objects.isNull(encryptedRequest) || encryptedRequest.getEncryptedData() == null
					|| encryptedRequest.getIv() == null) {
				throw new IllegalArgumentException("Invalid encrypted data or IV.");
			}

			byte[] encryptedBytes = Base64.getDecoder().decode(encryptedRequest.getEncryptedData());
			byte[] iv = Base64.getDecoder().decode(encryptedRequest.getIv());

			SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");
			GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_SIZE, iv);
			Cipher cipher = Cipher.getInstance(AES_MODE);
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec);

			byte[] decrypted = cipher.doFinal(encryptedBytes);
			String decryptedString = new String(decrypted, "UTF-8");

			ObjectMapper mapper = new ObjectMapper();
			result = mapper.readValue(decryptedString, targetClass);

		} catch (Exception e) {
			log.error("Decryption failed", e);
		}
		return result;
	}

	

}
