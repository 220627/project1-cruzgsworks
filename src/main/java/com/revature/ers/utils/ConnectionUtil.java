package com.revature.ers.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

//This Class is where we manage and establish our database connection
public class ConnectionUtil {

	private static String url = null;
	private static String username = null;
	private static String password = null;

	// This method will eventually return an object of type Connection, which we'll use to connect to our database
	public static Connection getConnection() throws SQLException {

		// For compatibility with other technologies/frameworks, we'll need to register our PostgreSQL driver
		// This process makes the application aware of what Driver class we're using
		try {
			Class.forName("org.postgresql.Driver");
			// searching for the postgres driver, which we have as a dependency

			if (url == null | username == null | password == null) {
				try {
					File configFile = new File("./configs/config.properties");
					FileReader reader = new FileReader(configFile);
					Properties props = new Properties();
					props.load(reader);

					String dbUrl = "jdbc:postgresql://" + props.getProperty("ERS_DB_URL") + ":" + props.getProperty("ERS_DB_PORT") + "/" + props.getProperty("ERS_DB_NAME")
					+ "?currentSchema=" + props.getProperty("ERS_DB_SCHEMA");
					
					url = dbUrl;
					username = props.getProperty("ERS_DB_USERNAME");
					password = props.getProperty("ERS_DB_PASSWORD");

					reader.close();
				} catch (FileNotFoundException ex) {
					LoggerUtil.log("ConnectionUtil").error("Could not find configs/config.properties file");
				} catch (IOException ex) {
					LoggerUtil.log("ConnectionUtil").error(ex.getMessage());
				}
			}

		} catch (ClassNotFoundException ex) {
			// This tells in the console us what went wrong
			ex.printStackTrace();
		}

		return DriverManager.getConnection(url, username, password);

	}

	public static boolean testConnection(String pUrl, String pUsername, String pPassword) {
		if (StringUtils.isNotEmpty(pUrl) & StringUtils.isNotEmpty(pUsername) & StringUtils.isNotEmpty(pPassword)) {

			url = pUrl;
			username = pUsername;
			password = pPassword;

			try (Connection conn = getConnection()) {
				if (conn != null) {
					LoggerUtil.log("ConnectionUtil").info("DB Connection successful");
					return true;
				}
			} catch (SQLException ex) {
				LoggerUtil.log("ConnectionUtil").info("Connection failed. Check credentials in initial setup page.");
			}
		} 
		return false;
	}

}
