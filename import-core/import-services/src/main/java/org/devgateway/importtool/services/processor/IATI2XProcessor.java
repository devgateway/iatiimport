package org.devgateway.importtool.services.processor;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.model.Language;
import org.devgateway.importtool.services.processor.helper.ActionStatus;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldType;
import org.devgateway.importtool.services.processor.helper.FieldValue;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.devgateway.importtool.services.processor.helper.InternalDocument;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Component("IATI2X")
@Scope("session")
public class IATI2XProcessor implements ISourceProcessor {

	private static final String ISO_DATE = "yyyy-MM-dd";

	private Log log = LogFactory.getLog(getClass());

	// Global Lists for fields and the filters
	private List<Field> fieldList = new ArrayList<Field>();
	private List<Field> filterFieldList = new ArrayList<Field>();
	private List<String> languageList = null;
	// Field names on the source document that hold key information
	private String DEFAULT_ID_FIELD = "iati-identifier";
	private String DEFAULT_TITLE_FIELD = "title";
	protected String PROCESSOR_VERSION = "";
	private String defaultLanguage = "";
	private String defaultCurrency = "";	
	protected String descriptiveName = "";
	protected String codelistPath = "";
	public String getCodelistPath() {
		return codelistPath;
	}


	public void setCodelistPath(String codelistPath) {
		this.codelistPath = codelistPath;
	}


	public String getPropertiesFile() {
		return propertiesFile;
	}


	public void setPropertiesFile(String propertiesFile) {
		this.propertiesFile = propertiesFile;
	}


	protected String propertiesFile = "";

	private List<Language> filterLanguages = new ArrayList<Language>();
	
	// XML Document that will hold the entire imported file
	private Document doc;
	
	private ActionStatus actionStatus;

    public IATI2XProcessor(){
	    
	}

	public Document getDoc() {
		return doc;
	}
	
	
	public ActionStatus getActionStatus() {
		return actionStatus;
	}


	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}


	// Map that holds information about how the field names map to code lists
	private static Map<String, String> mappingNameFile = new HashMap<String, String>();
	static {
		mappingNameFile.put("activity-status", "ActivityStatus");
		mappingNameFile.put("activity-scope", "ActivityScope");
		mappingNameFile.put("collaboration-type", "CollaborationType");
		mappingNameFile.put("recipient-country", "Country");
		mappingNameFile.put("recipient-region", "Region");
		mappingNameFile.put("default-aid-type", "AidType");
		mappingNameFile.put("default-finance-type", "FinanceType");
		mappingNameFile.put("default-flow-type", "FlowType");
		mappingNameFile.put("default-tied-status", "TiedStatus");
		mappingNameFile.put("policy-marker", "PolicyMarker");
		mappingNameFile.put("sector", "Sector");
	}

	//TODO: Extract from codelists
	private static Map<String, String> transactionTypes = new HashMap<String, String>();
	static {
		transactionTypes.put("C", "2");
		transactionTypes.put("D", "3");
	}
	
	private static Map<String, String> dateTypes = new HashMap<String, String>();
	static {
		dateTypes.put("start-planned", "1");
		dateTypes.put("start-actual", "2");
		dateTypes.put("end-planned", "3");
		dateTypes.put("end-actual", "4");
	}
		
	
	
	protected void configureDefaults(){
		InputStream propsStream = this.getClass().getResourceAsStream(this.getPropertiesFile());
		Properties properties = new Properties();		
		try {
			properties.load(propsStream);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		defaultLanguage = properties.getProperty("default_language");
		defaultCurrency = properties.getProperty("default_currency");
	}

	@Override
	public List<Field> getFields() {
		return fieldList;
	}

	@Override
	public void setInput(InputStream input) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringElementContentWhitespace(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			this.doc = builder.parse(input);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			log.error("IOException Parsing Source File: " + e);
		}
	}

	@Override
	public List<InternalDocument> getDocuments() throws Exception {
		List<InternalDocument> docList = new ArrayList<InternalDocument>();
		docList = extractDocuments(doc);
		return docList;
	}

	@Override
	public String getIdField() {
		return DEFAULT_ID_FIELD;
	}

	@Override
	public String getTitleField() {
		return DEFAULT_TITLE_FIELD;
	}

	@Override
	public List<String> getLanguages() {
		if (languageList != null) {
			return languageList;
		}
		// The strategy for getting the languages included in the file is to
		// parse the file, from the xml:lang attribute
		Document doc = this.doc;
		List<String> list = new ArrayList<String>();
		if (doc == null)
			return list;
		// Get root language
		List<String> activityLanguageList = extractLanguage(doc.getElementsByTagName("iati-activity"));

		List<String> narrativeLanguageList = extractLanguage(doc.getElementsByTagName("narrative"));

		Set<String> set = new HashSet<String>();
		set.addAll(activityLanguageList);
		set.addAll(narrativeLanguageList);
		list.addAll(set);
		languageList = list;
				
		return list;
	}

	@Override
	public List<Language> getFilterLanguages() {
		if(this.filterLanguages.size() == 0){
			List<Language> listLanguages = new ArrayList<Language>();
			this.getLanguages().stream().forEach(lang -> {
				Locale tmp = new Locale(lang);
				listLanguages.add(new Language(tmp.getLanguage(), tmp.getDisplayLanguage()));
			});
			this.setFilterLanguages(listLanguages);			
		}
		return this.filterLanguages;
	}
	@Override
	public void setFilterLanguages(List<Language> filterLanguages) {
		this.filterLanguages = filterLanguages;
	}
	
	@Override
	public List<Field> getFilterFields() {
		Document doc = this.doc;
		if (doc == null)
			return filterFieldList;

		// List<Field> filterFieldListReduced = new ArrayList<Field>();
		for (Field field : filterFieldList) {
			NodeList nodeList = doc.getElementsByTagName(field.getFieldName());
			List<FieldValue> reducedPossibleValues = new ArrayList<FieldValue>();
			switch (field.getType()) {
			case RECIPIENT_COUNTRY:
			case LIST:
				if (nodeList.getLength() > 0) {					
					for (int i = 0; i < nodeList.getLength(); i++) {
						Element fieldElement = (Element) nodeList.item(i);
						final String codeValue = fieldElement.getAttribute("code");						
						Optional<FieldValue> fieldValue = field.getPossibleValues().stream().filter(n -> {
							return n.getCode().equals(codeValue);
						}).findFirst();
						if(!fieldValue.isPresent() && !codeValue.isEmpty()){
							FieldValue newfv = new FieldValue();
							final String name = fieldElement.getElementsByTagName("narrative").item(0) != null ? fieldElement.getElementsByTagName("narrative").item(0).getTextContent() : "";
							newfv.setCode(codeValue);
							newfv.setValue(name);
							newfv.setIndex(field.getPossibleValues().size());
							field.getPossibleValues().add(newfv);
							if (!reducedPossibleValues.stream().filter(n -> {
								return n.getCode().equals(newfv.getCode());
							}).findFirst().isPresent()) {
								reducedPossibleValues.add(newfv);
							}
						}
						if (fieldValue.isPresent() && !reducedPossibleValues.stream().filter(n -> {
							return n.getCode().equals(fieldValue.get().getCode());
						}).findFirst().isPresent()) {
							reducedPossibleValues.add(fieldValue.get());
						}
					}
				}
				break;
			default:
				break;
			}
			field.setPossibleValues(reducedPossibleValues);
		}
		return filterFieldList;
	}

	@Override
	public String getDescriptiveName() {
		return this.descriptiveName;
	}

	@Override
	public void setFilterFields(List<Field> fields) {
		filterFieldList = fields;
	}

	// Private methods. Includes methods to get values for the different types
	// of data being managed.

	private List<FieldValue> getCodeListValues(String codeListName) {
		return getCodeListValues(codeListName, false);
	}

	private List<FieldValue> getCodeListValues(String codeListName, Boolean concatenate) {
		String standardFieldName = mappingNameFile.get(codeListName);
		List<FieldValue> possibleValues = new ArrayList<FieldValue>();
		InputStream is = this.getClass().getResourceAsStream(this.getCodelistPath() + standardFieldName + ".xml");
		if (is != null) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setValidating(false);
				factory.setIgnoringElementContentWhitespace(true);
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(is);
				NodeList nodeList = doc.getElementsByTagName("codelist-item");
				int index = 0;
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						Element codeElement = (Element) element.getElementsByTagName("code").item(0);
						String code = codeElement.getChildNodes().item(0).getNodeValue();

						Element nameElement = (Element) element.getElementsByTagName("name").item(0);
						NodeList narrativeNodeList = nameElement.getElementsByTagName("narrative");
						Element narrativeElement = (Element) narrativeNodeList.item(0);
						String name = narrativeElement.getChildNodes().item(0).getNodeValue();

						FieldValue fv = new FieldValue();
						fv.setIndex(index++);
						fv.setCode(code);
						if (concatenate) {
							fv.setValue(code + " - " + name);
						} else {
							fv.setValue(name);
						}

						possibleValues.add(fv);
					}
				}
			} catch (ParserConfigurationException | SAXException | IOException e) {
				log.error("IOException Parsing Source File: " + e);
			}
		}
		return possibleValues;
	}
	
	private NodeList getActivities() throws XPathExpressionException{		
		XPath xPath = XPathFactory.newInstance().newXPath();
		final StringBuilder query = new StringBuilder("/iati-activities/iati-activity[");		
		Field countryField = this.getFields().stream().filter(f -> {
			return f.getType().equals(FieldType.RECIPIENT_COUNTRY);
		}).findFirst().get();
		
		Field countryFilters = filterFieldList.stream().filter(n -> {
			return countryField.getFieldName().equals(n.getFieldName());
		}).findFirst().get();
		
		if(countryFilters.getFilters().size() > 0){
			String selectedCountry  = countryFilters.getFilters().get(0);
			query.append("recipient-country[@code='"+ selectedCountry +"']");
		}	
		
		
		this.getFields().forEach(field -> {			
			if(field.getType().equals(FieldType.LIST)){				
				Field filter = filterFieldList.stream().filter(n -> {
					return field.getFieldName().equals(n.getFieldName());
				}).findFirst().get();
				
				if(filter.getFilters().size() > 0){		
					if(!("/iati-activities/iati-activity[".equals(query.toString()))){					
						query.append(" and ");	
					}
					query.append(field.getFieldName() + "[");
					for (int i = 0;i < filter.getFilters().size(); i++) {
						String value = filter.getFilters().get(i);
						if(i > 0){
							query.append(" or ");
						}
						query.append("@code='" + value + "'");
					}					
					query.append("]");
				}	
			}			
			
		});		
		if(!("/iati-activities/iati-activity[".equals(query.toString()))){					
			query.append("]");	
		}else{
			query.setLength(query.length() - 1);
		}
		
		NodeList activities = (NodeList)xPath.compile(query.toString()).evaluate(this.getDoc(), XPathConstants.NODESET);
		return activities;
	 }

	private List<InternalDocument> extractDocuments(Document doc) throws Exception {
		// Extract global values

		XPath xPath = XPathFactory.newInstance().newXPath();		
		NodeList nodeList = getActivities();
		List<InternalDocument> list = new ArrayList<InternalDocument>();
		actionStatus.setTotal(Long.valueOf(nodeList.getLength()));	
		this.clearUsedValues();
		for (int i = 0; i < nodeList.getLength(); i++) {
			actionStatus.incrementProcessed();
			InternalDocument document = new InternalDocument();
			Element element = (Element) nodeList.item(i);
			String currency = !("".equals(element.getAttribute("default-currency"))) ? element.getAttribute("default-currency") : this.defaultCurrency;			
			document.addStringField("default-currency", currency);
			String defaultLanguageCode = !("".equals(element.getAttribute("xml:lang"))) ? element.getAttribute("xml:lang") : this.defaultLanguage;
			NodeList fieldNodeList;			
			for (Field field : getFields()) {
				switch (field.getType()) {
				case LIST:
					if (field.isMultiple()) {
						fieldNodeList = element.getElementsByTagName(field.getFieldName());						
						List <String> codes = new ArrayList<String>(); 
						for (int j = 0; j < fieldNodeList.getLength(); j++) {
							Element fieldElement = (Element) fieldNodeList.item(j);
							String code = fieldElement.getAttribute("code");								
							if(!code.isEmpty()){
								codes.add(code);
								Optional<FieldValue> foundfv = field.getPossibleValues().stream().filter( n -> {return n.getCode().equals(code);}).findFirst();
								FieldValue fv  = foundfv.isPresent() ? foundfv.get() : null;
								if(fv != null && fv.isSelected() != true){
									fv.setSelected(true);
								}
							}				
						}
						if(!codes.isEmpty()){
							String[] codeValues = codes.stream().toArray(String[]::new);						
							document.addStringMultiField(field.getFieldName(), codeValues);
						}
					} else {						
						fieldNodeList = element.getElementsByTagName(field.getFieldName());
						if (fieldNodeList.getLength() > 0 && fieldNodeList.getLength() == 1) {							
							Element fieldElement = (Element) fieldNodeList.item(0);
							String codeValue = fieldElement.getAttribute("code");							
							if(!codeValue.isEmpty()){
								Optional<FieldValue> foundfv = field.getPossibleValues().stream().filter( n -> {
									return n.getCode().equals(codeValue);
								}).findFirst();
								FieldValue fv  = foundfv.isPresent() ? foundfv.get() : null;
								if(fv != null && fv.isSelected() != true){
									fv.setSelected(true);
								}
								document.addStringField(field.getFieldName(), codeValue);
							}														
						}						
					}
					break;
				case RECIPIENT_COUNTRY:
					Field filtersField = filterFieldList.stream().filter(n -> {
						return field.getFieldName().equals(n.getFieldName());
					}).findFirst().get();				
					
					fieldNodeList = element.getElementsByTagName(field.getFieldName());					
					List<FieldValue> recipients = new ArrayList<FieldValue>();
					for (int j = 0; j < fieldNodeList.getLength(); j++) {
						Element fieldElement = (Element) fieldNodeList.item(j);
						FieldValue recipient = new FieldValue();	
						String code = fieldElement.getAttribute("code");
						boolean includeCountry = includedByFilter(filtersField.getFilters(), code);
						if(includeCountry){
							recipient.setCode(code);
							Optional<FieldValue> fieldValue = field.getPossibleValues().stream().filter(f -> f.getCode().equals(code)).findFirst();
							if(fieldValue.isPresent()){
								recipient.setValue(fieldValue.get().getValue());
							}
							recipient.setPercentage(fieldElement.getAttribute("percentage"));	
							recipients.add(recipient);	
						}																										
					}					
					document.addRecepientCountryFields(field.getFieldName(), recipients);					
					break;
				case STRING:
					String stringValue = "";
					fieldNodeList = element.getElementsByTagName(field.getFieldName());
					if (fieldNodeList.getLength() > 0 && fieldNodeList.getLength() == 1) {
						Element fieldElement = (Element) fieldNodeList.item(0);
						if (fieldElement.getChildNodes().getLength() == 1) {
							stringValue = fieldElement.getChildNodes().item(0).getNodeValue();
						} else {
							stringValue = "";
						}
					}
					document.addStringField(field.getFieldName(), stringValue);
					break;
				case ORGANIZATION:				
					fieldNodeList = element.getElementsByTagName(field.getFieldName());										
					if (fieldNodeList.getLength() > 0) {
						for (int j = 0; j < fieldNodeList.getLength(); j++) {
							Element fieldElement = (Element) fieldNodeList.item(j);							
							if (fieldElement.getAttribute("role").equals(field.getSubType()) || fieldElement.getAttribute("role").equals(field.getSubTypeCode())) {							
								final String stringOrgValue = fieldElement.getElementsByTagName("narrative").item(0) != null ? fieldElement.getElementsByTagName("narrative").item(0).getTextContent() : "";
								final String ref = fieldElement.getAttribute("ref");
								Map<String, String> orgFields = new HashMap<String, String>();
								orgFields.put("value", stringOrgValue);
								orgFields.put("role", field.getSubType());
								orgFields.put("ref", ref);
								orgFields.put("type", fieldElement.getAttribute("type"));
								FieldValue fv = new FieldValue();
								if(stringOrgValue != null && !stringOrgValue.isEmpty() ){
									fv.setCode(stringOrgValue);
									fv.setValue(stringOrgValue);	
								}else{
									fv.setCode(ref);
									fv.setValue(ref);
								}
								fv.setSelected(true);
								int index = field.getPossibleValues() == null ? 0 : field.getPossibleValues().size();
								fv.setIndex(index);
								if (field.getPossibleValues() == null) {
									field.setPossibleValues(new ArrayList<FieldValue>());
								}
								if(!field.getPossibleValues().stream().anyMatch(n->{ return n.getCode().equals(stringOrgValue);})) {									
									field.getPossibleValues().add(fv);
								}
								document.addOrganizationField(field.getFieldName() + "_" + field.getSubType() + "_" + index, orgFields);
							}
						}
					}
					break;
				case MULTILANG_STRING:

					String mlStringValue = "";
					NodeList narrativeNodeList;
					Map<String, String> mlv = new HashMap<String, String>();
					fieldNodeList = element.getElementsByTagName(field.getFieldName());
					if (fieldNodeList.getLength() > 0) {
						List<Element> titles = new ArrayList<Element>();
						for (int j = 0; j < fieldNodeList.getLength(); j++) {
							if (fieldNodeList.item(j).getParentNode().getNodeName().equals("iati-activity")) {
								titles.add((Element) fieldNodeList.item(j));
							}
						}
						for (Element titleElement : titles) {							
							narrativeNodeList = titleElement.getElementsByTagName("narrative");
							for (int j = 0; j < narrativeNodeList.getLength(); j++) {
								Element narrativeElement = (Element) narrativeNodeList.item(j);								
								if (narrativeElement.getChildNodes().getLength() == 1) {
									mlStringValue = narrativeElement.getChildNodes().item(0).getNodeValue();
									if(mlStringValue != null && !("".equals(mlStringValue))){
										if (!"".equals(narrativeElement.getAttribute("xml:lang"))) {
											String languageCode = narrativeElement.getAttribute("xml:lang");
											Optional<Language> selectedLanguage = this.getFilterLanguages().stream().filter(language -> languageCode.equalsIgnoreCase(language.getCode()) && language.getSelected() == true ).findFirst();
											if(selectedLanguage.isPresent()){									
												mlv.put(languageCode, mlStringValue);
											}
										}else{											
											mlv.put(defaultLanguageCode, mlStringValue);																						
										}
									}									
								} else {
									if(mlStringValue != null && !("".equals(mlStringValue))){
										mlv.put(defaultLanguageCode, mlStringValue);
									}
									
								}
								
								
							}

						}
					}

					document.addMultilangStringField(field.getFieldName(), mlv);
					break;
				case TRANSACTION:
					try {
						NodeList nodes;
						nodes = (NodeList) xPath.evaluate("transaction/transaction-type[@code='" + field.getSubType() + "' or @code= '" + field.getSubTypeCode() + "']/parent::*", element, XPathConstants.NODESET);
						for (int j = 0; j < nodes.getLength(); ++j) {
							String reference = "";
							String receivingOrganization = "";
							String providerOrganization = "";
							String providerRef = "";

							Element e = (Element) nodes.item(j);
							// Reference
							reference = e.getAttribute("ref");
							// Amount
							String localValue = e.getElementsByTagName("value").item(0).getChildNodes().item(0).getNodeValue();
							// Date
							NodeList el = e.getElementsByTagName("transaction-date").item(0).getChildNodes();
							String localDate = "";
							if(el.getLength() == 0) {
								localDate = ((Element) el).getAttribute("iso-date");
							}else{
								localDate = el.item(0).getNodeValue();
								
							}
							if (localDate != null && !isValidDate(localDate)) // TODO: Make it
															// defensive
							{
								localDate = e.getElementsByTagName("transaction-date").item(0).getAttributes().getNamedItem("iso-date").getNodeValue();
							}
							
							Element receiverNode = e.getElementsByTagName("receiver-org").item(0) != null
									? (Element) e.getElementsByTagName("receiver-org").item(0) : null;

							receivingOrganization = "";
							if (receiverNode != null) {
								receivingOrganization = receiverNode.getElementsByTagName("narrative").item(0) != null
										? receiverNode.getElementsByTagName("narrative").item(0).getTextContent() : "";
							}

							Element providerNode = e.getElementsByTagName("provider-org").item(0) != null
									? (Element) e.getElementsByTagName("provider-org").item(0) : null;
							if (providerNode != null) {
								providerOrganization = providerNode.getElementsByTagName("narrative").item(0) != null
										? providerNode.getElementsByTagName("narrative").item(0).getTextContent() : "";
								providerRef = providerNode.getAttribute("ref");
							}

							Map<String, String> transactionFields = new HashMap<String, String>();
							transactionFields.put("date", localDate);
							transactionFields.put("receiving-org", receivingOrganization);
							transactionFields.put("provider-org", providerOrganization);
							transactionFields.put("provider-org-ref", providerRef);
							transactionFields.put("reference", reference);
							transactionFields.put("value", localValue);
							transactionFields.put("subtype", field.getSubType());
							document.addTransactionField("transaction" + field.getSubType() + "_" + j,
									transactionFields);
						}

					} catch (XPathExpressionException e1) {
						throw new Exception("Document couldn't be parsed.");
					}
					break;
				case DATE:
					try {
						NodeList nodes;
						nodes = (NodeList) xPath.evaluate("activity-date[@type='" + dateTypes.get(field.getSubType()) + "']", element, XPathConstants.NODESET);
						for (int j = 0; j < nodes.getLength(); ++j) {
							Element e = (Element) nodes.item(j);
							String localDate = e.getAttribute("iso-date");
							String format = "yyyy-MM-dd";
							SimpleDateFormat sdf = new SimpleDateFormat(format);
							document.addDateField(field.getUniqueFieldName(), sdf.parse(localDate));
						}

					} catch (XPathExpressionException | ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				default:
					break;
				}
			}			
			list.add(document);			
		}

		return list;
	}

	private Boolean includedByFilter(List<String> filters, String codeValue) {
		if (filters.size() == 0)
			return true;

		for (String value : filters) {
			if (value.equals(codeValue)) {
				return true;
			}
		}
		return false;
	}

	private void clearUsedValues(){
		for (Field field : getFields()) {
			if(field.getPossibleValues() != null){
				for(FieldValue fv : field.getPossibleValues()){
					fv.setSelected(false);
				}			
			   }
			}			
		
	}
	
	private List<String> extractLanguage(NodeList elementsByTagName) {
		List<String> list = new ArrayList<String>();
		try {
			for (int i = 0; i < elementsByTagName.getLength(); i++) {
				Node langAttr = elementsByTagName.item(i).getAttributes().getNamedItem("xml:lang");
				if (langAttr == null)
					continue;
				String lang = langAttr.getNodeValue();
				if (!list.contains(lang)) {
					list.add(lang);
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return list;
	}

	protected void instantiateStaticFields() {
		// Text fields
		fieldList.add(new Field("IATI Identifier", "iati-identifier", FieldType.STRING, false));
		fieldList.add(new Field("Title", "title", FieldType.MULTILANG_STRING, false));
		fieldList.add(new Field("Description", "description", FieldType.MULTILANG_STRING, true));
		// fieldList.add(new Field("Currency", "default-currency",
		// FieldType.STRING, false));

		// Code Lists
		Field activityStatus = new Field("Activity Status", "activity-status", FieldType.LIST, true);
		activityStatus.setPossibleValues(getCodeListValues("activity-status"));
		fieldList.add(activityStatus);
		filterFieldList.add(activityStatus);

		Field activityScope = new Field("Activity Scope", "activity-scope", FieldType.LIST, true);
		activityScope.setPossibleValues(getCodeListValues("activity-scope"));
		fieldList.add(activityScope);
		filterFieldList.add(activityScope);

		Field aidType = new Field("Aid Type", "default-aid-type", FieldType.LIST, true);
		aidType.setPossibleValues(getCodeListValues("default-aid-type"));
		fieldList.add(aidType);
		filterFieldList.add(aidType);

		Field financeType = new Field("Finance Type", "default-finance-type", FieldType.LIST, true);
		financeType.setPossibleValues(getCodeListValues("default-finance-type"));
		fieldList.add(financeType);
		filterFieldList.add(financeType);

		Field flowType = new Field("Flow Type", "default-flow-type", FieldType.LIST, true);
		flowType.setPossibleValues(getCodeListValues("default-flow-type"));
		fieldList.add(flowType);
		filterFieldList.add(flowType);

		Field tiedStatus = new Field("Tied Status", "default-tied-status", FieldType.LIST, true);
		tiedStatus.setPossibleValues(getCodeListValues("default-tied-status"));
		fieldList.add(tiedStatus);
		filterFieldList.add(tiedStatus);

		Field policyMarker = new Field("PolicyMarker", "policy-marker", FieldType.LIST, true);
		policyMarker.setPossibleValues(getCodeListValues("policy-marker"));
		fieldList.add(policyMarker);
		filterFieldList.add(policyMarker);

		Field recipientCountry = new Field("Recipient Country", "recipient-country", FieldType.RECIPIENT_COUNTRY, true);
		recipientCountry.setPossibleValues(getCodeListValues("recipient-country"));
		recipientCountry.setExclusive(true);
		recipientCountry.setFilterRequired(true);
		fieldList.add(recipientCountry);
		filterFieldList.add(recipientCountry);

		Field sector = new Field("Sector", "sector", FieldType.LIST, true);
		sector.setPossibleValues(getCodeListValues("sector"));
		sector.setMultiple(true);
		sector.setPercentage(true);
		fieldList.add(sector);
		filterFieldList.add(sector);

		// Dates
		Field activityDateStartPlanned = new Field("Activity Date Start Planned", "activity-date", FieldType.DATE, true);
		activityDateStartPlanned.setSubType("start-planned");
		fieldList.add(activityDateStartPlanned);

		Field activityDateEndPlanned = new Field("Activity Date End Planned", "activity-date", FieldType.DATE, true);
		fieldList.add(activityDateEndPlanned);
		activityDateEndPlanned.setSubType("end-planned");

		Field activityDateStartActual = new Field("Activity Date Start Actual", "activity-date", FieldType.DATE, true);
		activityDateStartActual.setSubType("start-actual");
		fieldList.add(activityDateStartActual);

		Field activityDateEndActual = new Field("Activity Date End Actual", "activity-date", FieldType.DATE, true);
		fieldList.add(activityDateEndActual);
		activityDateEndActual.setSubType("end-actual");

		// Transaction Fields
		Field commitments = new Field("Commitments", "transaction", FieldType.TRANSACTION, true);
		commitments.setSubType("C");
		commitments.setSubTypeCode("2");
		fieldList.add(commitments);

		Field disbursements = new Field("Disbursements", "transaction", FieldType.TRANSACTION, true);
		disbursements.setSubType("D");
		disbursements.setSubTypeCode("3");
		fieldList.add(disbursements);

		// Organization Fields
		Field participatingOrg = new Field("Funding Organization", "participating-org", FieldType.ORGANIZATION, true);
		participatingOrg.setSubType("Funding");
		participatingOrg.setSubTypeCode("1");
		fieldList.add(participatingOrg);
	}

	
	@Override
	public Boolean isValidInput() {
		NodeList nodeList = doc.getElementsByTagName("iati-activities");
		try {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node version = nodeList.item(i).getAttributes().getNamedItem("version");
				if (version == null)
					continue;
				String ver = version.getNodeValue();
				if (ver.equalsIgnoreCase(PROCESSOR_VERSION)) {
					return true;
				}
			}
		} catch (Exception e) {
			log.error("Error validating IATI " + PROCESSOR_VERSION + " file");
		}

		return false;
	}

	public boolean isValidDate(String dateString) {
		SimpleDateFormat df = new SimpleDateFormat(ISO_DATE);
		try {
			df.parse(dateString);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	
}
