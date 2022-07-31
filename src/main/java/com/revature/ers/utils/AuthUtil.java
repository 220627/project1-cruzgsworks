package com.revature.ers.utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class AuthUtil {

	private static String internalSeed = "REVATURE2022";

	public static String doEncrypt(String subject) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(AuthUtil.internalSeed);
		return encryptor.encrypt(subject);
	}
	
	public static String doDecrypt(String subject) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(AuthUtil.internalSeed);
		return encryptor.decrypt(subject);
	}

	public static boolean comparePasswords(String input, String storedPass) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(internalSeed);
		if (encryptor.decrypt(storedPass).equals(input)) {
			return true;
		}
		return false;
	}
}
