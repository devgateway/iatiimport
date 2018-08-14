package org.devgateway.importtool.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@Table(name = "data_source")
public class DataSource {	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	
	
	@Column(name = "default_url", length = 512)
	private String defaultUrl;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "data_source_id")
	private List<CustomDataSource> customDataSources;
	
	public List<CustomDataSource> getCustomDataSources() {
		return customDataSources;
	}
	
	public void setCustomDataSources(List<CustomDataSource> customDataSources) {
		if (this.customDataSources == null) {
			this.customDataSources  = new ArrayList<>();
		}
		this.customDataSources.clear();	
		if (customDataSources != null) {
			this.customDataSources.addAll(customDataSources);
		}		
	}
	
	public String getDefaultUrl() {
		return defaultUrl;
	}
	
	public void setDefaultUrl(String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}
}
