package org.devgateway.importtool.model;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.hateoas.Identifiable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@JsonAutoDetect (fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@Table (name = "user_account")
// FIXME This is not in use remove before closing the feature
// FIXME Remember to drop the table
public class User implements Identifiable<Long>, Serializable    {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
	@SequenceGenerator(name="user_sequence", sequenceName = "user_seq")
	@Column (name = "id", unique = true, nullable = false)
	private Long id;

	@Column (name = "first_name")
	private String firstName;

	@Column (name = "last_name")
	private String lastName;

	@Column (name = "user_name")
	private String username;

	@Column (name = "pass_word")
	private String password;

	@Column (name = "enabled")
	private boolean enabled;

	@Column (name = "created_date")
	private Date createdDate;

	@Column (name = "is_admin")
	private boolean isAdmin;

	public User() {
	}

	public User(Long id) {
		this.id = id;
	}

	public User(User usr) {
		this(usr.id, usr.username, usr.firstName, usr.lastName);
		this.enabled = usr.enabled;
		this.password = usr.password;
		this.enabled = usr.enabled;
		this.setCreatedDate(usr.getCreatedDate());
		this.setAdmin(usr.isAdmin());
	}

	public User(Long id, String u, String f, String l) {
		this(u, null, f, l);
		this.id = id;
	}

	public User(String username, String password, String firstName, String lastName) {
		this.firstName = firstName;
		this.password = password;
		this.lastName = lastName;
		this.username = username;
		this.setCreatedDate(new java.util.Date());
		this.enabled = true;

	}

	public Long getId() {
		return this.id;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(55, 113).append(this.id).append(this.firstName).append(
				this.lastName).append(this.password).append(this.username).toHashCode();
	}

	@Override
	public boolean equals(Object o) {
		User other = (User) o;
		return new EqualsBuilder().append(other.firstName, this.firstName).append(
				other.lastName, this.lastName).append(other.password, this.password).append(
				other.username, this.username).append(other.id, this.id).isEquals();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

}
