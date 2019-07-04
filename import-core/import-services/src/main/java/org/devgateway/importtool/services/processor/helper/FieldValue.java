package org.devgateway.importtool.services.processor.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FieldValue {
	private int index;
	private String code;
	private String value;
	private boolean selected = false;
	private String percentage = "";
	private Map<Object, Object> properties;
	private Map<String, String> translatedValue;
	
	public FieldValue() {
		 this.properties = new Properties();
		 this.translatedValue = new HashMap<>();
	}
	
	public String getCode() {
		return code;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String getPercentage() {
		return percentage;
	}
	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString() {
		return this.value + " - " + this.code;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public Map<Object,Object> getProperties() {
		return properties;
	}
	public void setProperties(Map<Object,Object> properties) {
		this.properties = properties;
	}

    public Map<String, String> getTranslatedValue() {
        return translatedValue;
    }

    public void setTranslatedValue(Map<String, String> translatedValue) {
        this.translatedValue = translatedValue;
    }


}
