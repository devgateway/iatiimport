package org.devgateway.importtool.services.processor.helper;


public class ActionStatus {
	private Status status = Status.NOT_STARTED;
	private Long total = 0L;
	private Long processed = 0L;
	private String message = "";
	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Long getProcessed() {
		return processed;
	}

	public void incrementProcessed() {
		++this.processed;
	}	
	
	public ActionStatus(String message, Long total) {
		this.message = message;
		this.total = total;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getMessage() {
		return String.format(message, processed, total);		
	}

	public void setMessage(String message) {
		this.message = message;
	}

	

}
