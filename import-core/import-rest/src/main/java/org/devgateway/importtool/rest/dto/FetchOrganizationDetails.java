package org.devgateway.importtool.rest.dto;

import org.devgateway.importtool.model.Project;
import org.devgateway.importtool.services.processor.helper.Status;

import java.util.List;
import java.util.Set;

public class FetchOrganizationDetails {
    private Status status;
    private Set<String> versions;
    private List<Project> projectWithUpdates;

    private String message;

    public FetchOrganizationDetails(){
        status = Status.NOT_STARTED;
    }


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
