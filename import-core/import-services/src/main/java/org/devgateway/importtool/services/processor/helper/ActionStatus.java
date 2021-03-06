package org.devgateway.importtool.services.processor.helper;


public class ActionStatus {
	private Status status = Status.NOT_STARTED;
	private long total = 0L;
	private Integer processed = 0;
	private String message = "";
	private Integer code;
	
	
	public ActionStatus() {
	
	}
	
	public ActionStatus(String message, long total, Integer code) {
		this.message = message;
		this.total = total;
		this.code = code;
	}
	
	
	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}
	public void setProcessed(Integer newProcessed) {
		 processed = newProcessed ;
	}

	public Integer getProcessed() {
		return processed;
	}

	public void incrementProcessed() {
		++this.processed;
	}
	
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
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
