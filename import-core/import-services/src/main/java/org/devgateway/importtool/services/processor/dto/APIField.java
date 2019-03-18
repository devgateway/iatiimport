package org.devgateway.importtool.services.processor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.log4j.Logger;
import org.devgateway.importtool.services.dto.JsonBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"field_name", "apiType", "field_label", "required", "importable", "dependencies", "id_only",
        "multiple_values", "percentage_constraint", "unique_constraint", "tree_collection", "translatable", "regex_pattern",
        "regex_constraint", "field_length", "size_limit"})
/**
 * Class coming from AMP authored by Octavian Ciubotaru
 * We can probably make it a separate jar if needed to avoid copy & pasting
 */
public class APIField {
    private static final Logger logger = Logger.getLogger(APIField.class);

    @JsonProperty(JsonConstants.FIELD_NAME)
    private String fieldName;

    @JsonUnwrapped
    private APIType apiType;

    @JsonProperty(JsonConstants.FIELD_LABEL)
    private JsonBean fieldLabel;

    @JsonProperty(JsonConstants.REQUIRED)
    private String required;

    @JsonProperty(JsonConstants.ID_ONLY)
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    private boolean idOnly;

    @JsonProperty(JsonConstants.IMPORTABLE)
    private Boolean importable;

    @JsonProperty(JsonConstants.TRANSLATABLE)
    private Boolean translatable;

    @JsonProperty(JsonConstants.MULTIPLE_VALUES)
    private Boolean multipleValues;

    @JsonProperty(JsonConstants.UNIQUE_CONSTRAINT)
    private String uniqueConstraint;

    @JsonProperty(JsonConstants.PERCENTAGE_CONSTRAINT)
    private String percentageConstraint;

    @JsonProperty(JsonConstants.TREE_COLLECTION_CONSTRAINT)
    private Boolean treeCollectionConstraint;

    @JsonProperty(JsonConstants.FIELD_LENGTH)
    private Integer fieldLength;

    @JsonProperty(JsonConstants.CHILDREN)
    private List<APIField> children = new ArrayList<>();

    @JsonProperty(JsonConstants.DEPENDENCIES)
    private List<String> dependencies;

    @JsonProperty(JsonConstants.REGEX_PATTERN)
    private String regexPattern;

    @JsonProperty(JsonConstants.PERCENTAGE)
    private Boolean percentage;

    @JsonProperty(JsonConstants.SIZE_LIMIT)
    private Integer sizeLimit;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public APIType getApiType() {
        return apiType;
    }

    public void setApiType(APIType apiType) {
        this.apiType = apiType;
    }

    public JsonBean getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(JsonBean fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public boolean isIdOnly() {
        return idOnly;
    }

    public void setIdOnly(boolean idOnly) {
        this.idOnly = idOnly;
    }

    public Boolean getImportable() {
        return importable;
    }

    public void setImportable(Boolean importable) {
        this.importable = importable;
    }

    public Boolean getTranslatable() {
        return translatable;
    }

    public void setTranslatable(Boolean translatable) {
        this.translatable = translatable;
    }

    public Boolean getMultipleValues() {
        return multipleValues;
    }

    public void setMultipleValues(Boolean multipleValues) {
        this.multipleValues = multipleValues;
    }

    public String getUniqueConstraint() {
        return uniqueConstraint;
    }

    public void setUniqueConstraint(String uniqueConstraint) {
        this.uniqueConstraint = uniqueConstraint;
    }

    public String getPercentageConstraint() {
        return percentageConstraint;
    }

    public void setPercentageConstraint(String percentageConstraint) {
        this.percentageConstraint = percentageConstraint;
    }

    public Boolean getTreeCollectionConstraint() {
        return treeCollectionConstraint;
    }

    public void setTreeCollectionConstraint(Boolean treeCollectionConstraint) {
        this.treeCollectionConstraint = treeCollectionConstraint;
    }

    public Integer getFieldLength() {
        return fieldLength;
    }

    public void setFieldLength(Integer fieldLength) {
        this.fieldLength = fieldLength;
    }

    public List<APIField> getChildren() {
        return children;
    }

    public void setChildren(List<APIField> children) {
        this.children = children;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public String getRegexPattern() {
        return regexPattern;
    }

    public void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
    }

    public Boolean getPercentage() {
        return percentage;
    }

    public void setPercentage(Boolean percentage) {
        this.percentage = percentage;
    }

    public Integer getSizeLimit() {
        return sizeLimit;
    }

    public void setSizeLimit(Integer sizeLimit) {
        this.sizeLimit = sizeLimit;
    }
}
