package org.devgateway.importtool.services.processor.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Field {
	private FieldType type;
	private String fieldName;
	private Map<String, String> attributes;
	private List<Field> childFields;
	private Map<String, String> possibleValues;
	private List<String> filters;
	
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

	public Map<String, String> getPossibleValues() {
		return possibleValues;
	}

	//Setter left only for unit tests. See if still needed
	public void setPossibleValues(Map<String, String> possibleValues) {
		this.possibleValues = possibleValues;
	}

	public void setFilter(String string) {
		// TODO Auto-generated method stub
		
	}

	public List<String> getFilters() {
		return filters;
	}

	public void setFilters(List<String> filters) {
		this.filters = filters;
	}

	public void addFilter(String string) {
		if(this.filters == null) {
			this.filters = new ArrayList<String>();
		}  
		this.filters.add(string);
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

	public void appendPossibleValue(String code, String name) {
		if(!this.possibleValues.containsKey(code) && !this.possibleValues.containsValue(name) ) {
			this.possibleValues.put(code, name);
		}
	}
	
	public String toString() {
		return fieldName;
	}
}

