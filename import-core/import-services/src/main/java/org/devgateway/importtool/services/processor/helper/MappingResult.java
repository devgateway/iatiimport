package org.devgateway.importtool.services.processor.helper;

import java.util.HashMap;
import java.util.Map;

public class MappingResult {
	private Map<String, String> operationStatuses = new HashMap<String, String>();
	
	public void addOperationStatus(String status, String message) {
		this.getOperationStatuses().put(status, message);
	}

	public Map<String, String> getOperationStatuses() {
		return operationStatuses;
	}

	public void setOperationStatuses(Map<String, String> operationStatuses) {
		this.operationStatuses = operationStatuses;
	}

}
