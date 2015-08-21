package org.devgateway.importtool.services.response;

import org.devgateway.importtool.services.processor.helper.FieldMapping;

import java.util.List;
import java.util.ArrayList;


public class FieldMappingTemplateReponse {
	private Long id;
	
	private String name;
	
	private List<FieldMapping> fieldMapping = new ArrayList<FieldMapping>();
			
	public FieldMappingTemplateReponse() {
		
	}

	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
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
	
	
}
