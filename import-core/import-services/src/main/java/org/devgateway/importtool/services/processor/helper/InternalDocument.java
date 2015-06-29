package org.devgateway.importtool.services.processor.helper;

import java.util.HashMap;
import java.util.Map;

public class InternalDocument {
	private String identifier = "";
	private String title = "";
	private Map<String, Object> fields = new HashMap<String, Object>();

	public Map<String, Object> getFields() {
		return fields;
	}

	public void setField(Map<String, Object> field) {
		this.fields = field;
	}

	public void addStringField(String fieldName, String value) {
		fields.put(fieldName, value);
	}

	public Object getField(String fieldName) {
		return fields.get(fieldName);
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
