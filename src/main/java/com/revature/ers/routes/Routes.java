package com.revature.ers.routes;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.put;

import com.google.gson.Gson;
import com.revature.ers.controllers.AuthController;
import com.revature.ers.controllers.ERSReimbursementController;
import com.revature.ers.controllers.ERSReimbursementTypeController;
import com.revature.ers.controllers.ERSUserRolesController;
import com.revature.ers.controllers.ERSUsersController;
import com.revature.ers.controllers.IndexController;
import com.revature.ers.controllers.SetupController;
import com.revature.ers.models.Responses;
import com.revature.ers.models.Roles;
import com.revature.ers.utils.Path;
import com.revature.ers.utils.ViewUtil;

import io.javalin.Javalin;
import io.javalin.core.security.RouteRole;
import io.javalin.http.staticfiles.Location;

public class Routes {

	public void initRoute() {

		// Instance of Javalin
		Javalin app = Javalin.create(config -> {
			config.enableCorsForAllOrigins();
			config.addStaticFiles("/public", Location.CLASSPATH);
			config.accessManager((handler, ctx, routeRoles) -> {
				
				Roles role = AuthController.checkSetupAndRole(ctx);
				if (routeRoles.isEmpty() || routeRoles.contains(role)) {
					handler.handle(ctx);
				} else {
					Responses resp = new Responses(401, "Unauthorized", false, null);
					ctx.status(resp.getStatusCode()).contentType("application/json").result(new Gson().toJson(resp));
				}
				/*
				// Make sure the environment is setup first
				if (SetupController.isRun_once()) {
					// Make initial setup page inaccessible
					if (ctx.path().startsWith("/initialsetup")) {
						ctx.redirect(Path.Web.INDEX);
					} else {
						
					}
				}
				// Forward to initial setup page if it hasn't been done yet
				else {
					if (!ctx.path().startsWith("/initialsetup")) {
						LoggerUtil.log("AuthController").info("Redirect user to initial setup page.");
						ctx.redirect(Path.Web.INITIAL_SETUP);
					}
				}
				*/
			});
		}).start(8080);

		app.routes(() -> {
			path(Path.Web.INITIAL_SETUP, () -> {
				get(SetupController.serveSetupPage);
			});
			path(Path.Web.INDEX, () -> {
				get(IndexController.serveIndexPage);
			});
			path(Path.Web.REGISTER, () -> {
				get(AuthController.serveRegisterPage);
			});
			path(Path.Web.LOGIN, () -> {
				get(AuthController.serveLoginPage);
			});
			path("/api/login", () -> {
				post(AuthController.doLogin);
			});
			path("/api/register", () -> {
				post(AuthController.doRegister);
			});
			path("/api/setup", () -> {
				post(SetupController.doInitialSetup);
			});
			path("/api/manager/reimbursement/resolve/{reimb_id}", () -> {
				put(ERSReimbursementController.resolveReimbursements,
						new RouteRole[] { Roles.FINANCE_MANAGER });
			});
			path("/api/employee/reimbursement/receipt/{reimb_id}", () -> {
				get(ERSReimbursementController.getReceipt,
						new RouteRole[] { Roles.EMPLOYEE });
			});
			path("/api/employee/reimbursement", () -> {
				get(ERSReimbursementController.getReimbursementRequests,
						new RouteRole[] { Roles.EMPLOYEE });
			});
			path("/api/manager/reimbursement", () -> {
				get(ERSReimbursementController.getAllReimbursementRequests,
						new RouteRole[] { Roles.FINANCE_MANAGER });
			});
			path("/api/reimbursement/list", () -> {
				get(ERSReimbursementController.getReimbursementRequestsPagination,
						new RouteRole[] { Roles.FINANCE_MANAGER, Roles.EMPLOYEE });
			});
			path("/api/reimbursement/count", () -> {
				get(ERSReimbursementController.countReimbursements,
						new RouteRole[] { Roles.FINANCE_MANAGER, Roles.EMPLOYEE });
			});
			path("/api/reimbursement/new", () -> {
				post(ERSReimbursementController.newReimbursementRequest,
						new RouteRole[] { Roles.EMPLOYEE });
			});
			path("/api/reimbursement/types", () -> {
				get(ERSReimbursementTypeController.getAllTypes,
						new RouteRole[] { Roles.FINANCE_MANAGER, Roles.EMPLOYEE });
			});
			path("/api/users", () -> {
				post(ERSUsersController.createUser);
				get(ERSUsersController.getUserByUserName,
						new RouteRole[] { Roles.FINANCE_MANAGER, Roles.EMPLOYEE });
			});
			path("/api/roles", () -> {
				get(ERSUserRolesController.getRoleById,
						new RouteRole[] { Roles.FINANCE_MANAGER, Roles.EMPLOYEE });
			});
		}).error(404, ViewUtil.notFound);
	}

}
