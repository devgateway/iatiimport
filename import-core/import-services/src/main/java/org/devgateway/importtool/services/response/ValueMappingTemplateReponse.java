package org.devgateway.importtool.services.response;

import org.devgateway.importtool.services.processor.helper.FieldValueMapping;
import java.util.List;
import java.util.ArrayList;


public class ValueMappingTemplateReponse {
	private Long id;
	
	private String name;
	
	private List<FieldValueMapping> fieldValueMapping = new ArrayList<FieldValueMapping>();
			
	public List<FieldValueMapping> getFieldValueMapping() {
		return fieldValueMapping;
	}

	public void setFieldValueMapping(List<FieldValueMapping> fieldValueMapping) {
		this.fieldValueMapping = fieldValueMapping;
	}

	public ValueMappingTemplateReponse() {		
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
	
	

	
}
