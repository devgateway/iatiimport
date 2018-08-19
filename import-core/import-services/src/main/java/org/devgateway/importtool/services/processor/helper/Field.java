package org.devgateway.importtool.services.processor.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Field {
	private FieldType type;
	private String fieldName;
	private String displayName;
	//Description will hold the tooltip

	private List<FieldDescription> description;
	private Map<String, String> attributes;
	private Map<String, String> multiLangDisplayName;
	private List<Field> childFields;
	private List<FieldValue> possibleValues;
	private List<FieldValue> usedValues;
	private boolean isMappable;
	private boolean isMultiple;
	private boolean isPercentage;
	private List<String> filters = new ArrayList<String>();
	private List<Field> dependencies = new ArrayList<Field>();
	private boolean isRequired;
	private boolean isExclusive;
	private int length;
	
	private boolean filterRequired;
	// These types are silly but needed for now.
	// Refactor to something more reasonable and generic (Make Field an
	// interface and get types to be implementations of it)
	private String subType = "";
	private String subTypeCode = "";

	public Field() {
		FieldDescription fr =  new FieldDescription("FR","tooltip in fr");
		FieldDescription en =  new FieldDescription("EN","tooltip in fr");
		description = new ArrayList<>();
		description.add(fr);
		description.add(en);

	}

	public Field(String displayName, String fieldName, FieldType type) {
		this();
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
	public List<FieldDescription> getDescription() {
		return description;
	}

	public void setDescription(List<FieldDescription> description) {
		this.description = description;
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

	public String getSubTypeCode() {
		return subTypeCode;
	}

	public void setSubTypeCode(String subTypeCode) {
		this.subTypeCode = subTypeCode;
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

	public List<Field> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<Field> dependencies) {
		this.dependencies = dependencies;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}
	

	public boolean isExclusive() {
		return isExclusive;
	}

	public void setExclusive(boolean isExclusive) {
		this.isExclusive = isExclusive;
	}
	
	public boolean isFilterRequired() {
		return filterRequired;
	}

	public void setFilterRequired(boolean filterRequired) {
		this.filterRequired = filterRequired;
	}

	public List<FieldValue> getUsedValues() {
		return usedValues;
	}

	public void setUsedValues(List<FieldValue> usedValues) {
		this.usedValues = usedValues;
	}

	public Map<String, String> getMultiLangDisplayName() {
		return multiLangDisplayName;
	}

	public void setMultiLangDisplayName(Map<String, String> multiLangDisplayName) {
		this.multiLangDisplayName = multiLangDisplayName;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

}
