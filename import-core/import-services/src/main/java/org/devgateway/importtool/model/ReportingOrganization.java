package org.devgateway.importtool.model;

public class ReportingOrganization {
	private String orgId; // organization code/ref from IATI
	private String name;
	
	public ReportingOrganization(String orgId, String name) {
		super();
		this.orgId = orgId;
		this.name = name;
	}
	
	public ReportingOrganization() {
	}
	
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
