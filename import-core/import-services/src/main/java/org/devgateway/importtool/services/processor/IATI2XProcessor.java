package org.devgateway.importtool.services.processor;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.model.Language;
import org.devgateway.importtool.services.processor.helper.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import java.util.*;
import java.util.function.Consumer;

import static org.devgateway.importtool.services.processor.helper.FieldType.TRANSACTION;

@Component("IATI2X")
@Scope("session")
public class IATI2XProcessor extends IATIProcessor {


	private Log log = LogFactory.getLog(getClass());

	private String PROCESSOR_SUPER_VERSION = "2x";

	private List<String> languageList = null;
	// Field names on the source document that hold key information

	private String DEFAULT_TITLE_FIELD = "title";
	protected String descriptiveName = "";

    public IATI2XProcessor(){
		labelsTranslationsLocation = "IATI2X/fields/labels/labels";
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

	protected NodeList getNodeListForCodeListValues(Document doc, String  standardFieldName)
	{
		return doc.getElementsByTagName("codelist-item");
	}
protected String extractNameElementForCodeListValues(Element nameElement) {
	NodeList narrativeNodeList = nameElement.getElementsByTagName("narrative");
	Element narrativeElement = (Element) narrativeNodeList.item(0);
	return narrativeElement.getChildNodes().item(0).getNodeValue();
}

	protected void processMultiLangElementType(XPath xPath, InternalDocument document, Element iatiActivity, String defaultLanguageCode) {
		Consumer<Field> multiLangConsumer = field -> {
				NodeList fieldNodeList;
				String mlStringValue = "";
				NodeList narrativeNodeList;
				Map<String, String> mlv = new HashMap<>();
				fieldNodeList = iatiActivity.getElementsByTagName(field.getFieldName());
				if (fieldNodeList.getLength() > 0) {
					for (int i = 0; i < fieldNodeList.getLength(); i++) {
						if (fieldNodeList.item(i).getParentNode().isSameNode(iatiActivity)) {
							Element titleElement = (Element) fieldNodeList.item(i);
							narrativeNodeList = titleElement.getElementsByTagName("narrative");
							for (int j = 0; j < narrativeNodeList.getLength(); j++) {
								Element narrativeElement = (Element) narrativeNodeList.item(j);
								if (narrativeElement.getChildNodes().getLength() == 1) {
									mlStringValue = narrativeElement.getChildNodes().item(0).getNodeValue();
									if (mlStringValue != null && !("".equals(mlStringValue))) {
										if (!"".equals(narrativeElement.getAttribute("xml:lang"))) {
											String languageCode = narrativeElement.getAttribute("xml:lang");
											Optional<Language> selectedLanguage = this.getFilterLanguages().stream().filter(language -> languageCode.equalsIgnoreCase(language.getCode()) && language.getSelected() == true).findFirst();
											if (selectedLanguage.isPresent()) {
												mlv.put(languageCode, mlStringValue);
											}
										} else {
											mlv.put(defaultLanguageCode, mlStringValue);
										}
									}
								} else {
									if (mlStringValue != null && !("".equals(mlStringValue))) {
										mlv.put(defaultLanguageCode, mlStringValue);
									}
								}
							}
						}
					}
				}
				document.addMultilangStringField(field.getFieldName(), mlv);
		};
		processForEachFilteredByType(multiLangConsumer, FieldType.MULTILANG_STRING);
	}



	protected String extractProvidingOrganization(Element providerNode){
		return (providerNode != null
				&& providerNode.getElementsByTagName("narrative").item(0) != null)
				? providerNode.getElementsByTagName("narrative").item(0).getTextContent()
				: "";
	}
	protected String extractReceivingOrganization(Element e) {
		return extractNarrative(e, "receiver-org");
	}

	@Override
    protected String getDateSubtype(Field field) {
        return dateTypes.get(field.getSubType());
    }

    @Override
    protected String getNameFromElement(Element fieldElement) {
        String name = extractNarrative(fieldElement, "name");
        if (StringUtils.isBlank(name)) {
            name = extractNarrative(fieldElement, "description");
        }
        return name;
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

	@Override
	protected void instantiateStaticFields() {
		super.instantiateStaticFields();
		Field expenditure = new Field(Constants.EXPENDITURES, "transaction", TRANSACTION,
				true, getTooltipForField("transaction_E"), getLabelsForField("transaction_E"));
		expenditure.setSubType("E");
		expenditure.setSubTypeCode("4");
		getFields().add(expenditure);
	}

    protected String getStringOrgValue(Element fieldElement) {

        return fieldElement.getElementsByTagName("narrative").item(0) != null ?
                fieldElement.getElementsByTagName("narrative").item(0).getTextContent() : "";
    }
}
