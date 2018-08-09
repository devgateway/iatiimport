package org.devgateway.importtool.model;

import java.util.Set;

import org.w3c.dom.NodeList;

public class FetchResult {
	private Set<String> versions;
	private NodeList activities;
	
	public Set<String> getVersions() {
		return versions;
	}
	public void setVersions(Set<String> versions) {
		this.versions = versions;
	}
	public NodeList getActivities() {
		return activities;
	}
	public void setActivities(NodeList activities) {
		this.activities = activities;
	}
}
