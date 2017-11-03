package org.devgateway.importtool.services.request;

import org.devgateway.importtool.services.processor.helper.FieldMapping;
import java.util.List;
import java.util.ArrayList;

public class FieldMappingTemplateRequest {

	private String name;
	private Long id;

	private List<FieldMapping> fieldMapping = new ArrayList<FieldMapping>();

	public FieldMappingTemplateRequest() {

	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<FieldMapping> getFieldMapping() {
		return fieldMapping;
	}

	public void setFieldMapping(List<FieldMapping> fieldMapping) {
		this.fieldMapping = fieldMapping;
	}

	public Long getId() {
		return id;
	}

}
