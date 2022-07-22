package com.revature.ers.controllers;

import java.util.Map;

import com.revature.ers.daos.ERSUserRolesDAO;
import com.revature.ers.models.ERSUserRoles;
import com.revature.ers.models.ERSUsers;
import com.revature.ers.utils.AuthUtil;
import com.revature.ers.utils.Path;
import com.revature.ers.utils.ViewUtil;

import io.javalin.http.Handler;

public class IndexController {
	public static Handler serveIndexPage = ctx -> {
		Map<String, Object> model = ViewUtil.baseModel(ctx);

		ERSUsers curUser = AuthUtil.verifyCookie(ctx.cookie("Authentication"));

		if (curUser != null) {
			ERSUserRoles getEur = new ERSUserRolesDAO().getRoleById(curUser.getUser_role_id());

			model.put("userInfo", curUser);
			model.put("roleInfo", getEur);

			if (getEur.getUser_role().equals("manager")) {
				ctx.render(Path.Template.MANAGER, model);
			} else {
				ctx.render(Path.Template.EMPLOYEE, model);
			}
		} else {
			ctx.redirect(Path.Web.LOGIN);
		}
	};
}
