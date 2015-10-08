package org.devgateway.importtool.services.processor.helper;


public class ActionResult {
	private String operation = "";
	private String status = "";
	private String message = "";
	private String id = "";
	
	public ActionResult(String id, String operation, String status, String message) {
		this.id = id;
		this.operation = operation;
		this.status = status;
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

}
