package org.devgateway.importtool.services.processor.helper;

import java.util.Map;

public class InternalDocument {
	private Map<String, Object> field;
	private OperationType operation = OperationType.NOOP;

	public void addStringField(String fieldName, String value) {
		field.put(fieldName, value);
	}

	public OperationType getOperation() {
		return operation;
	}

	public void setOperation(OperationType operation) {
		this.operation = operation;
	}
	
}
