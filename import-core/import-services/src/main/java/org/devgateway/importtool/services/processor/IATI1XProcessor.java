package org.devgateway.importtool.services.processor;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.model.Language;
import org.devgateway.importtool.services.processor.helper.ActionStatus;
import org.devgateway.importtool.services.processor.helper.Constants;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldType;
import org.devgateway.importtool.services.processor.helper.FieldValue;
import org.devgateway.importtool.services.processor.helper.IATIProcessorHelper;
import org.devgateway.importtool.services.processor.helper.InternalDocument;
import org.devgateway.importtool.services.processor.helper.ProviderOganizationField;
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
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component("IATI1X")
@Scope("session")
abstract public class IATI1XProcessor extends IATIProcessor {

	protected static final String ISO_DATE = "yyyy-MM-dd";

	protected Log log = LogFactory.getLog(getClass());



	// Field names on the source document that hold key information
	protected String DEFAULT_TITLE_FIELD = "title";

	protected String descriptiveName = "";
	private String PROCESSOR_SUPER_VERSION = "1x";
	protected ActionStatus actionStatus;

	public ActionStatus getActionStatus() {
		return actionStatus;
	}


	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}


	public String getCodelistPath() {
		return codelistPath;
	}

	public void setCodelistPath(String codelistPath) {
		this.codelistPath = codelistPath;
	}

	public IATI1XProcessor(){
		labelsTranslationsLocation = "IATI1X/fields/labels/labels";
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
	public List<Field> getFilterFields() {
		Document doc = this.doc;
		if (doc == null)
			return getFilterFieldList();
		for (Field field : getFilterFieldList()) {
			String xPath = "//"+ field.getFieldName() + (this.isFromDatastore()?"["+
					field.getXpathFilterCondition(this.isFromDatastore()) +
					"ancestor::iati-activity [@*[name()='iati-extra:version']='"+ PROCESSOR_VERSION +"']]":
					 field.getXpathFilterCondition(this.isFromDatastore()) +"");
			NodeList nodeList = this.getNodeListFromXpath(this.doc , xPath);

			List<FieldValue> reducedPossibleValues = new ArrayList<FieldValue>();
			if(nodeList != null) {
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
							if (!fieldValue.isPresent()) {
								final String name = fieldElement.getTextContent();
								FieldValue newfv = new FieldValue();
								newfv.setCode(orgCode);
								if (StringUtils.isEmpty(name)) {
									newfv.setValue(orgCode);
								} else {
									newfv.setValue(name);
								}
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

								if (!fieldValue.isPresent() && !codeValue.isEmpty()) {
									FieldValue newfv = new FieldValue();
									final String name = fieldElement.getTextContent();
									newfv.setCode(codeValue);									
									if (StringUtils.isEmpty(name) ) {
										newfv.setValue(codeValue);
									} else {										
										newfv.setValue(name);	
									}									
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
			try {
                 Document doc = getDocument(this.getCodelistPath() + standardFieldName + ".xml");
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
				log.error("IOException Parsing Source File:  " + e);
			}
		return possibleValues;
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
                    processLocationElementType(document, element, field);
					break;
				case LIST:
					processListElementType(document, element, field);
					break;
				case RECIPIENT_COUNTRY:
                    processRecipientCountryElementType(document, element, field);
					break;
				case STRING:
					processStringElementType(document, element, field);
					break;
				case ORGANIZATION:
                    processOrganizationElementType(document,element,field);
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

						nodes = (NodeList) xPath.evaluate("(transaction/value/parent::*)/transaction-type[@code='"+
								field.getSubType() +"' or @code= '"+ field.getSubTypeCode() +"']/parent::*",element,
								XPathConstants.NODESET);
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
							
							//if no provider tag, use reporting org		
                            if (providerNode == null) {
                                providerNode = element.getElementsByTagName("reporting-org").item(0) != null
                                        ? (Element) element.getElementsByTagName("reporting-org").item(0)
                                        : null;
                            }

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
							
							if (StringUtils.isBlank(providingOrganization)) {
								//if we don't have provider organization we should ingore the transaction
								continue;
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
                    processDateElementType(document, element, field);
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

    private void clearUsedValues(){
		for (Field field : getFields()) {
			if(field.getPossibleValues() != null){
				for(FieldValue fv : field.getPossibleValues()){
					fv.setSelected(false);
				}			
			   }
			}			
		
	}


	protected void instantiateStaticFields() {
		//We need to to refactor the way of instantiating this static fields. Probably from a config file, or from
		// configuration map, but it has a lot of repeating code
		// Text fields
		getFields().add(new Field("IATI Identifier", "iati-identifier", FieldType.STRING,
                false, getTooltipForField("iati-identifier"), getLabelsForField("iati-identifier")));

		getFields().add(new Field("Title", "title", FieldType.MULTILANG_STRING, false,
                getTooltipForField("title"),  getLabelsForField("title")));

		getFields().add(new Field("Description", "description", FieldType.MULTILANG_STRING,
                true, getTooltipForField("description"), getLabelsForField("description")));

		// Code Lists
		Field activityStatus = new Field("Activity Status", "activity-status", FieldType.LIST,
                true, getTooltipForField("activity-status"), getLabelsForField("activity-status"));
		activityStatus.setPossibleValues(getCodeListValues("activity-status"));
		getFields().add(activityStatus);
		getFilterFieldList().add(activityStatus);

		Field activityScope = new Field("Activity Scope", "activity-scope", FieldType.LIST,
                true, getTooltipForField("activity-scope"), getLabelsForField("activity-scope"));
		activityScope.setPossibleValues(getCodeListValues("activity-scope"));
		getFields().add(activityScope);
		getFilterFieldList().add(activityScope);

		Field aidType = new Field("Aid Type", "default-aid-type", FieldType.LIST, true,
                getTooltipForField("default-aid-type"), getLabelsForField("default-aid-type"));
		aidType.setPossibleValues(getCodeListValues("default-aid-type"));
		getFields().add(aidType);
		getFilterFieldList().add(aidType);

		Field financeType = new Field("Finance Type", "default-finance-type", FieldType.LIST,
                true, getTooltipForField("default-finance-type"), getLabelsForField("default-finance-type"));
		financeType.setPossibleValues(getCodeListValues("default-finance-type"));
		getFields().add(financeType);
		getFilterFieldList().add(financeType);

		Field flowType = new Field("Flow Type", "default-flow-type", FieldType.LIST,
                true, getTooltipForField("default-flow-type"), getLabelsForField("default-flow-type"));
		flowType.setPossibleValues(getCodeListValues("default-flow-type"));
		getFields().add(flowType);
		getFilterFieldList().add(flowType);

		Field tiedStatus = new Field("Tied Status", "default-tied-status", FieldType.LIST,
                true, getTooltipForField("default-tied-status"), getLabelsForField("default-tied-status"));
		tiedStatus.setPossibleValues(getCodeListValues("default-tied-status"));
		getFields().add(tiedStatus);
		getFilterFieldList().add(tiedStatus);


		Field policyMarker = new Field("PolicyMarker", "policy-marker", FieldType.LIST,
                true, getTooltipForField("policy-marker"), getLabelsForField("policy-marker"));
		policyMarker.setPossibleValues(getCodeListValues("policy-marker"));
		policyMarker.setMultiple(true);
		getFields().add(policyMarker);
		getFilterFieldList().add(policyMarker);

		Field recipientCountry = new Field("Recipient Country", "recipient-country",
                FieldType.RECIPIENT_COUNTRY, true, getTooltipForField("recipient-country"), getLabelsForField(
                		"recipient-country"));
		recipientCountry.setPossibleValues(getCodeListValues("recipient-country"));
		recipientCountry.setExclusive(true);
		recipientCountry.setFilterRequired(true);
		getFields().add(recipientCountry);
		getFilterFieldList().add(recipientCountry);

		Field sector = new Field("Sector", "sector", FieldType.LIST, true,
                getTooltipForField("sector") , getLabelsForField("sector") );
		sector.setPossibleValues(getCodeListValues("sector"));
		sector.setMultiple(true);
		sector.setPercentage(true);
		getFields().add(sector);
		getFilterFieldList().add(sector);

		Field location = new Field("Location", "location", FieldType.LOCATION, true,
                getTooltipForField("location"), getLabelsForField("location"));
		location.setPossibleValues(new ArrayList<FieldValue>());
		location.setMultiple(true);
		location.setPercentage(true);
		getFields().add(location);

		// Dates
		Field activityDateStartPlanned = new Field("Activity Date Start Planned", "activity-date",
                FieldType.DATE, true, getTooltipForField("activity-date_start-planned"), getLabelsForField("activity" +
				"-date_start-planned"));
		activityDateStartPlanned.setSubType("start-planned");
		getFields().add(activityDateStartPlanned);

		Field activityDateEndPlanned = new Field("Activity Date End Planned", "activity-date",
                FieldType.DATE, true, getTooltipForField("activity-date_end-planned"),
				getLabelsForField("activity-date_end-planned"));
		getFields().add(activityDateEndPlanned);
		activityDateEndPlanned.setSubType("end-planned");

		Field activityDateStartActual = new Field("Activity Date Start Actual", "activity-date",
                FieldType.DATE, true , getTooltipForField("activity-date_start-actual"), getLabelsForField("activity" +
				"-date_start-actual"));
		activityDateStartActual.setSubType("start-actual");
		getFields().add(activityDateStartActual);

		Field activityDateEndActual = new Field("Activity Date End Actual", "activity-date",
                FieldType.DATE, true, getTooltipForField("activity-date_end-actual"), getLabelsForField("activity" +
				"-date_end-actual"));
		getFields().add(activityDateEndActual);
		activityDateEndActual.setSubType("end-actual");

		// Transaction Fields
		Field commitments = new Field("Commitments", "transaction", FieldType.TRANSACTION,
                true, getTooltipForField("transaction_C"), getLabelsForField("transaction_C"));
		commitments.setSubType("C");
		commitments.setSubTypeCode("2");
		getFields().add(commitments);

		Field disbursements = new Field("Disbursements", "transaction", FieldType.TRANSACTION,
                true, getTooltipForField("transaction_D"), getLabelsForField("transaction_D"));
		disbursements.setSubType("D");
		disbursements.setSubTypeCode("3");
		getFields().add(disbursements);

		// Organization Fields
		
		Field participatingOrg = new Field(Constants.FUNDING_ORG_DISPLAY_NAME, "participating-org",
                FieldType.ORGANIZATION,true, getTooltipForField("participating-org_"
				+ Constants.ORG_ROLE_FUNDING), getLabelsForField("participating-org_" + Constants.ORG_ROLE_FUNDING));
		participatingOrg.setSubType(Constants.ORG_ROLE_FUNDING);
		participatingOrg.setSubTypeCode(Constants.ORG_ROLE_FUNDING_CODE);
		getFields().add(participatingOrg);
		
		Field accountableOrg = new Field(Constants.ACCOUNTABLE_ORG_DISPLAY_NAME, "participating-org",
                FieldType.ORGANIZATION, true , getTooltipForField("participating-org_"
				+ Constants.ORG_ROLE_ACCOUNTABLE),
				getLabelsForField("participating-org_" + Constants.ORG_ROLE_ACCOUNTABLE));
		accountableOrg.setSubTypeCode(Constants.ORG_ROLE_ACCOUNTABLE_CODE);
		accountableOrg.setSubType(Constants.ORG_ROLE_ACCOUNTABLE);
		getFields().add(accountableOrg);

		Field extendingOrg = new Field(Constants.EXTENDING_ORG_DISPLAY_NAME, "participating-org",
                FieldType.ORGANIZATION, true, getTooltipForField("participating-org_"
				+ Constants.ORG_ROLE_EXTENDING), getLabelsForField("participating-org_"
				+ Constants.ORG_ROLE_EXTENDING));
		extendingOrg.setSubTypeCode(Constants.ORG_ROLE_EXTENDING_CODE);
		extendingOrg.setSubType(Constants.ORG_ROLE_EXTENDING);
		getFields().add(extendingOrg);

		Field implementingOrg = new Field(Constants.IMPLEMENTING_ORG_DISPLAY_NAME, "participating-org",
                FieldType.ORGANIZATION, true, getTooltipForField("participating-org_"
				+ Constants.ORG_ROLE_IMPLEMENTING), getLabelsForField("participating-org_"
				+ Constants.ORG_ROLE_IMPLEMENTING));
		implementingOrg.setSubTypeCode(Constants.ORG_ROLE_IMPLEMENTING_CODE);
		implementingOrg.setSubType(Constants.ORG_ROLE_IMPLEMENTING);
		getFields().add(implementingOrg);
		
		// Provider Organization, within Transactions
		Field providerOrg = new ProviderOganizationField(Constants.PROVIDER_ORG_DISPLAY_NAME, "provider-org",
                FieldType.ORGANIZATION, false, getTooltipForField("provider-org"),
				getLabelsForField("provider-org"));
		providerOrg.setSubType("Provider");
		getFields().add(providerOrg);
		getFilterFieldList().add(providerOrg);

		//Contact Info
		Field contact = new Field("Contact Info", "contact-info", FieldType.CONTACT,
                false, getTooltipForField("contact-info"), getLabelsForField("contact-info"));
		contact.setMultiple(true);
		getFields().add(contact);

	}

    protected String getNameFromElement(Element fieldElement) {
        String name = null;

        if (fieldElement.getElementsByTagName("name").item(0) != null && fieldElement.
                getElementsByTagName("name").item(0).getChildNodes().item(0) != null) {
            name = fieldElement.getElementsByTagName("name").item(0).getChildNodes().item(0).
                    getNodeValue();
        }

        if (StringUtils.isBlank(name)) {
            if (fieldElement.getElementsByTagName("description").item(0) != null && fieldElement.
                    getElementsByTagName("description").item(0).getChildNodes().item(0) != null) {
                name = fieldElement.getElementsByTagName("description").item(0).getChildNodes().
                        item(0).getNodeValue();
            }
        }
        return name;
    }

    protected String getStringOrgValue(Element fieldElement) {
        return fieldElement.getTextContent();
    }

    protected  String getDateSubtype(Field field) {
        return field.getSubType();
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
