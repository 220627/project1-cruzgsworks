package com.revature.ers.controllers;

import com.google.gson.Gson;
import com.revature.ers.daos.ERSUserRolesDAO;
import com.revature.ers.models.ERSUserRoles;
import com.revature.ers.models.Responses;

import io.javalin.http.Handler;

public class ERSUserRolesController {
	public static Handler getRoleById = (ctx) -> {
		// Post method

		// object of gson class
		Gson gson = new Gson();

		// Get request body
		ERSUserRoles requestData = gson.fromJson(ctx.body(), ERSUserRoles.class);
		ERSUserRoles getUserRole = new ERSUserRolesDAO().getRoleById(requestData.getEsr_user_role_id());

		// object of responses class
		Responses response;

		// Response
		if (getUserRole != null) {
			response = new Responses(200, "Retrieved User Information", true, getUserRole);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		} else {
			response = new Responses(400, "Could not create user", false, getUserRole);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		}

	};
}
