package com.revature.ers.routes;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.put;

import java.io.File;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;

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
	
	// Method to get SSL certificate
	private static SslContextFactory.Server getSslContextFactory() {
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(new File("./certs/cert.jks").getPath());
        sslContextFactory.setKeyStorePassword("H3aesN6dHNUACN");
        return sslContextFactory;
    }


	public void initRoute() {

		// Instance of Javalin
		Javalin app = Javalin.create(config -> {
			config.enableCorsForAllOrigins();
			config.addStaticFiles("/public", Location.CLASSPATH);
			config.accessManager((handler, ctx, routeRoles) -> {
				// Check role of current user
				Roles role = AuthController.checkSetupAndRole(ctx);
				if (routeRoles.isEmpty() || routeRoles.contains(role)) {
					handler.handle(ctx);
				} else {
					Responses resp = new Responses(401, "Unauthorized", false, null);
					ctx.status(resp.getStatusCode()).contentType("application/json").result(new Gson().toJson(resp));
				}
			});
			// Configure SSL
			config.server(() -> {
                Server server = new Server();
                ServerConnector sslConnector = new ServerConnector(server, getSslContextFactory());
                // Run SSL on port 8443
                sslConnector.setPort(8443);
                ServerConnector connector = new ServerConnector(server);
                // Run HTTP on port 8080
                connector.setPort(8080);
                server.setConnectors(new Connector[]{sslConnector, connector});
                return server;
            });
		}).start();

		app.routes(() -> {
			// Setup pages
			path(Path.Web.INITIAL_SETUP, () -> {
				get(SetupController.serveSetupPage);
			});
			// Index page
			path(Path.Web.INDEX, () -> {
				get(IndexController	.serveIndexPage);
			});
			// Sign-up page
			path(Path.Web.REGISTER, () -> {
				get(AuthController.serveRegisterPage);
			});
			// Login page
			path(Path.Web.LOGIN, () -> {
				get(AuthController.serveLoginPage);
			});
			// Endpoint to process authentication
			path("/api/login", () -> {
				post(AuthController.doLogin);
			});
			// Endpoint to process signup
			path("/api/register", () -> {
				post(AuthController.doRegister);
			});
			// Should be accessed first time when app is deployed
			path("/api/setup", () -> {
				post(SetupController.doInitialSetup);
			});
			// Endpoint to update status of reimbursements
			path("/api/manager/reimbursement/resolve/{reimb_id}", () -> {
				put(ERSReimbursementController.resolveReimbursements,
						new RouteRole[] { Roles.FINANCE_MANAGER });
			});
			// Endpoint to download receipts
			path("/api/employee/reimbursement/receipt/{reimb_id}", () -> {
				get(ERSReimbursementController.getReceipt,
						new RouteRole[] { Roles.EMPLOYEE });
			});
			// NOT USED - Replaced with /api/reimbursement/list which includes pagination
			path("/api/employee/reimbursement", () -> {
				get(ERSReimbursementController.getReimbursementRequests,
						new RouteRole[] { Roles.EMPLOYEE });
			});
			// NOT USED - Replaced with /api/reimbursement/list which includes pagination
			path("/api/manager/reimbursement", () -> {
				get(ERSReimbursementController.getAllReimbursementRequests,
						new RouteRole[] { Roles.FINANCE_MANAGER });
			});
			// Endpoint to get list of reimbursements
			path("/api/reimbursement/list", () -> {
				get(ERSReimbursementController.getReimbursementRequestsPagination,
						new RouteRole[] { Roles.FINANCE_MANAGER, Roles.EMPLOYEE });
			});
			// Count reimbursements that user can view. For pagination
			path("/api/reimbursement/count", () -> {
				get(ERSReimbursementController.countReimbursements,
						new RouteRole[] { Roles.FINANCE_MANAGER, Roles.EMPLOYEE });
			});
			// Endpoint to create new reimbursements
			path("/api/reimbursement/new", () -> {
				post(ERSReimbursementController.newReimbursementRequest,
						new RouteRole[] { Roles.EMPLOYEE });
			});
			// Endpoint to get reimbursement types
			path("/api/reimbursement/types", () -> {
				get(ERSReimbursementTypeController.getAllTypes,
						new RouteRole[] { Roles.FINANCE_MANAGER, Roles.EMPLOYEE });
			});
			// Endpoint to create user and get user information
			path("/api/users", () -> {
				post(ERSUsersController.createUser);
				get(ERSUsersController.getUserByUserName,
						new RouteRole[] { Roles.FINANCE_MANAGER, Roles.EMPLOYEE });
			});
			// Endpoint to get roles
			path("/api/roles", () -> {
				get(ERSUserRolesController.getRoleById,
						new RouteRole[] { Roles.FINANCE_MANAGER, Roles.EMPLOYEE });
			});
		}).error(404, ViewUtil.notFound);
	}

}
