package org.devgateway.importtool.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import org.hibernate.annotations.Type;
import org.springframework.hateoas.Identifiable;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@Table(name = "project")

@NamedQueries({
@NamedQuery(name="Project.findProjectLastSyncedDate",
        query="SELECT p FROM Project  p WHERE p.lastSyncedOn = ( select max(p1.lastSyncedOn) from " +
                " Project p1 where p1.projectIdentifier = p.projectIdentifier) and p.projectIdentifier is not" +
                " null and p.status='OK' and p.groupingCriteria is not null" )
})
public class Project implements Identifiable<Long>, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

    @Type(type="text")
	@Column(name = "title")
    private String title;

	@Column(name = "status")
	private String status;

	@Column(name = "notes")
    @Type(type="text")
    private String notes;

	@Column(name ="project_identifier")
	private String projectIdentifier;

	@Column(name ="last_synced_on")
	private Date lastSyncedOn;

	@Column(name ="last_updated_on")
	private Date lastUpdatedOn;

	@Column(name ="grouping_criteria")
	private String groupingCriteria;

	@ManyToOne
    @JoinColumn(name="file_id", referencedColumnName="id" )
    private File file;

	public Project() {
	}

	public Project(Long id) {
		this.id = id;
	}

	public Long getId() {
		return this.id;
	}
	
	public String getTitle() {
		return this.title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStatus() {
		return this.status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNotes() {
		return this.notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getProjectIdentifier() {
		return projectIdentifier;
	}

	public void setProjectIdentifier(String projectIdentifier) {
		this.projectIdentifier = projectIdentifier;
	}

	public Date getLastSyncedOn() {
		return lastSyncedOn;
	}

	public void setLastSyncedOn(Date lastSyncedOn) {
		this.lastSyncedOn = lastSyncedOn;
	}

	public Date getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getGroupingCriteria() {
		return groupingCriteria;
	}

	public void setGroupingCriteria(String groupingCriteria) {
		this.groupingCriteria = groupingCriteria;
	}

	public File getFile() {
		return this.file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
}
