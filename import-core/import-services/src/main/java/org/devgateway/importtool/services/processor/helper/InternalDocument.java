package org.devgateway.importtool.services.processor.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InternalDocument {
	private String identifier = "";
	private String grouping = "";
	private Map<String, String> stringFields = new HashMap<String, String>();
	private Map<String, String[]> stringMultiFields = new HashMap<String, String[]>();
	private Map<String, Map<String, String>> multilangFields = new HashMap<String, Map<String, String>>();
	private Map<String, Map<String, String>> organizationFields = new HashMap<String, Map<String, String>>();
	private Map<String, Map<String, String>> transactionFields = new HashMap<String, Map<String, String>>();
	private Map<String, Map<String, String>> contactFields = new HashMap<String, Map<String, String>>();
	private Map<String, Date> dateFields = new HashMap<String, Date>();	
	private Map<String, List<FieldValue>> recepientCountryFields = new HashMap<String, List<FieldValue>>();	
	private boolean titleDuplicate = false;

	/**
	 * Some of the fields may have translations in other language.
	 */
	private List<Translation> translations = new ArrayList<>();

	

	public boolean isTitleDuplicate() {
		return titleDuplicate;
	}

	public void setTitleDuplicate(boolean titleDuplicate) {
		this.titleDuplicate = titleDuplicate;
	}

	public Map<String, List<FieldValue>> getRecepientCountryFields() {
		return recepientCountryFields;
	}

	public void setRecepientCountryFields(
			Map<String, List<FieldValue>> recepientCountryFields) {
		this.recepientCountryFields = recepientCountryFields;
	}




	private boolean allowEdit = false;

	public boolean isAllowEdit() {
		return allowEdit;
	}

	public void setAllowEdit(boolean allowEdit) {
		this.allowEdit = allowEdit;
	}

	public void addStringField(String fieldName, String value) {
		getStringFields().put(fieldName, value);
	}
	
	public void addStringMultiField(String fieldName, String[] value) {
		getStringMultiFields().put(fieldName, value);
	}

	public void addMultilangStringField(String fieldName, Map<String, String> multiLangvalue) {
		getMultilangFields().put(fieldName, multiLangvalue);
	}

	public void addTransactionField(String fieldName, Map<String, String> transaction) {
		getTransactionFields().put(fieldName, transaction);
	}

	
	
	public void addContactFields(String fieldName, Map<String, String> contact) {
		getContactFields().put(fieldName, contact);
	}
	
	public void addOrganizationField(String fieldName, Map<String, String> organization) {
		getOrganizationFields().put(fieldName, organization);
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

	public Map<String, Map<String, String>> getTransactionFields() {
		return transactionFields;
	}

	public void setTransactionFields(Map<String, Map<String, String>> transactionFields) {
		this.transactionFields = transactionFields;
	}

	public Map<String, Date> getDateFields() {
		return dateFields;
	}

	public void setDateFields(Map<String, Date> dateFields) {
		this.dateFields = dateFields;
	}
	public Map<String, String[]> getStringMultiFields() {
		return stringMultiFields;
	}
	public void setStringMultiFields(Map<String, String[]> stringMultiFields) {
		this.stringMultiFields = stringMultiFields;
	}

	public Map<String, Map<String, String>> getOrganizationFields() {
		return organizationFields;
	}

	public void setOrganizationFields(Map<String, Map<String, String>> organizationFields) {
		this.organizationFields = organizationFields;
	}

	public Map<String, Map<String, String>> getContactFields() {
		return contactFields;
	}

	public void setContactFields(Map<String, Map<String, String>> contactFields) {
		this.contactFields = contactFields;
	}


	

	public void addRecepientCountryFields(String fieldName, List<FieldValue> value) {
		this.getRecepientCountryFields().put(fieldName, value);
	}

	public String getGrouping() {
		return grouping;
	}

	public void setGrouping(String grouping) {
		this.grouping = grouping;
	}

	public List<Translation> getTranslations() {
		return translations;
	}

	public void setTranslations(List<Translation> translations) {
		this.translations = translations;
	}
}
