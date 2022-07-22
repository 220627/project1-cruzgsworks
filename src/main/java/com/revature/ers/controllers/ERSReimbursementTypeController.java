package com.revature.ers.controllers;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.revature.ers.daos.ERSReimbursementTypeDAO;
import com.revature.ers.models.ERSReimbursementType;
import com.revature.ers.models.Responses;

import io.javalin.http.Handler;

public class ERSReimbursementTypeController {

	public static Handler getAllTypes = (ctx) -> {
		// Post method

		// object of gson class
		Gson gson = new Gson();

		// retrieve user records
		ArrayList<ERSReimbursementType> getTypes = new ERSReimbursementTypeDAO().getAllReimbursementTypeByReimbTypes();

		// object of responses class
		Responses response;

		// Response
		if (getTypes.size() > 0) {
			response = new Responses(200, "Retrieved Reimbursement Types", true, getTypes);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		} else {
			response = new Responses(400, "Could not create user", false, null);
			ctx.status(response.getStatusCode()).json(gson.toJson(response));
		}
	};

}
