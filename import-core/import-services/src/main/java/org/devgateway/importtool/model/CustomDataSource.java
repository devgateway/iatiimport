package org.devgateway.importtool.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@Table(name = "custom_data_source")
public class CustomDataSource {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;	
	@Column(name = "url", length = 512)
	private String url;	
	
	@Column(name = "reporting_org_id")
	private String reportingOrgId;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getReportingOrgId() {
		return reportingOrgId;
	}
	public void setReportingOrgId(String reportingOrgId) {
		this.reportingOrgId = reportingOrgId;
	}	
}

