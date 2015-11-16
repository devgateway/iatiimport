package org.devgateway.importtool.model;

public class ImportSummary {
	private long fileCount;
	private long projectCount;
	private long filterCount;
	private int fieldMappingCount;
	private int valueMappingCount;
	
	public long getFileCount() {
		return fileCount;
	}
	public void setFileCount(long fileCount) {
		this.fileCount = fileCount;
	}
	public long getProjectCount() {
		return projectCount;
	}
	public void setProjectCount(long projectCount) {
		this.projectCount = projectCount;
	}
	public long getFilterCount() {
		return filterCount;
	}
	public void setFilterCount(long filterCount) {
		this.filterCount = filterCount;
	}
	public int getFieldMappingCount() {
		return fieldMappingCount;
	}
	public void setFieldMappingCount(int fieldMappingCount) {
		this.fieldMappingCount = fieldMappingCount;
	}
	public int getValueMappingCount() {
		return valueMappingCount;
	}
	public void setValueMappingCount(int valueMappingCount) {
		this.valueMappingCount = valueMappingCount;
	}
	
	
	

}
