package org.devgateway.importtool.services;

public class ImportSummary {
	private Long fileCount;
	private Integer projectCount;
	private Long filterCount;
	private Integer fieldMappingCount;
	private Integer valueMappingCount;
	
	public Long getFileCount() {
		return fileCount;
	}
	public void setFileCount(Long fileCount) {
		this.fileCount = fileCount;
	}
	public Integer getProjectCount() {
		return projectCount;
	}
	public void setProjectCount(Integer projectCount) {
		this.projectCount = projectCount;
	}
	public Long getFilterCount() {
		return filterCount;
	}
	public void setFilterCount(Long filterCount) {
		this.filterCount = filterCount;
	}
	public Integer getFieldMappingCount() {
		return fieldMappingCount;
	}
	public void setFieldMappingCount(Integer fieldMappingCount) {
		this.fieldMappingCount = fieldMappingCount;
	}
	public Integer getValueMappingCount() {
		return valueMappingCount;
	}
	public void setValueMappingCount(Integer valueMappingCount) {
		this.valueMappingCount = valueMappingCount;
	}
	
	
	

}
