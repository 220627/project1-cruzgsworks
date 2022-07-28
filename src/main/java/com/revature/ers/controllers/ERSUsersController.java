package com.revature.ers.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.revature.ers.daos.ERSUsersDAO;
import com.revature.ers.models.ERSUsers;
import com.revature.ers.models.Responses;
import com.revature.ers.utils.AuthUtil;

import io.javalin.http.Handler;

public class ERSUsersController {

	public static Logger log = LogManager.getLogger();

	public static Handler updatePassword = (ctx) -> {
		// Put method

		// object of gson class
		Gson gson = new Gson();

		ERSUsers updatePassword = null;
		try {
			ERSUsers curUser = AuthUtil.verifyCookie(ctx.cookie("Authentication"));
			// Get request body
			ERSUsers requestData = gson.fromJson(ctx.body(), ERSUsers.class);

			int ers_users_id = Integer.parseInt(ctx.pathParam("ers_users_id"));

			// update the user
			if (curUser.getErs_users_id() == ers_users_id) {
				requestData.setErs_users_id(ers_users_id);
				updatePassword = new ERSUsersDAO().updatePassword(requestData);
			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

		// object of responses class
		Responses response;

		// Response
		if (updatePassword != null) {
			response = new Responses(200, "Password updated", true, updatePassword);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		} else {
			response = new Responses(400, "Could not update user", false, null);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		}
	};

	public static Handler currentUser = (ctx) -> {
		// Post method

		// object of gson class
		Gson gson = new Gson();

		ERSUsers getUser = null;
		try {
			ERSUsers curUser = AuthUtil.verifyCookie(ctx.cookie("Authentication"));

			getUser = new ERSUsersDAO().getUserByUserName(curUser.getErs_username());

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

		// object of responses class
		Responses response;

		// Response
		if (getUser != null) {
			response = new Responses(200, "Retrieved User Information", true, getUser);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		} else {
			response = new Responses(400, "Could not retrieve user", false, null);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		}
	};

	public static Handler updateUser = (ctx) -> {
		// Put method

		// object of gson class
		Gson gson = new Gson();

		ERSUsers updateUser = null;
		try {
			ERSUsers curUser = AuthUtil.verifyCookie(ctx.cookie("Authentication"));
			// Get request body
			ERSUsers requestData = gson.fromJson(ctx.body(), ERSUsers.class);

			int ers_users_id = Integer.parseInt(ctx.pathParam("ers_users_id"));

			// update the user
			if (curUser.getErs_users_id() == ers_users_id) {
				requestData.setErs_users_id(ers_users_id);
				updateUser = new ERSUsersDAO().updateUser(requestData);
			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

		// object of responses class
		Responses response;

		// Response
		if (updateUser != null) {
			response = new Responses(200, "User updated", true, updateUser);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		} else {
			response = new Responses(400, "Could not update user", false, null);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		}
	};

	public static Handler getUserByUserName = (ctx) -> {
		// Post method

		// object of gson class
		Gson gson = new Gson();

		// Get request body
		ERSUsers requestData = gson.fromJson(ctx.body(), ERSUsers.class);

		// retrieve user records
		ERSUsers getUser = new ERSUsersDAO().getUserByUserName(requestData.getErs_username());

		// object of responses class
		Responses response;

		// Response
		if (getUser != null) {
			response = new Responses(200, "Retrieved User Information", true, getUser);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		} else {
			response = new Responses(400, "Could not create user", false, null);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		}

	};

	public static Handler createUser = (ctx) -> {
		// Post method

		// object of gson class
		Gson gson = new Gson();

		// Get request body
		ERSUsers requestData = gson.fromJson(ctx.body(), ERSUsers.class);

		// Create the user
		ERSUsers newUser = new ERSUsersDAO().createUser(requestData);

		// object of responses class
		Responses response;

		// Response
		if (newUser != null) {
			response = new Responses(200, "User Created", true, newUser);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		} else {
			response = new Responses(400, "Could not create user", false, null);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		}

	};

}
