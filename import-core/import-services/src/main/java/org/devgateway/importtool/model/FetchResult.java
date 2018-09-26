package org.devgateway.importtool.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.devgateway.importtool.services.processor.helper.Status;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class FetchResult {
	private Set<String> versions;
	private Document activities;
	private Status status;
	
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
}
