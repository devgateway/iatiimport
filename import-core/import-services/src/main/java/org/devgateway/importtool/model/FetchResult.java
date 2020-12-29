package org.devgateway.importtool.model;

import java.util.HashSet;
import java.util.Set;

import org.devgateway.importtool.services.processor.helper.Status;
import org.w3c.dom.Document;

public class FetchResult {
	private Set<String> versions;
	private Document activities;
	private Status status;

	private String message;
	
	public Set<String> getVersions() {
		return versions;
	}
	public void setVersions(Set<String> versions) {
		this.versions = versions;
	}
	public Document getActivities() {
		return activities;
	}
	public void setActivities(Document activities) {
		this.activities = activities;
	}
	public FetchResult(){
		versions = new HashSet<>();
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
