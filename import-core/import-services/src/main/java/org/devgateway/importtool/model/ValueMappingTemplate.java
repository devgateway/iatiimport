package org.devgateway.importtool.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.hateoas.Identifiable;

import javax.persistence.*;
import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@Table(name = "value_mapping_template")
public class ValueMappingTemplate implements Identifiable<Long>, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "value_mapping_sequence")
	@SequenceGenerator(name="value_mapping_sequence", sequenceName = "value_mapping_seq", allocationSize = 1)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "name",unique=true)
	private String name;
	
	@Lob
	@Column(name = "mapping_template")
	private String mappingTemplate;

			
	public ValueMappingTemplate() {
	}

	public ValueMappingTemplate(Long id) {
		this.id = id;
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
	
	public String getMappingTemplate() {
		return this.mappingTemplate;
	}
	public void setMappingTemplate(String mappingTemplate) {
		this.mappingTemplate = mappingTemplate;
	}
	
	
	
}
