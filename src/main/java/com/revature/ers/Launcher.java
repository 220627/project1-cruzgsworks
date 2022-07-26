package com.revature.ers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.ers.routes.Routes;

public class Launcher {
	
	public static Logger log = LogManager.getLogger();

	public static void main(String[] args) {
		
		log.info("Server starting");

		Routes route = new Routes();
		route.initRoute();
		
	}

}
