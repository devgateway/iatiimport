package org.devgateway.importtool.services.processor.helper;


public class ActionResult {
	private String operation = "";
	private String status = "";
	private String message = "";
	private String id = "";
	private String sourceProjectIdentifier;
	private String sourceGroupingCriteria;

	public ActionResult(String id, String operation, String status, String message) {
		this(id, operation, status, message, null, null);
	}

	public ActionResult(String id, String operation, String status, String message, String sourceProjectIdentifier, String
			sourceGroupingCriteria) {
		this.id = id;
		this.operation = operation;
		this.status = status;
		this.message = message;
		this.sourceProjectIdentifier = sourceProjectIdentifier;
		this.sourceGroupingCriteria = sourceGroupingCriteria;
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

	public String getSourceProjectIdentifier() {
		return sourceProjectIdentifier;
	}

	public void setSourceProjectIdentifier(String sourceProjectIdentifier) {
		this.sourceProjectIdentifier = sourceProjectIdentifier;
	}

	public String getSourceGroupingCriteria() {
		return sourceGroupingCriteria;
	}

	public void setSourceGroupingCriteria(String sourceGroupingCriteria) {
		this.sourceGroupingCriteria = sourceGroupingCriteria;
	}
}
