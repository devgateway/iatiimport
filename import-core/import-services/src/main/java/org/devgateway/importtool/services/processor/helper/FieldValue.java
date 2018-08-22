package org.devgateway.importtool.services.processor.helper;

import java.util.Map;
import java.util.Properties;

public class FieldValue {
	private int index;
	private String code;
	private String value;
	private boolean selected = false;
	private String percentage = "";
	private Map<String,String> description;
	private Properties properties;

	
	public FieldValue() {
		 this.properties = new Properties();
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
	public Properties getProperties() {
		return properties;
	}
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public Map<String, String> getDescription() {
		return description;
	}

	public void setDescription(Map<String, String> description) {
		this.description = description;
	}
}
