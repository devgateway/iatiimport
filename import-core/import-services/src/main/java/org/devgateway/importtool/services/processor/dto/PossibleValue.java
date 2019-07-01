package org.devgateway.importtool.services.processor.dto;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;



/**
 * Holds one possible value for a field. Immutable.
 * @author Octavian Ciubotaru
 */
public class PossibleValue {

    private  Object id;
    private  String value;

    @JsonProperty("translated-value")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    private  Map<String, String> translatedValues;

    @JsonProperty("extra_info")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private Map<Object, Object> extraInfo;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    private  List<PossibleValue> children;

    public PossibleValue(){

    }
    public Object getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public List<PossibleValue> getChildren() {
        return children;
    }

    public Map<Object,Object> getExtraInfo() {
        return extraInfo;
    }

    public Map<String, String> getTranslatedValues() {
        return translatedValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PossibleValue that = (PossibleValue) o;
        return Objects.equals(id, that.id)
                && Objects.equals(value, that.value)
                && Objects.equals(extraInfo, that.extraInfo)
                && Objects.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, extraInfo, children);
    }


    @Override
    public String toString() {
        return "PossibleValue{"
                + "id=" + id
                + ", value='" + value + '\''
                + ", hasExtraInfo=" + (extraInfo != null)
                + ", hasChildren=" + (!children.isEmpty())
                + '}';
    }
}