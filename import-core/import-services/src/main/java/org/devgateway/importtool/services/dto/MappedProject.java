package org.devgateway.importtool.services.dto;

public class MappedProject {

    private String projectIdentifier;
    private String groupingCriteria;
    private JsonBean project;

    public String getProjectIdentifier() {
        return projectIdentifier;
    }

    public void setProjectIdentifier(String projectIdentifier) {
        this.projectIdentifier = projectIdentifier;
    }

    public String getGroupingCriteria() {
        return groupingCriteria;
    }

    public void setGroupingCriteria(String groupingCriteria) {
        this.groupingCriteria = groupingCriteria;
    }

    public JsonBean getProject() {
        return project;
    }

    public void setProject(JsonBean project) {
        this.project = project;
    }
}
