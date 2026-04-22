package com.organisation.service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class PasswordService {
	private static PasswordService instance;
	
	private PasswordService() {	
	}
	
	public static synchronized String encrypt (String plainText) throws Exception {
		MessageDigest md =null;
		try {
			md = MessageDigest.getInstance("SHA-256");  // Neml - SHA1
		} catch (NoSuchAlgorithmException e) {
			throw new Exception(e.getMessage());
		}
		try {
			md.update(plainText.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new Exception(e.getMessage());
		}
		byte[] raw = md.digest();
		String hash = Base64.getEncoder().encodeToString(raw); // Neml - BASE64Encoder
		
		return hash.replace("=", "").trim();
		
	}
	
	public static synchronized PasswordService getInstance() {
		if(instance == null) {
			instance = new PasswordService();
		}
		return instance;
	}
	
}
