package com.revature.ers.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.google.gson.Gson;
import com.revature.ers.daos.ERSReimbursementStatusDAO;
import com.revature.ers.daos.ERSReimbursementTypeDAO;
import com.revature.ers.daos.ERSUserRolesDAO;
import com.revature.ers.daos.ERSUsersDAO;
import com.revature.ers.models.ERSReimbursementStatus;
import com.revature.ers.models.ERSReimbursementType;
import com.revature.ers.models.ERSUserRoles;
import com.revature.ers.models.ERSUsers;
import com.revature.ers.models.InitialSetup;
import com.revature.ers.models.Responses;
import com.revature.ers.models.RunOnce;
import com.revature.ers.utils.ConnectionUtil;
import com.revature.ers.utils.Path;

import io.javalin.http.Handler;

public class SetupController {

	// private static Logger logger = LoggerFactory.getLogger(SetupController.class);

	public static Handler serveSetupPage = ctx -> {
		ctx.render(Path.Template.INITIAL_SETUP);
	};

	public static Handler doInitialSetup = ctx -> {

		Gson gson = new Gson();
		File configFile = null;
		FileReader reader = null;
		Properties props = null;

		String ERS_DB_RUNONCE = null;

		try {
			configFile = new File("./configs/config.properties");
			reader = new FileReader(configFile);
			props = new Properties();
			props.load(reader);

			ERS_DB_RUNONCE = props.getProperty("ERS_DB_RUNONCE");

			reader.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		if (!ERS_DB_RUNONCE.equals("true")) {

			InitialSetup is = gson.fromJson(ctx.body(), InitialSetup.class);

			String dbUrl = "jdbc:postgresql://" + is.getPostgresDBUrl() + ":" + is.getPostgresDBPort() + "/" + is.getPostgresDBName()
					+ "?currentSchema=" + is.getPostgresDBSchema();
			// System.out.println(dbUrl);

			boolean testConnection = ConnectionUtil.testConnection(dbUrl, is.getPostgresUsername(), is.getPostgresPassword());

			if (testConnection) {

				// create 2 new roles and 1 financial manager
				ERSUserRoles newRole1 = new ERSUserRoles("manager");
				ERSUserRoles newRole2 = new ERSUserRoles("employee");
				ERSUsers newUser = new ERSUsers(is.getErsUsername(), is.getErsPassword(), is.getErsFirstname(), is.getErsLastname(),
						is.getErsEmail(), 1);
				ERSReimbursementType newRtype1 = new ERSReimbursementType("lodging");
				ERSReimbursementType newRtype2 = new ERSReimbursementType("travel");
				ERSReimbursementType newRtype3 = new ERSReimbursementType("food");
				ERSReimbursementType newRtype4 = new ERSReimbursementType("other");
				ERSReimbursementStatus newRStatus1 = new ERSReimbursementStatus("pending");
				ERSReimbursementStatus newRStatus2 = new ERSReimbursementStatus("approved");
				ERSReimbursementStatus newRStatus3 = new ERSReimbursementStatus("denied");

				ERSUserRoles createRole1 = new ERSUserRolesDAO().createRole(newRole1);
				ERSUserRoles createRole2 = new ERSUserRolesDAO().createRole(newRole2);
				ERSUsers createUser = new ERSUsersDAO().createUser(newUser);
				ERSReimbursementType createRtype1 = new ERSReimbursementTypeDAO().createReimbursementType(newRtype1);
				ERSReimbursementType createRtype2 = new ERSReimbursementTypeDAO().createReimbursementType(newRtype2);
				ERSReimbursementType createRtype3 = new ERSReimbursementTypeDAO().createReimbursementType(newRtype3);
				ERSReimbursementType createRtype4 = new ERSReimbursementTypeDAO().createReimbursementType(newRtype4);
				ERSReimbursementStatus createRstatus1 = new ERSReimbursementStatusDAO().createReimbursementStatus(newRStatus1);
				ERSReimbursementStatus createRstatus2 = new ERSReimbursementStatusDAO().createReimbursementStatus(newRStatus2);
				ERSReimbursementStatus createRstatus3 = new ERSReimbursementStatusDAO().createReimbursementStatus(newRStatus3);

				if (createRole1 != null & createRole2 != null & createUser != null & createRtype1 != null & createRtype2 != null
						& createRtype3 != null & createRtype4 != null & createRstatus1 != null & createRstatus1 != null
						& createRstatus2 != null & createRstatus3 != null) {
					HashMap<String, ArrayList<?>> setupList = new HashMap<String, ArrayList<?>>();

					ArrayList<ERSUserRoles> roleList = new ArrayList<ERSUserRoles>();
					ArrayList<ERSUsers> userList = new ArrayList<ERSUsers>();
					ArrayList<ERSReimbursementType> ertList = new ArrayList<ERSReimbursementType>();
					ArrayList<ERSReimbursementStatus> ersList = new ArrayList<ERSReimbursementStatus>();

					roleList.add(createRole1);
					roleList.add(createRole2);
					userList.add(createUser);
					ertList.add(createRtype1);
					ertList.add(createRtype2);
					ertList.add(createRtype3);
					ertList.add(createRtype4);
					ersList.add(createRstatus1);
					ersList.add(createRstatus2);
					ersList.add(createRstatus3);

					setupList.put("roles", roleList);
					setupList.put("user", userList);
					setupList.put("reimbursementTypes", ertList);
					setupList.put("reimbursementStatus", ersList);

					try {
						reader = new FileReader(configFile);
						props = new Properties();
						props.load(reader);

						props.setProperty("ERS_DB_URL", is.getPostgresDBUrl());
						props.setProperty("ERS_DB_PORT", Integer.toString(is.getPostgresDBPort()));
						props.setProperty("ERS_DB_NAME", is.getPostgresDBName());
						props.setProperty("ERS_DB_SCHEMA", is.getPostgresDBSchema());
						props.setProperty("ERS_DB_USERNAME", is.getPostgresUsername());
						props.setProperty("ERS_DB_PASSWORD", is.getPostgresPassword());
						props.setProperty("ERS_DB_RUNONCE", "true");
						props.store(new FileOutputStream(configFile), null);

						reader.close();
					} catch (FileNotFoundException ex) {
						ex.printStackTrace();
					} catch (IOException ex) {
						ex.printStackTrace();
					}

					RunOnce.setRunonce(true);

					Responses rp = new Responses(200, "Setup Success", true, setupList);
					ctx.status(rp.getStatusCode()).json(gson.toJson(rp));
				} else {
					Responses rp = new Responses(400, "Setup Failed. One of the insert SQLs may have failed.", false, null);
					ctx.status(rp.getStatusCode()).json(gson.toJson(rp));
				}
			} else {
				Responses rp = new Responses(400, "Connection failed", false, null);
				ctx.status(rp.getStatusCode()).json(gson.toJson(rp));
			}
		} else {
			Responses rp = new Responses(400, "This can only be executed once during initial setup", false, null);
			ctx.status(rp.getStatusCode()).json(gson.toJson(rp));
		}

	};

	/*
	 * private static void getAllFiles(File curDir) {
	 * 
	 * File[] filesList = curDir.listFiles(); for (File f : filesList) { if (f.isDirectory()) System.out.println("(Directory)" +
	 * f.getName()); if (f.isFile()) { System.out.println(f.getName()); } }
	 * 
	 * }
	 */

	public static boolean isRun_once() {
		// File curDir = new File(".");
		// getAllFiles(curDir);

		String ERS_DB_RUNONCE = null;

		try {
			File configFile = new File("./configs/config.properties");
			FileReader reader = new FileReader(configFile);
			Properties props = new Properties();
			props.load(reader);

			ERS_DB_RUNONCE = props.getProperty("ERS_DB_RUNONCE");

			reader.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		if (ERS_DB_RUNONCE.equals("true")) {
			return true;
		} else {
			return false;
		}

	}
}
