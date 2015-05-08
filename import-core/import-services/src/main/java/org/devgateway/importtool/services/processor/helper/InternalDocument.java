package org.devgateway.importtool.services.processor.helper;

import java.util.HashMap;
import java.util.Map;

public class InternalDocument {
	private Map<String, Object> fields = new HashMap<String, Object>();
	private OperationType operation = OperationType.NOOP;

	public Map<String, Object> getFields() {
		return fields;
	}

	public void setField(Map<String, Object> field) {
		this.fields = field;
	}

	public void addStringField(String fieldName, String value) {
		fields.put(fieldName, value);
	}

	public OperationType getOperation() {
		return operation;
	}

	public void setOperation(OperationType operation) {
		this.operation = operation;
	}

}
