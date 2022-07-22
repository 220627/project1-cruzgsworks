package com.revature.ers.models;

public class ERSUserRoles {
	private int esr_user_role_id;
	private String user_role;

	public ERSUserRoles(int esr_user_role_id, String user_role) {
		super();
		this.esr_user_role_id = esr_user_role_id;
		this.user_role = user_role;
	}

	public ERSUserRoles(String user_role) {
		super();
		this.user_role = user_role;
	}

	public ERSUserRoles() {
		super();
	}

	public int getEsr_user_role_id() {
		return esr_user_role_id;
	}

	public void setEsr_user_role_id(int esr_user_role_id) {
		this.esr_user_role_id = esr_user_role_id;
	}

	public String getUser_role() {
		return user_role;
	}

	public void setUser_role(String user_role) {
		this.user_role = user_role;
	}

}
