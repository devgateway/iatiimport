package org.devgateway.importtool.services.processor.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Field {
	private FieldType type;
	private String fieldName;
	private String displayName;
	private Map<String, String> attributes;
	private List<Field> childFields;
	private List<FieldValue> possibleValues;
	private List<String> filters = new ArrayList<String>();
	
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public List<FieldValue> getPossibleValues() {
		return possibleValues;
	}

	//Setter left only for unit tests. See if still needed
	public void setPossibleValues(List<FieldValue> possibleValues) {
		this.possibleValues = possibleValues;
	}

	public List<String> getFilters() {
		return filters;
	}

	public void setFilters(List<String> filters) {
		this.filters = filters;
	}

	public void addFilter(String value) {
		this.filters.add(value);
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public List<Field> getChildFields() {
		return childFields;
	}

	public void setChildFields(List<Field> childFields) {
		this.childFields = childFields;
	}
	
	public boolean equals(Field f) {
		return f.getFieldName() == this.getFieldName();
	}

//	public void appendPossibleValue(String code, String name) {
//		if(!this.possibleValues.containsKey(code) && !this.possibleValues.containsValue(name) ) {
//			this.possibleValues.put(code, name);
//		}
//	}
	
	public String toString() {
		return fieldName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}

