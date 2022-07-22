package com.revature.ers.controllers;

import com.google.gson.Gson;
import com.revature.ers.daos.ERSUsersDAO;
import com.revature.ers.models.ERSUsers;
import com.revature.ers.models.Responses;

import io.javalin.http.Handler;

public class ERSUsersController {

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
