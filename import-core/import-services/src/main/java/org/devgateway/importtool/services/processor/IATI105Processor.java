package org.devgateway.importtool.services.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

@Component("IATI105")
@Scope("session")
public class IATI105Processor implements ISourceProcessor {

	private Log log = LogFactory.getLog(getClass());

	private List<Field> fieldList = new ArrayList<Field>();
	private List<Field> filterFieldList = new ArrayList<Field>();
	private String DEFAULT_ID_FIELD = "iati-identifier";
	private String DEFAULT_TITLE_FIELD = "title";
	private String descriptiveName = "IATI 1.05";

	private Document doc;

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

	public IATI105Processor() {
		instantiateStaticFields();
	}

	private void instantiateStaticFields() {
		// Text fields
		fieldList.add(new Field("IATI Identifier", "iati-identifier", FieldType.STRING, false));
		fieldList.add(new Field("Title", "title", FieldType.STRING, false));
		fieldList.add(new Field("Description", "description", FieldType.STRING, false));

		// Code Lists
		Field activityStatus = new Field("Activity Status", "activity-status", FieldType.LIST, true);
		activityStatus.setPossibleValues(getCodeListValues("activity-status"));
		fieldList.add(activityStatus);
		filterFieldList.add(activityStatus);

		Field aidType = new Field("Aid Type", "default-aid-type", FieldType.LIST, true);
		aidType.setPossibleValues(getCodeListValues("default-aid-type"));
		fieldList.add(aidType);
		filterFieldList.add(aidType);

		Field financeType = new Field("Finance Type", "default-finance-type", FieldType.LIST, true);
		financeType.setPossibleValues( getCodeListValues("default-finance-type"));
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

		Field sector = new Field("Sector", "sector", FieldType.LIST, true);
		sector.setPossibleValues(getCodeListValues("sector"));
		fieldList.add(sector);
		filterFieldList.add(sector);

		// Dates
		Field activityDateStartPlanned = new Field("Activity Date Start Planned", "activity-date", FieldType.DATE);
		activityDateStartPlanned.setSubType("start-planned");
		fieldList.add(activityDateStartPlanned);

		Field activityDateEndPlanned = new Field("Activity Date End Planned", "activity-date", FieldType.DATE);
		fieldList.add(activityDateEndPlanned);
		activityDateEndPlanned.setSubType("end-planned");

		Field activityDateStartActual = new Field("Activity Date Start Actual", "activity-date", FieldType.DATE);
		activityDateStartActual.setSubType("start-actual");
		fieldList.add(activityDateStartActual);

		Field activityDateEndActual = new Field("Activity Date End Actual", "activity-date", FieldType.DATE);
		fieldList.add(activityDateEndActual);
		activityDateEndActual.setSubType("end-actual");

		//Special Fields
		Field commitments = new Field("Commitments", "transaction", FieldType.TRANSACTION);
		commitments.setSubType("C");
		fieldList.add(commitments);

		Field disbursements = new Field("Disbursements", "transaction", FieldType.TRANSACTION);
		commitments.setSubType("D");
		fieldList.add(disbursements);


	}

	private List<FieldValue> getCodeListValues(String codeListName) {
		String standardFieldName = mappingNameFile.get(codeListName);
		List<FieldValue> possibleValues = new ArrayList<FieldValue>();
		InputStream is = this.getClass().getResourceAsStream(
				"IATI201/codelist/" + standardFieldName + ".xml");
		if (is != null) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				factory.setValidating(true);
				factory.setIgnoringElementContentWhitespace(true);
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(is);
				NodeList nodeList = doc.getElementsByTagName(standardFieldName);
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						Element codeElement = (Element) element
								.getElementsByTagName("code").item(0);
						String code = codeElement.getChildNodes().item(0)
								.getNodeValue();

						Element nameElement = (Element) element
								.getElementsByTagName("name").item(0);
						String name = nameElement.getChildNodes().item(0)
								.getNodeValue();

						FieldValue fv = new FieldValue();
						fv.setCode(code);
						fv.setValue(name);

						possibleValues.add(fv);
					}
				}
			} catch (ParserConfigurationException | SAXException | IOException e) {
				log.error("IOException Parsing Source File: " + e);
			}
		}
		return possibleValues;
	}

	@Override
	public List<Field> getFields() {
		return fieldList;
	}

	@Override
	public void setInput(InputStream input) {
		if (this.doc == null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setValidating(false);
			factory.setIgnoringElementContentWhitespace(true);
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				this.doc = builder.parse(input);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				log.error("IOException Parsing Source File: " + e);
			}
		}
	}

	@Override
	public List<InternalDocument> getDocuments() {
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
		List<String> activityLanguageList = extractLanguage(doc
				.getElementsByTagName("iati-activity"));
		List<String> narrativeLanguageList = extractLanguage(doc
				.getElementsByTagName("narrative"));

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
			return filterFieldList;

		// List<Field> filterFieldListReduced = new ArrayList<Field>();
		for (Field field : filterFieldList) {
			NodeList nodeList = doc.getElementsByTagName(field.getFieldName());
			List<FieldValue> reducedPossibleValues = new ArrayList<FieldValue>();
			switch (field.getType()) {
			case LIST:
				if (nodeList.getLength() > 0) {
					for (int i = 0; i < nodeList.getLength(); i++) {
						Element fieldElement = (Element) nodeList.item(i);
						final String codeValue = fieldElement
								.getAttribute("code");
						Optional<FieldValue> fieldValue = field
								.getPossibleValues().stream().filter(n -> {
									return n.getCode().equals(codeValue);
								}).findFirst();
						if (fieldValue.isPresent()
								&& !reducedPossibleValues
										.stream()
										.filter(n -> {
											return n.getCode().equals(
													fieldValue.get().getCode());
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

	private List<InternalDocument> extractDocuments(Document doc) {
		NodeList nodeList = doc.getElementsByTagName("iati-activity");
		List<InternalDocument> list = new ArrayList<InternalDocument>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			// Creo el nuevo documento.
			InternalDocument document = new InternalDocument();
			Element element = (Element) nodeList.item(i);
			Boolean filterIncluded = false;
			NodeList fieldNodeList;
			for (Field field : getFields()) {
				switch (field.getType()) {
				case LIST:
					String codeValue = "";
					fieldNodeList = element.getElementsByTagName(field
							.getFieldName());
					if (fieldNodeList.getLength() > 0
							&& fieldNodeList.getLength() == 1) {
						Element fieldElement = (Element) fieldNodeList.item(0);
						codeValue = fieldElement.getAttribute("code");
					}
					filterIncluded = includedByFilter(field.getFilters(),
							codeValue);
					document.addStringField(field.getFieldName(), codeValue);
					break;
				case STRING:
					String stringValue = "";
					fieldNodeList = element.getElementsByTagName(field
							.getFieldName());
					if (fieldNodeList.getLength() > 0
							&& fieldNodeList.getLength() == 1) {
						Element fieldElement = (Element) fieldNodeList.item(0);
						if (fieldElement.getChildNodes().getLength() == 1) {
							stringValue = fieldElement.getChildNodes().item(0)
									.getNodeValue();
						} else {
							stringValue = "";
						}
					}
					// filterIncluded = includedByFilter(field.getFilters(),
					// stringValue);
					document.addStringField(field.getFieldName(), stringValue);
					break;
				case MULTILANG_STRING:
					String mlStringValue = "";
					NodeList narrativeNodeList;
					fieldNodeList = element.getElementsByTagName(field
							.getFieldName());
					if (fieldNodeList.getLength() > 0
							&& fieldNodeList.getLength() == 1) {
						Element fieldElement = (Element) fieldNodeList.item(0);

						narrativeNodeList = fieldElement
								.getElementsByTagName("narrative");
						Element narrativeElement = (Element) narrativeNodeList
								.item(0);
						if (narrativeElement.getChildNodes().getLength() == 1) {
							mlStringValue = narrativeElement.getChildNodes()
									.item(0).getNodeValue();
						} else {
							mlStringValue = "";
						}
					}
					// filterIncluded = includedByFilter(field.getFilters(),
					// stringValue);
					document.addStringField(field.getFieldName(), mlStringValue);
					break;
				default:
					break;
				}
			}
			if (filterIncluded) {
				list.add(document);
			}
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

	@Override
	public String getDescriptiveName() {
		return this.descriptiveName;
	}

	private List<String> extractLanguage(NodeList elementsByTagName) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < elementsByTagName.getLength(); i++) {
			Node langAttr = elementsByTagName.item(i).getAttributes()
					.getNamedItem("xml:lang");
			if (langAttr == null)
				continue;
			String lang = langAttr.getNodeValue();
			if (!list.contains(lang)) {
				list.add(lang);
			}
		}
		return list;
	}

	@Override
	public void setFilterFields(List<Field> fields) {
		filterFieldList = fields;
	}

}
