package org.devgateway.importtool.services.processor.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InternalDocument {
	private String identifier = "";
	private Map<String, String> stringFields = new HashMap<String, String>();
	private Map<String, Map<String, String>> multilangFields = new HashMap<String, Map<String, String>>();
	private Map<String, FinancialTransaction> transactionFields = new HashMap<String, FinancialTransaction>();
	private Map<String, Date> dateFields = new HashMap<String, Date>();

	public void addStringField(String fieldName, String value) {
		getStringFields().put(fieldName, value);
	}

	public void addMultilangStringField(String fieldName, Map<String, String> multiLangvalue) {
		getMultilangFields().put(fieldName, multiLangvalue);
	}

	public void addTransactionField(String fieldName, FinancialTransaction transaction) {
		getTransactionFields().put(fieldName, transaction);
	}

	public void addDateField(String fieldName, Date activityDate) {
		getDateFields().put(fieldName, activityDate);
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Map<String, String> getStringFields() {
		return stringFields;
	}

	public void setStringFields(Map<String, String> stringFields) {
		this.stringFields = stringFields;
	}

	public Map<String, Map<String, String>> getMultilangFields() {
		return multilangFields;
	}

	public void setMultilangFields(Map<String, Map<String, String>> multilangFields) {
		this.multilangFields = multilangFields;
	}

	public Map<String, FinancialTransaction> getTransactionFields() {
		return transactionFields;
	}

	public void setTransactionFields(Map<String, FinancialTransaction> transactionFields) {
		this.transactionFields = transactionFields;
	}

	public Map<String, Date> getDateFields() {
		return dateFields;
	}

	public void setDateFields(Map<String, Date> dateFields) {
		this.dateFields = dateFields;
	}

}
