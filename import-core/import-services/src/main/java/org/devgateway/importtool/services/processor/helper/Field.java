package org.devgateway.importtool.services.processor.helper;

import java.util.ArrayList;
import java.util.List;

public class Field {
	private FieldType type;
	private String fieldName;
	private List<String> possibleValues;
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

	public List<String> getPossibleValues() {
		return possibleValues;
	}

	//Setter left only for unit tests. See if still needed
	public void setPossibleValues(List<String> possibleValues) {
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
	
	

}
