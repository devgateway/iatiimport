package org.devgateway.importtool.services.processor.helper;


public class ActionStatus {
	private Status status = Status.NOT_STARTED;
	private long total = 0L;
	private long processed = 0L;
	

	private String message = "";
	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getProcessed() {
		return processed;
	}

	public void incrementProcessed() {
		++this.processed;
	}
	
	public void resetProcessed() {
		this.processed = 0L;
	}
	
	
	
	public ActionStatus(String message, long total) {
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
