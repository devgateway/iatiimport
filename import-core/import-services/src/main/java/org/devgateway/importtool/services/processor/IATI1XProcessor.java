package org.devgateway.importtool.services.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.model.Language;
import org.devgateway.importtool.services.processor.helper.ActionStatus;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldType;
import org.devgateway.importtool.services.processor.helper.FieldValue;
import org.devgateway.importtool.services.processor.helper.IATIProcessorHelper;
import org.devgateway.importtool.services.processor.helper.InternalDocument;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
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
import java.util.Set;

@Component("IATI1X")
@Scope("session")
abstract public class IATI1XProcessor extends IATIProcessor {

	protected static final String ISO_DATE = "yyyy-MM-dd";

	protected Log log = LogFactory.getLog(getClass());

	// Global Lists for fields and the filters
	protected List<Field> fieldList = new ArrayList<Field>();

	// Field names on the source document that hold key information
	protected String DEFAULT_TITLE_FIELD = "title";

	protected String descriptiveName = "";
	protected String codelistPath = "";

	protected ActionStatus actionStatus;

	public ActionStatus getActionStatus() {
		return actionStatus;
	}


	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}


	protected List<Language> filterLanguages = new ArrayList<Language>();

	public String getCodelistPath() {
		return codelistPath;
	}

	public void setCodelistPath(String codelistPath) {
		this.codelistPath = codelistPath;
	}

	public IATI1XProcessor(){
		
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
			return getFilterFieldList();

		// List<Field> getFilterFieldList()Reduced = new ArrayList<Field>();
		for (Field field : getFilterFieldList()) {
			NodeList nodeList = doc.getElementsByTagName(field.getFieldName());
			List<FieldValue> reducedPossibleValues = new ArrayList<FieldValue>();
			switch (field.getType()) {
			case ORGANIZATION:
				if (field.getPossibleValues() == null) {
					field.setPossibleValues(new ArrayList<FieldValue>());
				}
				if (nodeList.getLength() > 0) {					
					for (int i = 0; i < nodeList.getLength(); i++) {
						Element fieldElement = (Element) nodeList.item(i);
						final String orgCode = fieldElement.getAttribute("value");
						Optional<FieldValue> fieldValue = field.getPossibleValues().stream().filter(n -> {
							return n.getCode().equals(orgCode);
						}).findFirst();
						if(!fieldValue.isPresent()) {
							FieldValue newfv = new FieldValue();
							final String name = fieldElement.getTextContent();
							newfv.setCode(orgCode);
							newfv.setValue(name);							
							newfv.setIndex(field.getPossibleValues().size());
							field.getPossibleValues().add(newfv);
						}
					}
				}
				break;
			case LOCATION:
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
							final String name = fieldElement.getTextContent();
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
				field.setPossibleValues(reducedPossibleValues);
				break;
			default:
				break;
			}
		}
		return getFilterFieldList();
	}

	@Override
	public String getDescriptiveName() {
		return this.descriptiveName;
	}

	@Override
	public void setFilterFields(List<Field> fields) {
		setFilterFieldList(fields);
	}

	// Private methods. Includes methods to get values for the different types
	// of data being managed.

	private List<FieldValue> getCodeListValues(String codeListName) {
		return getCodeListValues(codeListName, false);
	}

	private List<FieldValue> getCodeListValues(String codeListName, Boolean concatenate) {
		String standardFieldName = IATIProcessorHelper.mappingNameFile.get(codeListName);
		List<FieldValue> possibleValues = new ArrayList<FieldValue>();
		InputStream is = this.getClass().getResourceAsStream(this.getCodelistPath() + standardFieldName + ".xml");
		if (is != null) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setValidating(false);
				factory.setIgnoringElementContentWhitespace(true);
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(is);
				NodeList nodeList = doc.getElementsByTagName(standardFieldName);
				if (nodeList.getLength() == 0) {
					nodeList = doc.getElementsByTagName("codelist-item");
				}
				int index = 0;
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						Element codeElement = (Element) element.getElementsByTagName("code").item(0);
						String code = codeElement.getChildNodes().item(0).getNodeValue();

						Element nameElement = (Element) element.getElementsByTagName("name").item(0);
						String name = nameElement.getChildNodes().item(0).getNodeValue();

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

	private String getFieldName(String fieldName) {
		String name = fieldName;
		switch(fieldName) {
		case "sector":
			name = "transaction/sector";
			break;
		}
		return name;
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
			String currency = !("".equals(element.getAttribute("default-currency"))) ? element.getAttribute
					("default-currency") : this.getDefaultCurrency();
			document.addStringField("default-currency", currency);	
			String defaultLanguageCode = !("".equals(element.getAttribute("xml:lang"))) ? element.getAttribute
					("xml:lang") : this.getDefaultLanguage();
			
			
			NodeList fieldNodeList;			
			for (Field field : getFields()) {
				switch (field.getType()) {
				case LOCATION:
					fieldNodeList = element.getElementsByTagName(field.getFieldName());
					List <String> codesLocation = new ArrayList<String>(); 
					for (int j = 0; j < fieldNodeList.getLength(); j++) {
						Element fieldElement = (Element) fieldNodeList.item(j);
						String code = "code";							
						if(!code.isEmpty()) {
							codesLocation.add(code);
							FieldValue fv = new FieldValue();
							if(code != null && !code.isEmpty() ){
								fv.setCode(code);
								fv.setValue(code);	
								fv.setSelected(true);
							}
							int index = field.getPossibleValues() == null ? 0 : field.getPossibleValues().size();
							fv.setIndex(index);
							if (field.getPossibleValues() == null) {
								field.setPossibleValues(new ArrayList<FieldValue>());
							}
							if(!field.getPossibleValues().stream().anyMatch(n->{ return n.getCode().equals(code);})) {									
								field.getPossibleValues().add(fv);
							}
						}				
					}
					if(!codesLocation.isEmpty()){
						String[] codeValues = codesLocation.stream().toArray(String[]::new);						
						document.addStringMultiField(field.getFieldName(), codeValues);
					}
					break;
				case LIST:
					IATIProcessorHelper.processListElementType(document, element, field);
					break;
				case RECIPIENT_COUNTRY:
					Field filtersField = getFilterFieldList().stream().filter(n -> {
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
					IATIProcessorHelper.processStringElementType(document, element, field);
					break;
				case ORGANIZATION:
					fieldNodeList = element.getElementsByTagName(field.getFieldName());
					if (fieldNodeList.getLength() > 0) {
						for (int j = 0; j < fieldNodeList.getLength(); j++) {
							Element fieldElement = (Element) fieldNodeList.item(j);
							if (fieldElement.getAttribute("role").equals(field.getSubType())) {
								final String stringOrgValue = fieldElement.getTextContent();
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
					Map<String, String> mlv = new HashMap<String, String>();
					fieldNodeList = (NodeList) xPath.evaluate(field.getFieldName(), element, XPathConstants.NODESET);
					for (int k = 0; k < fieldNodeList.getLength(); ++k) {
						Element fieldElement = (Element) fieldNodeList.item(k);
						if (fieldElement.getChildNodes().getLength() == 1) {
							String mlStringValue = fieldElement.getChildNodes().item(0).getNodeValue();
							Node langAttr = fieldElement.getAttributes().getNamedItem("xml:lang");
							if(mlStringValue != null && !("".equals(mlStringValue))){
								if(langAttr != null){								
									String lang = langAttr.getNodeValue();								
									Optional<Language> selectedLanguage = this.getFilterLanguages().stream().filter(language -> lang.equalsIgnoreCase(language.getCode()) && language.getSelected() == true ).findFirst();
									if(selectedLanguage.isPresent()){									
										mlv.put(lang, mlStringValue);
									}								
								}else{								
									mlv.put(defaultLanguageCode, mlStringValue);
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
							
							Element e = (Element) nodes.item(j);
							// Reference
							reference = e.getAttribute("ref");
							// Amount
							String localValue = e.getElementsByTagName("value").item(0).getChildNodes().item(0).getNodeValue();
							// Date
							String localDate = "";
							if(e.getElementsByTagName("transaction-date").item(0) != null && e.getElementsByTagName("transaction-date").item(0).getChildNodes().item(0) != null){
								localDate = e.getElementsByTagName("transaction-date").item(0).getChildNodes().item(0).getNodeValue();
							}														
							if (!isValidDate(localDate)){
								localDate = (e.getElementsByTagName("transaction-date").item(0) != null && e.getElementsByTagName("transaction-date").item(0).getAttributes() != null) ? e.getElementsByTagName("transaction-date").item(0).getAttributes().getNamedItem("iso-date").getNodeValue() : "";
							}

							Element receiverNode = e.getElementsByTagName("receiver-org").item(0) != null
									? (Element) e.getElementsByTagName("receiver-org").item(0) : null;

							final String receivingOrganization = (receiverNode != null
									&& receiverNode.getChildNodes().item(0) != null)
											? receiverNode.getChildNodes().item(0).getNodeValue() : "";

							Element providerNode = e.getElementsByTagName("provider-org").item(0) != null
									? (Element) e.getElementsByTagName("provider-org").item(0) : null;

							final String providingOrganization = (providerNode != null
									&& providerNode.getChildNodes().getLength() > 0)
											? providerNode.getChildNodes().item(0).getNodeValue() : "";
							final String providerRef = (providerNode != null) ? providerNode.getAttribute("ref") : "";

							// Get the field for provider org
							Optional<Field> fieldValue = getFilterFieldList().stream().filter(n -> {
								return "provider-org".equals(n.getFieldName());
							}).findFirst();

							// If it has filters set, check if this transaction
							// complies
							if (fieldValue.isPresent() && fieldValue.get().getFilters().size() > 0) {
								// See if the current transaction has the
								// correct provider organization
								Optional<String> optField = fieldValue.get().getFilters().stream().filter(n -> {
									return n.equals(providingOrganization);
								}).findAny();

								if (!optField.isPresent()) {
									// If it's not there, then move to the next
									// transaction
									continue;
								}
							}

							Map<String, String> transactionFields = new HashMap<String, String>();
							transactionFields.put("date", localDate);
							transactionFields.put("receiving-org", receivingOrganization);
							transactionFields.put("providing-org", providingOrganization);
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
						nodes = (NodeList) xPath.evaluate("//activity-date[@type='" + field.getSubType() + "']", element, XPathConstants.NODESET);
						for (int j = 0; j < nodes.getLength(); ++j) {
							Element e = (Element) nodes.item(j);
							String localDate = e.getAttribute("iso-date");
							String format = "yyyy-MM-dd";
							SimpleDateFormat sdf = new SimpleDateFormat(format);
							if(localDate != null && !localDate.isEmpty()){
								document.addDateField(field.getUniqueFieldName(), sdf.parse(localDate));
							}
							
						}

					} catch (XPathExpressionException | ParseException e1) {
						e1.printStackTrace();
					}
					break;
				case CONTACT:					
					/*NodeList contactNodes = element.getElementsByTagName(field.getFieldName());
					if (contactNodes.getLength() > 0) {
						for (int j = 0; j < contactNodes.getLength(); j++) {													
							Element contact = (Element) contactNodes.item(j);							
							String organisation = contact.getElementsByTagName("organisation").item(0) != null ?  contact.getElementsByTagName("organisation").item(0).getTextContent() : "";						    
							String personName = contact.getElementsByTagName("person-name").item(0) != null ?  contact.getElementsByTagName("person-name").item(0).getTextContent() : "";
							String jobTitle = contact.getElementsByTagName("job-title").item(0) != null ?  contact.getElementsByTagName("job-title").item(0).getTextContent() : "";
							String telephone = contact.getElementsByTagName("telephone").item(0) != null ?  contact.getElementsByTagName("telephone").item(0).getTextContent() : "";
							String email = contact.getElementsByTagName("email").item(0) != null ?  contact.getElementsByTagName("email").item(0).getTextContent() : "";
							String address = contact.getElementsByTagName("mailing-address").item(0) != null ?  contact.getElementsByTagName("mailing-address").item(0).getTextContent() : "";
							String website = contact.getElementsByTagName("website").item(0) != null ?  contact.getElementsByTagName("website").item(0).getTextContent() : "";
							
							Map<String, String> contactFields = new HashMap<String, String>();													
							contactFields.put("organisation", organisation);
							contactFields.put("person-name", personName);
							contactFields.put("job-title", jobTitle);
							contactFields.put("telephone", telephone);
							contactFields.put("email", email);
							contactFields.put("mailing-address", address);
							contactFields.put("website", website);							
							document.addContactFields("donor_contact", contactFields);							
						}
					}	*/				
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
		//fieldList.add(new Field("Currency", "default-currency", FieldType.STRING, false));

		// Code Lists
		Field activityStatus = new Field("Activity Status", "activity-status", FieldType.LIST, true);
		activityStatus.setPossibleValues(getCodeListValues("activity-status"));
		fieldList.add(activityStatus);
		getFilterFieldList().add(activityStatus);

		Field activityScope = new Field("Activity Scope", "activity-scope", FieldType.LIST, true);
		activityScope.setPossibleValues(getCodeListValues("activity-scope"));
		fieldList.add(activityScope);
		getFilterFieldList().add(activityScope);

		Field aidType = new Field("Aid Type", "default-aid-type", FieldType.LIST, true);
		aidType.setPossibleValues(getCodeListValues("default-aid-type"));
		fieldList.add(aidType);
		getFilterFieldList().add(aidType);

		Field financeType = new Field("Finance Type", "default-finance-type", FieldType.LIST, true);
		financeType.setPossibleValues(getCodeListValues("default-finance-type"));
		fieldList.add(financeType);
		getFilterFieldList().add(financeType);

		Field flowType = new Field("Flow Type", "default-flow-type", FieldType.LIST, true);
		flowType.setPossibleValues(getCodeListValues("default-flow-type"));
		fieldList.add(flowType);
		getFilterFieldList().add(flowType);

		Field tiedStatus = new Field("Tied Status", "default-tied-status", FieldType.LIST, true);
		tiedStatus.setPossibleValues(getCodeListValues("default-tied-status"));
		fieldList.add(tiedStatus);
		getFilterFieldList().add(tiedStatus);

		Field policyMarker = new Field("PolicyMarker", "policy-marker", FieldType.LIST, true);
		policyMarker.setPossibleValues(getCodeListValues("policy-marker"));
		fieldList.add(policyMarker);
		getFilterFieldList().add(policyMarker);

		Field recipientCountry = new Field("Recipient Country", "recipient-country", FieldType.RECIPIENT_COUNTRY, true);
		recipientCountry.setPossibleValues(getCodeListValues("recipient-country"));
		recipientCountry.setExclusive(true);
		recipientCountry.setFilterRequired(true);
		fieldList.add(recipientCountry);
		getFilterFieldList().add(recipientCountry);

		Field sector = new Field("Sector", "sector", FieldType.LIST, true);
		sector.setPossibleValues(getCodeListValues("sector"));
		sector.setMultiple(true);
		sector.setPercentage(true);
		fieldList.add(sector);
		getFilterFieldList().add(sector);

		Field location = new Field("Location", "location", FieldType.LOCATION, true);
		location.setPossibleValues(new ArrayList<FieldValue>());
		location.setMultiple(true);
		location.setPercentage(true);
		fieldList.add(location);

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
		fieldList.add(participatingOrg);
		
		Field accountableOrg = new Field("Accountable Organization", "participating-org", FieldType.ORGANIZATION, true);
		accountableOrg.setSubType("Accountable");
		fieldList.add(accountableOrg);

		Field extendingOrg = new Field("Extending Organization", "participating-org", FieldType.ORGANIZATION, true);
		extendingOrg.setSubType("Extending");
		fieldList.add(extendingOrg);

		Field implementingOrg = new Field("Implementing Organization", "participating-org", FieldType.ORGANIZATION, true);
		implementingOrg.setSubType("Implementing");
		fieldList.add(implementingOrg);
		
		// Provider Organization, within Transactions
		Field providerOrg = new Field("Provider Organization", "provider-org", FieldType.ORGANIZATION, false);
		providerOrg.setSubType("Provider");
		fieldList.add(providerOrg);
		getFilterFieldList().add(providerOrg);

		//Contact Info
		Field contact = new Field("Contact Info", "contact-info", FieldType.CONTACT, false);
		contact.setMultiple(true);
		fieldList.add(contact);
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
