package org.devgateway.importtool.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;
import org.springframework.hateoas.Identifiable;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@Table(name = "uploaded_file")
public class File implements Identifiable<Long>, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_sequence")
	@SequenceGenerator(name = "file_sequence", sequenceName = "uploaded_file_seq", allocationSize = 1)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "file_name")
	private String fileName;

	@Lob
	@Column(name = "file_data")
	private byte[] data;

	@Column(name = "author")
	private String author;

	@Type(type = "pg-uuid")
	@Column(name = "session_id")
	private UUID sessionId;

	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "is_valid")
	private Boolean valid;

	public File() {
	}

	public File(Long id) {
		this.id = id;
	}

	public Long getId() {
		return this.id;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(55, 113).append(this.id)
				.append(this.getFileName()).toHashCode();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Boolean isValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public UUID getSessionId() {
		return sessionId;
	}

	public void setSessionId(UUID sessionId) {
		this.sessionId = sessionId;
	}

}
