package com.revature.ers.models;

public class ERSReimbursementType {
	private int reimb_type_id;
	private String reimb_type;

	public int getReimb_type_id() {
		return reimb_type_id;
	}

	public void setReimb_type_id(int reimb_type_id) {
		this.reimb_type_id = reimb_type_id;
	}

	public String getReimb_type() {
		return reimb_type;
	}

	public void setReimb_type(String reimb_type) {
		this.reimb_type = reimb_type;
	}

	public ERSReimbursementType(int reimb_type_id, String reimb_type) {
		super();
		this.reimb_type_id = reimb_type_id;
		this.reimb_type = reimb_type;
	}

	public ERSReimbursementType(String reimb_type) {
		super();
		this.reimb_type = reimb_type;
	}
}
