package com.revature.ers.models;

public class Responses {
	private int statusCode;
	private String statusMessage;
	private boolean statusSuccess;
	private Object statusObject;

	public Responses(int statusCode, String statusMessage, boolean statusSuccess, Object statusObject) {
		super();
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.statusSuccess = statusSuccess;
		this.statusObject = statusObject;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public boolean isStatusSuccess() {
		return statusSuccess;
	}

	public void setStatusSuccess(boolean statusSuccess) {
		this.statusSuccess = statusSuccess;
	}

	public Object getStatusObject() {
		return statusObject;
	}

	public void setStatusObject(Object statusObject) {
		this.statusObject = statusObject;
	}
}
