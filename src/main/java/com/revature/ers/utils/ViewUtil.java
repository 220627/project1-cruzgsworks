package com.revature.ers.utils;

import static com.revature.ers.utils.RequestUtil.getSessionCurrentUser;

import java.util.HashMap;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class ViewUtil {
	public static Map<String, Object> baseModel(Context ctx) {
		Map<String, Object> model = new HashMap<>();
		model.put("currentUser", getSessionCurrentUser(ctx));
		return model;
	}

	public static Handler notFound = ctx -> {
		ctx.render(Path.Template.NOT_FOUND);
	};

}
