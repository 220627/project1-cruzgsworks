package com.revature.ers.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.revature.ers.Launcher;

//This Class is where we manage and establish our database connection
public class ConnectionUtil {

	private static String url;
	private static String username;
	private static String password;

	public static String getUrl() {
		return url;
	}

	public static void setUrl(String url) {
		ConnectionUtil.url = url;
	}

	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		ConnectionUtil.username = username;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		ConnectionUtil.password = password;
	}

	// This method will eventually return an object of type Connection, which we'll
	// use to connect to our database
	public static Connection getConnection() throws SQLException {

		// For compatibility with other technologies/frameworks, we'll need to register
		// our PostgreSQL driver
		// This process makes the application aware of what Driver class we're using
		try {
			Class.forName("org.postgresql.Driver"); // searching for the postgres driver, which we have as a dependency
		} catch (ClassNotFoundException ex) {
			// This tells in the console us what went wrong
			ex.printStackTrace();
		}

		Launcher launcher = new Launcher();

		// initialize DB connection
		try {
			ClassLoader classLoader = launcher.getClass().getClassLoader();
			URL path = classLoader.getResource("configs/config.properties");
			File configFile = new File(path.toURI());
			FileReader reader = new FileReader(configFile);
			Properties props = new Properties();

			props.load(reader);

			String dbUrl = "jdbc:postgresql://" + props.getProperty("ERS_DB_URL") + ":"
					+ props.getProperty("ERS_DB_PORT") + "/"
					+ props.getProperty("ERS_DB_NAME") + "?currentSchema=" + props.getProperty("ERS_DB_SCHEMA");

			url = dbUrl;
			username = props.getProperty("ERS_DB_USERNAME");
			password = props.getProperty("ERS_DB_PASSWORD");

			reader.close();
		} catch (FileNotFoundException ex) {

		} catch (IOException ex) {

		} catch (URISyntaxException ex) {

		}

		return DriverManager.getConnection(url, username, password);

	}

	public static boolean testConnection() {
		if (StringUtils.isNotEmpty(url) & StringUtils.isNotEmpty(username) & StringUtils.isNotEmpty(password)) {
			try (Connection conn = getConnection()) {
				if (conn != null) {
					return true;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();

			}
		} else {
			LoggerUtil.log(ConnectionUtil.class)
					.error("Required connection data is empty. Make sure config.properties file is not empty");
		}
		return false;
	}

}
