package com.revature.ers.models;

public class RunOnce {
	private static boolean runonce = false;

	public static boolean isRunonce() {
		return runonce;
	}

	public static void setRunonce(boolean runonce) {
		RunOnce.runonce = runonce;
	}

}
