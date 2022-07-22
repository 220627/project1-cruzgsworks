package com.revature.ers.utils;

import io.javalin.http.Context;

public class RequestUtil {

	public static String getQueryUsername(Context ctx) {
		return ctx.formParam("ers_username");
	}

	public static String getQueryPassword(Context ctx) {
		return ctx.formParam("ers_password");
	}

	public static String getFormParamRedirect(Context ctx) {
		return ctx.formParam("loginRedirect");
	}

	public static String getSessionCurrentUser(Context ctx) {
		return (String) ctx.sessionAttribute("currentUser");
	}

	public static boolean removeSessionAttrLoggedOut(Context ctx) {
		String loggedOut = ctx.sessionAttribute("loggedOut");
		ctx.sessionAttribute("loggedOut", null);
		return loggedOut != null;
	}

	public static String removeSessionAttrLoginRedirect(Context ctx) {
		String loginRedirect = ctx.sessionAttribute("loginRedirect");
		ctx.sessionAttribute("loginRedirect", null);
		return loginRedirect;
	}
}
