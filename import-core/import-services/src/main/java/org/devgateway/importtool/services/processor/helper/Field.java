package org.devgateway.importtool.services.processor.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Field {
	private FieldType type;
	private String fieldName;
	private String displayName;
	private Map<String, String> attributes;
	private List<Field> childFields;
	private List<FieldValue> possibleValues;
	private boolean isMappable;
	private boolean isMultiple;
	private boolean isPercentage;
	private List<String> filters = new ArrayList<String>();

	// These types are silly but needed for now.
	// Refactor to something more reasonable and generic (Make Field an
	// interface and get types to be implementations of it)
	private String subType = "";

	public Field() {
	}

	public Field(String displayName, String fieldName, FieldType type) {
		this.displayName = displayName;
		this.fieldName = fieldName;
		this.type = type;
	}

	public Field(String displayName, String fieldName, FieldType type, boolean isMappable) {
		this(displayName, fieldName, type);
		this.isMappable = isMappable;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getUniqueFieldName() {
		if("".equals(subType))
			return fieldName;
		else
			return fieldName + "_" + subType;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public List<FieldValue> getPossibleValues() {
		return possibleValues;
	}

	// Setter left only for unit tests. See if still needed
	public void setPossibleValues(List<FieldValue> possibleValues) {
		this.possibleValues = possibleValues;
	}

	public List<String> getFilters() {
		return filters;
	}

	public void setFilters(List<String> filters) {
		this.filters = filters;
	}

	public void addFilter(String value) {
		this.filters.add(value);
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public List<Field> getChildFields() {
		return childFields;
	}

	public void setChildFields(List<Field> childFields) {
		this.childFields = childFields;
	}

	public boolean equals(Field f) {
		return f.getFieldName() == this.getFieldName();
	}

	public String toString() {
		return fieldName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isMappable() {
		return isMappable;
	}

	public void setIsMappable(boolean isMappable) {
		this.isMappable = isMappable;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public boolean isMultiple() {
		return isMultiple;
	}

	public void setMultiple(boolean isMultiple) {
		this.isMultiple = isMultiple;
	}

	public boolean isPercentage() {
		return isPercentage;
	}

	public void setPercentage(boolean isPercentage) {
		this.isPercentage = isPercentage;
	}
}
