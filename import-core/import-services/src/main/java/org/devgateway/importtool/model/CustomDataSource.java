package org.devgateway.importtool.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@Table(name = "custom_data_source")
public class CustomDataSource {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "custom_datasource_sequence")
	@SequenceGenerator(name="custom_datasource_sequence", sequenceName = "custom_datasource_seq")
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

