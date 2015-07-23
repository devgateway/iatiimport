package org.devgateway.importtool.services.processor.helper;

import java.util.HashMap;
import java.util.Map;

public class FieldValueMapping {
	private Field sourceField;
	private Field destinationField;
	private Map<Integer, Integer> valueIndexMapping = new HashMap<Integer, Integer>();

	public Field getSourceField() {
		return sourceField;
	}

	public void setSourceField(Field sourceField) {
		this.sourceField = sourceField;
	}

	public Field getDestinationField() {
		return destinationField;
	}

	public void setDestinationField(Field destinationField) {
		this.destinationField = destinationField;
	}

	public Map<Integer, Integer> getValueIndexMapping() {
		return valueIndexMapping;
	}

	public void setValueIndexMapping(Map<Integer, Integer> mapping) {
		this.valueIndexMapping = mapping;
	}
	

}
