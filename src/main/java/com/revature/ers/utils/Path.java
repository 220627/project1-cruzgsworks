package com.revature.ers.utils;

public class Path {
	public static class Web {
		public static final String INDEX = "/";
		public static final String INITIAL_SETUP = "/setup";
		public static final String NOT_FOUND = "/notfound";
		public static final String LOGIN = "/login";
		public static final String REGISTER = "/register";
	}

	public static class Template {
		public static final String EMPLOYEE = "/velocity/index/employee.vm";
		public static final String MANAGER = "/velocity/index/manager.vm";
		public static final String INITIAL_SETUP = "/velocity/initialSetup/initialSetup.vm";
		public static final String NOT_FOUND = "/velocity/errorPages/notFound.vm";
		public static final String LOGIN = "/velocity/login/login.vm";
		public static final String REGISTER = "/velocity/login/register.vm";
	}
}
