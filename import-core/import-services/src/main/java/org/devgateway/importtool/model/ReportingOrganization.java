package org.devgateway.importtool.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.ANY)
@Entity

@Table(name = "reporting_organization")
@NamedQueries({
		@NamedQuery(name = "ReportingOrganization.groupingCriteriaWithUpdates",
				query = "select r from ReportingOrganization r where r.orgId in (SELECT distinct(p.groupingCriteria) " +
						"FROM Project  p WHERE p.lastSyncedOn = ( " +
						"select max" +
						"(p1.lastSyncedOn) from " +
						" Project p1 where p1.projectIdentifier = p.projectIdentifier) and p.projectIdentifier is not" +
						" null and p.status='OK' and p.groupingCriteria is not null" +
						" and p.lastSyncedOn > p.lastUpdatedOn )")

})
public class ReportingOrganization {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "org_id")
	private String orgId; // organization code/ref from IATI
	@Column(name = "name")
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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
