package com.revature.ers.controllers;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.revature.ers.daos.ERSUserRolesDAO;
import com.revature.ers.daos.ERSUsersDAO;
import com.revature.ers.models.ERSUserRoles;
import com.revature.ers.models.ERSUsers;
import com.revature.ers.models.Responses;
import com.revature.ers.models.Roles;
import com.revature.ers.utils.AuthUtil;
import com.revature.ers.utils.Path;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class AuthController {
	
	public static Logger log = LogManager.getLogger();

	public static final Handler serveNewManager = ctx -> {
		ctx.render(Path.Template.REGISTER_MANAGER);
	};

	public static Handler createManager = ctx -> {
		// Post method

		// object of gson class
		Gson gson = new Gson();

		// Get request body
		ERSUsers requestData = gson.fromJson(ctx.body(), ERSUsers.class);
		ERSUserRoles eur = new ERSUserRolesDAO().getRoleByUseRole("manager");

		// object of responses class
		Responses response;

		ERSUsers createUser = null;

		if (eur != null) {
			requestData.setUser_role_id(eur.getEsr_user_role_id());

			// create new user
			createUser = new ERSUsersDAO().createUser(requestData);
		}

		if (createUser != null) {
			response = new Responses(200, "Created new manager", true, createUser);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		} else {
			response = new Responses(400, "Failed to create new user", false, null);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		}
	};

	public static Handler doRegister = ctx -> {
		// Post method

		// object of gson class
		Gson gson = new Gson();

		// Get request body
		ERSUsers requestData = gson.fromJson(ctx.body(), ERSUsers.class);
		ERSUserRoles eur = new ERSUserRolesDAO().getRoleByUseRole("employee");

		// object of responses class
		Responses response;

		ERSUsers createUser = null;

		if (eur != null) {
			requestData.setUser_role_id(eur.getEsr_user_role_id());

			// create new user
			createUser = new ERSUsersDAO().createUser(requestData);
		}

		if (createUser != null) {
			response = new Responses(200, "Created new user", true, createUser);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		} else {
			response = new Responses(400, "Failed to create new user", false, null);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		}
	};

	public static Handler serveRegisterPage = ctx -> {
		ctx.render(Path.Template.REGISTER);
	};

	public static Handler serveLoginPage = ctx -> {
		if (AuthController.verifyCookie(ctx.cookie("Authentication")) != null) {
			ctx.redirect(Path.Web.INDEX);
		} else {
			ctx.render(Path.Template.LOGIN);
		}
	};

	public static Handler doLogin = ctx -> {
		// Post method

		// object of gson class
		Gson gson = new Gson();

		// Get request body
		ERSUsers requestData = gson.fromJson(ctx.body(), ERSUsers.class);

		// object of responses class
		Responses response;

		// retrieve user records
		ERSUsers getUser = new ERSUsersDAO().getUserByUserName(requestData.getErs_username().toLowerCase());
		if (getUser != null) {
			// Compare passwords
			boolean comparePasswords = AuthUtil.comparePasswords(requestData.getErs_password(),
					getUser.getErs_password());

			// Response
			if (comparePasswords) {
				log.info("Successful login by " + getUser.getUser_first_name() + " " + getUser.getUser_last_name());
				response = new Responses(200, "Login Successful", true, getUser);
				ctx.status(response.getStatusCode()).json(gson.toJson(response)).cookie("Authentication",
						AuthUtil.doEncrypt(requestData.getErs_username() + ":" + requestData.getErs_password()));
			} else {
				log.info("Login failed. Incorrect credentials.");
				response = new Responses(400, "Incorrect Username or Password", false, getUser);
				ctx.status(response.getStatusCode()).json(gson.toJson(response));
			}
		} else {
			response = new Responses(400, "Incorrect Username or Password", false, getUser);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		}

	};

	public static Roles checkSetupAndRole(Context ctx) {

		// Get role of currently logged on user
		if (StringUtils.isNotEmpty(ctx.cookie("Authentication"))) {
			// Make sure authentication cookie is valid
			ERSUsers curUser = AuthController.verifyCookie(ctx.cookie("Authentication"));
			if (curUser != null) {
				ERSUserRoles getEur = new ERSUserRolesDAO().getRoleById(curUser.getUser_role_id());
				log.info(curUser.getUser_first_name() + " " + curUser.getUser_last_name() + " has the \""
						+ getEur.getUser_role() + "\" role.");
				switch (getEur.getUser_role()) {
				case "manager":
					return Roles.FINANCE_MANAGER;
				default:
					return Roles.EMPLOYEE;
				}
			}
		}

		// Default Role
		return Roles.ANYBODY;
	}

	public static ERSUsers verifyCookie(String authenticationStr) {
		// StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		// encryptor.setPassword(AuthUtil.internalSeed);
		if (StringUtils.isNoneEmpty(authenticationStr)) {
			String[] userPass = AuthUtil.doDecrypt(authenticationStr).split(":");
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
