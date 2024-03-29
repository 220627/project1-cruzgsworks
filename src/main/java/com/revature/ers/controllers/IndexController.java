package com.revature.ers.controllers;

import java.util.Map;

import com.revature.ers.daos.ERSUserRolesDAO;
import com.revature.ers.models.ERSUserRoles;
import com.revature.ers.models.ERSUsers;
import com.revature.ers.utils.Path;
import com.revature.ers.utils.ViewUtil;

import io.javalin.http.Handler;

public class IndexController {
	public static Handler serveIndexPage = ctx -> {
		Map<String, Object> model = ViewUtil.baseModel(ctx);

		ERSUsers curUser = AuthController.verifyCookie(ctx.cookie("Authentication"));

		if (curUser != null) {
			ERSUserRoles getEur = new ERSUserRolesDAO().getRoleById(curUser.getUser_role_id());
			
			model.put("userInfo", curUser);

			if (getEur.getUser_role().toLowerCase().equals("manager")) {
				String roleInfo = "Finance Manager";
				model.put("roleInfo", roleInfo);
				ctx.render(Path.Template.MANAGER, model);
			} else {
				String roleInfo = "Employee";
				model.put("roleInfo", roleInfo);
				ctx.render(Path.Template.EMPLOYEE, model);
			}
		} else {
			ctx.redirect(Path.Web.LOGIN);
		}
	};
}
