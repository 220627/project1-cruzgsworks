package com.revature.ers.utils;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import com.revature.ers.daos.ERSUsersDAO;
import com.revature.ers.models.ERSUsers;

public class AuthUtil {

	private static String internalSeed = "REVATURE2022";

	public static String doEncrypt(String subject) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(AuthUtil.internalSeed);
		return encryptor.encrypt(subject);
	}

	public static boolean comparePasswords(String input, String storedPass) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(internalSeed);
		if (encryptor.decrypt(storedPass).equals(input)) {
			return true;
		}
		return false;
	}

	public static ERSUsers verifyCookie(String authenticationStr) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(internalSeed);
		if (StringUtils.isNoneEmpty(authenticationStr)) {
			String[] userPass = encryptor.decrypt(authenticationStr).split(":");
			// retrieve user records
			ERSUsers getUser = new ERSUsersDAO().getUserByUserName(userPass[0].toLowerCase());
			if (getUser != null) {
				// Compare passwords
				boolean checkStr = AuthUtil.comparePasswords(userPass[1],
						getUser.getErs_password());

				if (checkStr) {
					return getUser;
				}
			}
		}

		return null;
	}
}
