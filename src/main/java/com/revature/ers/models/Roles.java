package com.revature.ers.models;

import io.javalin.core.security.RouteRole;

public enum Roles implements RouteRole {
	ADMIN, FINANCE_MANAGER, EMPLOYEE, ANYBODY
}
