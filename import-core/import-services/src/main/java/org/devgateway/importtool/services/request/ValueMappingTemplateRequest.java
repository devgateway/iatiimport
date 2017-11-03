package org.devgateway.importtool.services.request;

import org.devgateway.importtool.services.processor.helper.FieldValueMapping;

import java.util.List;
import java.util.ArrayList;

public class ValueMappingTemplateRequest {

	private String name;
	private Long id;

	private List<FieldValueMapping> fieldValueMapping = new ArrayList<FieldValueMapping>();

	public List<FieldValueMapping> getFieldValueMapping() {
		return fieldValueMapping;
	}

	public void setFieldValueMapping(List<FieldValueMapping> fieldValueMapping) {
		this.fieldValueMapping = fieldValueMapping;
	}

	public ValueMappingTemplateRequest() {

	}
	
	public Long getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
