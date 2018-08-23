package org.devgateway.importtool.rest.dto;

import org.devgateway.importtool.model.Project;

import java.util.List;
import java.util.Set;

public class FetchOrganizationDetails {
    Set<String> versions;
    List<Project> projectWithUpdates;

    public Set<String> getVersions() {
        return versions;
    }

    public void setVersions(Set<String> versions) {
        this.versions = versions;
    }

    public List<Project> getProjectWithUpdates() {
        return projectWithUpdates;
    }

    public void setProjectWithUpdates(List<Project> projectWithUpdates) {
        this.projectWithUpdates = projectWithUpdates;
    }
}
