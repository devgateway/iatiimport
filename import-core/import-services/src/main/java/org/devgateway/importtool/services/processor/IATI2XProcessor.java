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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.util.DigestUtils;
@Component("IATI2X")
@Scope("session")
public class IATI2XProcessor extends IATIProcessor {

	private static final String ISO_DATE = "yyyy-MM-dd";

	private Log log = LogFactory.getLog(getClass());

	private List<String> languageList = null;
	// Field names on the source document that hold key information

	private String DEFAULT_TITLE_FIELD = "title";
	protected String descriptiveName = "";
	public String getCodelistPath() {
		return codelistPath;
	}


	public void setCodelistPath(String codelistPath) {
		this.codelistPath = codelistPath;
	}

	private ActionStatus actionStatus;

    public IATI2XProcessor(){
	    
	}

	public ActionStatus getActionStatus() {
		return actionStatus;
	}


	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
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

						if(fieldElement.getElementsByTagName("narrative").getLength() > 0) {
							final String orgCode = fieldElement.getElementsByTagName("narrative").item(0).getTextContent();
							if("".equals(orgCode.trim())) {
								continue;
							}
							Optional<FieldValue> fieldValue = field.getPossibleValues().stream().filter(n -> {
								return n.getCode().equals(orgCode);
							}).findFirst();
							if(!fieldValue.isPresent()) {
								FieldValue newfv = new FieldValue();
								newfv.setCode(orgCode);
								newfv.setValue(orgCode);
								newfv.setIndex(field.getPossibleValues().size());
								field.getPossibleValues().add(newfv);
							}
						}
					}
				}
				field.getPossibleValues().sort(Comparator.comparing(FieldValue::getCode));
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
							final String name = fieldElement.getElementsByTagName("narrative").item(0) != null ? fieldElement.getElementsByTagName("narrative").item(0).getTextContent() : "";
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


	private List<InternalDocument> extractDocuments(Document doc) throws Exception {
		// Extract global values
		Integer id = 0 ;
		id = id + 1;
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = getActivities();
		List<InternalDocument> list = new ArrayList<InternalDocument>();
		actionStatus.setTotal(Long.valueOf(nodeList.getLength()));
		this.clearUsedValues();
		for (int i = 0; i < nodeList.getLength(); i++) {
			actionStatus.incrementProcessed();
			InternalDocument document = new InternalDocument();

			Element element = (Element) nodeList.item(i);
			//we set the grouping criteria
			document.setGrouping(IATIProcessorHelper.getStringFromElement(element,DEFAULT_GROUPING_FIELD,"ref"));

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
						String code = extractNarrative(fieldElement, "name");
						if (StringUtils.isBlank(code)) {
							code = extractNarrative(fieldElement, "description");
						}
						if(code != null && !code.isEmpty()) {
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
							if(!field.getPossibleValues().stream().anyMatch(n->{ return n.getCode().equals(fv.getValue());})) {
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
							if (fieldElement.getAttribute("role").equals(field.getSubType()) || fieldElement.getAttribute("role").equals(field.getSubTypeCode())) {
								final String stringOrgValue = fieldElement.getElementsByTagName("narrative").item(0) != null ? fieldElement.getElementsByTagName("narrative").item(0).getTextContent() : "";
								final String ref = fieldElement.getAttribute("ref");
								
								if ((stringOrgValue != null && !stringOrgValue.trim().isEmpty())
										|| (ref != null && !ref.trim().isEmpty())) {

									Map<String, String> orgFields = new HashMap<String, String>();
									orgFields.put("value", stringOrgValue);
									orgFields.put("role", field.getSubType());
									orgFields.put("ref", ref);
									orgFields.put("type", fieldElement.getAttribute("type"));
									FieldValue fv = new FieldValue();
									if (stringOrgValue != null && !stringOrgValue.isEmpty()) {
										fv.setCode(stringOrgValue);
									fv.setValue(stringOrgValue);	
									} else {
										fv.setCode(ref);
										fv.setValue(ref);
									}
									fv.setSelected(true);
									int index = field.getPossibleValues() == null ? 0
											: field.getPossibleValues().size();
									fv.setIndex(index);
									if (field.getPossibleValues() == null) {
										field.setPossibleValues(new ArrayList<FieldValue>());
									}
									if (!field.getPossibleValues().stream().anyMatch(n -> {
										return n.getCode().equals(stringOrgValue);
									})) {
										field.getPossibleValues().add(fv);
									}

									document.addOrganizationField(field.getFieldName() + "_" + field.getSubType() + "_"
											+ DigestUtils.md5DigestAsHex(fv.getValue().getBytes()), orgFields);

								}				
							}
						}							
					}
					break;
				case MULTILANG_STRING:
					String mlStringValue = "";
					NodeList narrativeNodeList;
					Map<String, String> mlv = new HashMap<String, String>();
					fieldNodeList = (NodeList) xPath.evaluate(field.getFieldName(), element, XPathConstants.NODESET);
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

							final String receivingOrganization = extractNarrative(e, "receiver-org");
							Element providerNode = e.getElementsByTagName("provider-org").item(0) != null
									? (Element) e.getElementsByTagName("provider-org").item(0) : null;
							
                            // if no provider tag, use reporting org
                            if (providerNode == null) {
                                providerNode = element.getElementsByTagName("reporting-org").item(0) != null
                                        ? (Element) element.getElementsByTagName("reporting-org").item(0)
                                        : null;
                            }

							final String providingOrganization = (providerNode != null
									&& providerNode.getElementsByTagName("narrative").item(0) != null)
											? providerNode.getElementsByTagName("narrative").item(0).getTextContent()
											: "";
							final String providerRef = providerNode != null ? providerNode.getAttribute("ref") : "";

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
							transactionFields.put("providing-org", providingOrganization);
							transactionFields.put("receiving-org", receivingOrganization);
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
							if (localDate != null && !localDate.isEmpty()){
								document.addDateField(field.getUniqueFieldName(), sdf.parse(localDate));
							}							
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

	private String extractNarrative(Element e, String string) {
		Node node = e.getElementsByTagName(string).item(0);
		if(node != null && node.getChildNodes().getLength() > 0) {
		    for (int i = 0; i < node.getChildNodes().getLength(); i++) {
		        Node nodeChild = node.getChildNodes().item(i);
		        if (nodeChild.getNodeType() == Node.ELEMENT_NODE && "narrative".equals(nodeChild.getNodeName())) {
		        	return nodeChild.getTextContent();
		        }
		    }
		}
		return null;
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

	protected void instantiateStaticFields() {
		// Text fields
		getFields().add(new Field("IATI Identifier", "iati-identifier", FieldType.STRING,
				false, getTranslationForField("iati-identifier")));
		getFields().add(new Field("Title", "title", FieldType.MULTILANG_STRING, false,
				getTranslationForField("title")));
		getFields().add(new Field("Description", "description", FieldType.MULTILANG_STRING,
				true, getTranslationForField("description")));

		// Code Lists
		Field activityStatus = new Field("Activity Status", "activity-status", FieldType.LIST,
				true, getTranslationForField("activity-status"));
		activityStatus.setPossibleValues(getCodeListValues("activity-status"));
		getFields().add(activityStatus);
		getFilterFieldList().add(activityStatus);

		Field activityScope = new Field("Activity Scope", "activity-scope", FieldType.LIST,
				true, getTranslationForField("activity-scope"));
		activityScope.setPossibleValues(getCodeListValues("activity-scope"));
		getFields().add(activityScope);
		getFilterFieldList().add(activityScope);

		Field aidType = new Field("Aid Type", "default-aid-type", FieldType.LIST, true,
				getTranslationForField("default-aid-type"));
		aidType.setPossibleValues(getCodeListValues("default-aid-type"));
		getFields().add(aidType);
		getFilterFieldList().add(aidType);

		Field financeType = new Field("Finance Type", "default-finance-type", FieldType.LIST,
				true, getTranslationForField("default-finance-type"));
		financeType.setPossibleValues(getCodeListValues("default-finance-type"));
		getFields().add(financeType);
		getFilterFieldList().add(financeType);

		Field flowType = new Field("Flow Type", "default-flow-type", FieldType.LIST,
				true, getTranslationForField("default-flow-type"));
		flowType.setPossibleValues(getCodeListValues("default-flow-type"));
		getFields().add(flowType);
		getFilterFieldList().add(flowType);

		Field tiedStatus = new Field("Tied Status", "default-tied-status", FieldType.LIST,
				true, getTranslationForField("default-tied-status"));
		tiedStatus.setPossibleValues(getCodeListValues("default-tied-status"));
		getFields().add(tiedStatus);
		getFilterFieldList().add(tiedStatus);

		Field policyMarker = new Field("PolicyMarker", "policy-marker", FieldType.LIST,
				true, getTranslationForField("policy-marker"));
		policyMarker.setPossibleValues(getCodeListValues("policy-marker"));
		policyMarker.setMultiple(true);
		getFields().add(policyMarker);
		getFilterFieldList().add(policyMarker);

		Field recipientCountry = new Field("Recipient Country", "recipient-country",
				FieldType.RECIPIENT_COUNTRY, true, getTranslationForField("recipient-country"));
		recipientCountry.setPossibleValues(getCodeListValues("recipient-country"));
		recipientCountry.setExclusive(true);
		recipientCountry.setFilterRequired(true);
		getFields().add(recipientCountry);
		getFilterFieldList().add(recipientCountry);

		Field sector = new Field("Sector", "sector", FieldType.LIST, true,
				getTranslationForField("sector"));
		sector.setPossibleValues(getCodeListValues("sector"));
		sector.setMultiple(true);
		sector.setPercentage(true);
		getFields().add(sector);
		getFilterFieldList().add(sector);

		Field location = new Field("Location", "location", FieldType.LOCATION, true,
				getTranslationForField("location"));
		location.setPossibleValues(new ArrayList<FieldValue>());
		location.setMultiple(true);
		location.setPercentage(true);
		getFields().add(location);

		// Dates
		Field activityDateStartPlanned = new Field("Activity Date Start Planned",
				"activity-date", FieldType.DATE, true, getTranslationForField("activity-date"));
		activityDateStartPlanned.setSubType("start-planned");
		getFields().add(activityDateStartPlanned);

		Field activityDateEndPlanned = new Field("Activity Date End Planned", "activity-date",
				FieldType.DATE, true, getTranslationForField("activity-date"));
		getFields().add(activityDateEndPlanned);
		activityDateEndPlanned.setSubType("end-planned");

		Field activityDateStartActual = new Field("Activity Date Start Actual", "activity-date",
				FieldType.DATE, true, getTranslationForField("activity-date"));
		activityDateStartActual.setSubType("start-actual");
		getFields().add(activityDateStartActual);

		Field activityDateEndActual = new Field("Activity Date End Actual", "activity-date",
				FieldType.DATE, true, getTranslationForField("activity-date"));
		getFields().add(activityDateEndActual);
		activityDateEndActual.setSubType("end-actual");

		// Transaction Fields
		Field commitments = new Field("Commitments", "transaction", FieldType.TRANSACTION,
				true, getTranslationForField("transaction"));
		commitments.setSubType("C");
		commitments.setSubTypeCode("2");
		getFields().add(commitments);

		Field disbursements = new Field("Disbursements", "transaction", FieldType.TRANSACTION,
				true, getTranslationForField("transaction"));
		disbursements.setSubType("D");
		disbursements.setSubTypeCode("3");
		getFields().add(disbursements);

		Field expenditure = new Field(Constants.EXPENDITURES, "transaction", FieldType.TRANSACTION,
				true, getTranslationForField("transaction"));
		expenditure.setSubType("E");
		expenditure.setSubTypeCode("4");
		getFields().add(expenditure);


		// Organization Fields
		Field participatingOrg = new Field(Constants.FUNDING_ORG_DISPLAY_NAME, "participating-org",
				FieldType.ORGANIZATION, true, getTranslationForField("participating-org"));
		participatingOrg.setSubType(Constants.ORG_ROLE_FUNDING);
        participatingOrg.setSubTypeCode(Constants.ORG_ROLE_FUNDING_CODE);
		getFields().add(participatingOrg);

		Field accountableOrg = new Field(Constants.ACCOUNTABLE_ORG_DISPLAY_NAME, "participating-org",
				FieldType.ORGANIZATION, true, getTranslationForField("participating-org"));
		accountableOrg.setSubTypeCode(Constants.ORG_ROLE_ACCOUNTABLE_CODE);
        accountableOrg.setSubType(Constants.ORG_ROLE_ACCOUNTABLE);
		getFields().add(accountableOrg);

		Field extendingOrg = new Field(Constants.EXTENDING_ORG_DISPLAY_NAME, "participating-org",
				FieldType.ORGANIZATION, true, getTranslationForField("participating-org"));
		 extendingOrg.setSubTypeCode(Constants.ORG_ROLE_EXTENDING_CODE);
	     extendingOrg.setSubType(Constants.ORG_ROLE_EXTENDING);
		getFields().add(extendingOrg);

		Field implementingOrg = new Field(Constants.IMPLEMENTING_ORG_DISPLAY_NAME, "participating-org",
				FieldType.ORGANIZATION, true, getTranslationForField("participating-org"));
		implementingOrg.setSubTypeCode(Constants.ORG_ROLE_IMPLEMENTING_CODE);
        implementingOrg.setSubType(Constants.ORG_ROLE_IMPLEMENTING);
		getFields().add(implementingOrg);

// Provider Organization, within Transactions
		Field providerOrg = new Field(Constants.PROVIDER_ORG_DISPLAY_NAME, "provider-org",
				FieldType.ORGANIZATION, false, getTranslationForField("provider-org"));
		providerOrg.setSubType("Provider");
		getFields().add(providerOrg);
		getFilterFieldList().add(providerOrg);

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
