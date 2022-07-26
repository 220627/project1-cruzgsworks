package com.revature.ers.models;

import java.sql.Timestamp;

public class ERSReimbursement {
	private int reimb_id;
	private double reimb_amount;
	private Timestamp reimb_submitted;
	private Timestamp reimb_resolved;
	private String reimb_description;
	private byte[] reimb_receipt;
	private int reimb_author;
	private int reimb_resolver;
	private int reimb_status_id;
	private int reimb_type_id;
	private ERSUsers ersAuthor;
	private ERSUsers ersResolver;
	private ERSReimbursementStatus ersReimbursementStatus;
	private ERSReimbursementType ersReimbursementType;
	private boolean has_receipt;

	public boolean isHas_receipt() {
		return has_receipt;
	}

	public void setHas_receipt(boolean has_receipt) {
		this.has_receipt = has_receipt;
	}

	public ERSUsers getErsAuthor() {
		return ersAuthor;
	}

	public void setErsAuthor(ERSUsers ersAuthor) {
		this.ersAuthor = ersAuthor;
	}

	public ERSUsers getErsResolver() {
		return ersResolver;
	}

	public void setErsResolver(ERSUsers ersResolver) {
		this.ersResolver = ersResolver;
	}

	public ERSReimbursementStatus getErsReimbursementStatus() {
		return ersReimbursementStatus;
	}

	public void setErsReimbursementStatus(ERSReimbursementStatus ersReimbursementStatus) {
		this.ersReimbursementStatus = ersReimbursementStatus;
	}

	public ERSReimbursementType getErsReimbursementType() {
		return ersReimbursementType;
	}

	public void setErsReimbursementType(ERSReimbursementType ersReimbursementType) {
		this.ersReimbursementType = ersReimbursementType;
	}

	public ERSReimbursement(int reimb_id, double reimb_amount, Timestamp reimb_submitted, Timestamp reimb_resolved,
			String reimb_description, byte[] reimb_receipt, int reimb_author, int reimb_resolver, int reimb_status_id,
			int reimb_type_id) {
		super();
		this.reimb_id = reimb_id;
		this.reimb_amount = reimb_amount;
		this.reimb_submitted = reimb_submitted;
		this.reimb_resolved = reimb_resolved;
		this.reimb_description = reimb_description;
		this.reimb_receipt = reimb_receipt;
		this.reimb_author = reimb_author;
		this.reimb_resolver = reimb_resolver;
		this.reimb_status_id = reimb_status_id;
		this.reimb_type_id = reimb_type_id;
	}
	
	

	public ERSReimbursement(int reimb_id, double reimb_amount, Timestamp reimb_submitted, Timestamp reimb_resolved,
			String reimb_description, int reimb_author, int reimb_resolver, int reimb_status_id, int reimb_type_id) {
		super();
		this.reimb_id = reimb_id;
		this.reimb_amount = reimb_amount;
		this.reimb_submitted = reimb_submitted;
		this.reimb_resolved = reimb_resolved;
		this.reimb_description = reimb_description;
		this.reimb_author = reimb_author;
		this.reimb_resolver = reimb_resolver;
		this.reimb_status_id = reimb_status_id;
		this.reimb_type_id = reimb_type_id;
	}

	public ERSReimbursement(double reimb_amount, Timestamp reimb_submitted, String reimb_description,
			byte[] reimb_receipt, int reimb_author, int reimb_status_id, int reimb_type_id) {
		super();
		this.reimb_amount = reimb_amount;
		this.reimb_submitted = reimb_submitted;
		this.reimb_description = reimb_description;
		this.reimb_receipt = reimb_receipt;
		this.reimb_author = reimb_author;
		this.reimb_status_id = reimb_status_id;
		this.reimb_type_id = reimb_type_id;
	}

	public ERSReimbursement() {
		super();
	}

	public int getReimb_id() {
		return reimb_id;
	}

	public void setReimb_id(int reimb_id) {
		this.reimb_id = reimb_id;
	}

	public double getReimb_amount() {
		return reimb_amount;
	}

	public void setReimb_amount(double reimb_amount) {
		this.reimb_amount = reimb_amount;
	}

	public Timestamp getReimb_submitted() {
		return reimb_submitted;
	}

	public void setReimb_submitted(Timestamp reimb_submitted) {
		this.reimb_submitted = reimb_submitted;
	}

	public Timestamp getReimb_resolved() {
		return reimb_resolved;
	}

	public void setReimb_resolved(Timestamp reimb_resolved) {
		this.reimb_resolved = reimb_resolved;
	}

	public String getReimb_description() {
		return reimb_description;
	}

	public void setReimb_description(String reimb_description) {
		this.reimb_description = reimb_description;
	}

	public byte[] getReimb_receipt() {
		return reimb_receipt;
	}

	public void setReimb_receipt(byte[] reimb_receipt) {
		this.reimb_receipt = reimb_receipt;
	}

	public int getReimb_author() {
		return reimb_author;
	}

	public void setReimb_author(int reimb_author) {
		this.reimb_author = reimb_author;
	}

	public int getReimb_resolver() {
		return reimb_resolver;
	}

	public void setReimb_resolver(int reimb_resolver) {
		this.reimb_resolver = reimb_resolver;
	}

	public int getReimb_status_id() {
		return reimb_status_id;
	}

	public void setReimb_status_id(int reimb_status_id) {
		this.reimb_status_id = reimb_status_id;
	}

	public int getReimb_type_id() {
		return reimb_type_id;
	}

	public void setReimb_type_id(int reimb_type_id) {
		this.reimb_type_id = reimb_type_id;
	}

}
