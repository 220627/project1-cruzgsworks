package com.revature.ers.routes;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.put;

import java.io.File;

import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
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
					if (ctx.path().startsWith("/api")) {
						Responses resp = new Responses(401, "Unauthorized", false, null);
						ctx.status(resp.getStatusCode()).contentType("application/json")
								.result(new Gson().toJson(resp));
					} else {
						ctx.status(401).render(Path.Template.UNAUTHORIZED);
					}

				}
			});
			config.server(() -> {

				int httpPort = 8080;
				int httpsPort = 8443;

				Server server = new Server();

				// Setup HTTP configuration
				HttpConfiguration httpConfiguration = new HttpConfiguration();
				httpConfiguration.addCustomizer(new SecureRequestCustomizer());
				httpConfiguration.setSecureScheme(HttpScheme.HTTPS.asString());
				httpConfiguration.setSecurePort(httpsPort);

				ServerConnector httpConnector = new ServerConnector(server,
						new HttpConnectionFactory(httpConfiguration));
				httpConnector.setPort(httpPort);

				// Setup HTTPS configuration
				HttpConfiguration httpsConfiguration = new HttpConfiguration(httpConfiguration);
				httpsConfiguration.addCustomizer(new SecureRequestCustomizer());

				ServerConnector httpsConnector = new ServerConnector(server,
						new SslConnectionFactory(getSslContextFactory(), HttpVersion.HTTP_1_1.asString()),
						new HttpConnectionFactory(httpsConfiguration));
				httpsConnector.setPort(httpsPort);

				// Add connectors
				server.setConnectors(new Connector[] { httpsConnector, httpConnector });

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
				get(IndexController.serveIndexPage);
			});
			// Sign-up page
			path(Path.Web.REGISTER, () -> {
				get(AuthController.serveRegisterPage);
			});
			// New Manager
			path(Path.Web.REGISTER_MANAGER, () -> {
				get(AuthController.serveNewManager, new RouteRole[] { Roles.FINANCE_MANAGER });
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
			path("/api/manager/register", () -> {
				post(AuthController.createManager,
						new RouteRole[] { Roles.FINANCE_MANAGER });
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
						new RouteRole[] { Roles.EMPLOYEE, Roles.FINANCE_MANAGER });
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
			// Endpoint to create user and get user information (Get - Finance Manager only)
			path("/api/users", () -> {
				post(ERSUsersController.createUser);
				get(ERSUsersController.getUserByUserName,
						new RouteRole[] { Roles.FINANCE_MANAGER });
			});
			path("/api/users/current", () -> {
				get(ERSUsersController.currentUser,
						new RouteRole[] { Roles.FINANCE_MANAGER, Roles.EMPLOYEE });
			});
			path("/api/users/update/{ers_users_id}", () -> {
				put(ERSUsersController.updateUser,
						new RouteRole[] { Roles.FINANCE_MANAGER, Roles.EMPLOYEE });
			});
			path("/api/users/password/{ers_users_id}", () -> {
				put(ERSUsersController.updatePassword,
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
