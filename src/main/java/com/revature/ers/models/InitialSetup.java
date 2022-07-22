package com.revature.ers.models;

public class InitialSetup {
	private String postgresDBUrl;
	private int postgresDBPort;
	private String postgresDBName;
	private String postgresDBSchema;
	private String postgresUsername;
	private String postgresPassword;
	private String ersUsername;
	private String ersPassword;
	private String ersFirstname;
	private String ersLastname;
	private String ersEmail;

	public InitialSetup(String postgresDBUrl, int postgresDBPort, String postgresDBName, String postgresDBSchema,
			String postgresUsername, String postgresPassword, String ersUsername, String ersPassword,
			String ersFirstname, String ersLastname, String ersEmail) {
		super();
		this.postgresDBUrl = postgresDBUrl;
		this.postgresDBPort = postgresDBPort;
		this.postgresDBName = postgresDBName;
		this.postgresDBSchema = postgresDBSchema;
		this.postgresUsername = postgresUsername;
		this.postgresPassword = postgresPassword;
		this.ersUsername = ersUsername;
		this.ersPassword = ersPassword;
		this.ersFirstname = ersFirstname;
		this.ersLastname = ersLastname;
		this.ersEmail = ersEmail;
	}

	public String getPostgresDBUrl() {
		return postgresDBUrl;
	}

	public void setPostgresDBUrl(String postgresDBUrl) {
		this.postgresDBUrl = postgresDBUrl;
	}

	public int getPostgresDBPort() {
		return postgresDBPort;
	}

	public void setPostgresDBPort(int postgresDBPort) {
		this.postgresDBPort = postgresDBPort;
	}

	public String getPostgresDBName() {
		return postgresDBName;
	}

	public void setPostgresDBName(String postgresDBName) {
		this.postgresDBName = postgresDBName;
	}

	public String getPostgresDBSchema() {
		return postgresDBSchema;
	}

	public void setPostgresDBSchema(String postgresDBSchema) {
		this.postgresDBSchema = postgresDBSchema;
	}

	public String getPostgresUsername() {
		return postgresUsername;
	}

	public void setPostgresUsername(String postgresUsername) {
		this.postgresUsername = postgresUsername;
	}

	public String getPostgresPassword() {
		return postgresPassword;
	}

	public void setPostgresPassword(String postgresPassword) {
		this.postgresPassword = postgresPassword;
	}

	public String getErsUsername() {
		return ersUsername;
	}

	public void setErsUsername(String ersUsername) {
		this.ersUsername = ersUsername;
	}

	public String getErsPassword() {
		return ersPassword;
	}

	public void setErsPassword(String ersPassword) {
		this.ersPassword = ersPassword;
	}

	public String getErsFirstname() {
		return ersFirstname;
	}

	public void setErsFirstname(String ersFirstname) {
		this.ersFirstname = ersFirstname;
	}

	public String getErsLastname() {
		return ersLastname;
	}

	public void setErsLastname(String ersLastname) {
		this.ersLastname = ersLastname;
	}

	public String getErsEmail() {
		return ersEmail;
	}

	public void setErsEmail(String ersEmail) {
		this.ersEmail = ersEmail;
	}

}
