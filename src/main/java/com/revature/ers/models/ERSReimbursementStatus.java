package com.revature.ers.models;

public class ERSReimbursementStatus {
	private int reimb_status_id;
	private String reimb_status;

	public ERSReimbursementStatus(int reimb_status_id, String reimb_status) {
		super();
		this.reimb_status_id = reimb_status_id;
		this.reimb_status = reimb_status;
	}

	public ERSReimbursementStatus(String reimb_status) {
		super();
		this.reimb_status = reimb_status;
	}

	public int getReimb_status_id() {
		return reimb_status_id;
	}

	public void setReimb_status_id(int reimb_status_id) {
		this.reimb_status_id = reimb_status_id;
	}

	public String getReimb_status() {
		return reimb_status;
	}

	public void setReimb_status(String reimb_status) {
		this.reimb_status = reimb_status;
	}
}
