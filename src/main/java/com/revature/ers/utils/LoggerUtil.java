package com.revature.ers.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerUtil {

	public static Logger log(String className) {
		Class<?> myClass = null;
		try {
			myClass = Class.forName(className);
			return LogManager.getLogger(myClass.getClass());
		} catch (ClassNotFoundException e) {
			// Do Nothing
		}
		// Generic logger
		return LogManager.getLogger();
	}
}
