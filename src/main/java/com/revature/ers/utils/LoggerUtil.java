package com.revature.ers.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {
	// Get the current line number in Java
	public static int getLineNumber() {
		return new Throwable().getStackTrace()[0].getLineNumber();
	}

	public static Logger log(Object myClass) {
		return LoggerFactory.getLogger(myClass.getClass());
	}
}
