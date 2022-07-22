package com.revature.ers.controllers;

import org.apache.commons.lang3.StringUtils;

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

	public static Handler doRegister = ctx -> {
		// Post method

		// object of gson class
		Gson gson = new Gson();

		// Get request body
		ERSUsers requestData = gson.fromJson(ctx.body(), ERSUsers.class);
		ERSUserRoles eur = new ERSUserRolesDAO().getRoleByUseRole("employee");
		requestData.setUser_role_id(eur.getEsr_user_role_id());

		// object of responses class
		Responses response;

		// create new user
		ERSUsers createUser = new ERSUsersDAO().createUser(requestData);

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
		if (AuthUtil.verifyCookie(ctx.cookie("Authentication")) != null) {
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
				response = new Responses(200, "Login Successful", true, getUser);
				ctx.status(response.getStatusCode()).json(gson.toJson(response)).cookie("Authentication",
						AuthUtil.doEncrypt(requestData.getErs_username() + ":" + requestData.getErs_password()));
			} else {
				response = new Responses(400, "Incorrect Username or Password", false, getUser);
				ctx.status(response.getStatusCode()).json(gson.toJson(response));
			}
		} else {
			response = new Responses(400, "Incorrect Username or Password", false, getUser);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		}

	};

	public static Roles checkSetupAndRole(Context ctx) {
		// Make sure the environment is setup first
		if (SetupController.isRun_once()) {
			// Make initial setup page inaccessible
			if (ctx.path().startsWith("/initialsetup")) {
				ctx.redirect(Path.Web.INDEX);
			}
			// Get role of currently logged on user
			else if (StringUtils.isNotEmpty(ctx.cookie("Authentication"))) {
				// Make sure authentication cookie is valid
				ERSUsers curUser = AuthUtil.verifyCookie(ctx.cookie("Authentication"));
				if (curUser != null) {
					ERSUserRoles getEur = new ERSUserRolesDAO().getRoleById(curUser.getUser_role_id());
					switch (getEur.getUser_role()) {
					case "manager":
						return Roles.FINANCE_MANAGER;
					default:
						return Roles.EMPLOYEE;
					}
				}
			}

		}
		// Forward to initial setup page if it hasn't been done yet
		else {
			if (!ctx.path().startsWith("/initialsetup")) {
				ctx.redirect(Path.Web.INITIAL_SETUP);
			}
		}

		// Default Role
		return Roles.ANYBODY;
	}

}
