package org.devgateway.importtool.services;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.hateoas.Identifiable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@Table(name = "project")
public class Project implements Identifiable<Long>, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "title")
	private String title;

	@Column(name = "status")
	private String status;
	
	@Column(name = "notes")
	private String notes;
	
		
	@ManyToOne
    @JoinColumn(name="file_id", referencedColumnName="id")
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

	public File getFile() {
		return this.file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
}
