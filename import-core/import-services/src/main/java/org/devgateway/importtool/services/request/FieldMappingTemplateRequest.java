package org.devgateway.importtool.services.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.hateoas.Identifiable;
import org.devgateway.importtool.services.processor.helper.FieldMapping;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;


public class FieldMappingTemplateRequest {
	
	private String name;
	
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
	
	
}
