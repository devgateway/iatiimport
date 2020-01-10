package org.devgateway.importtool.services.processor;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.model.Language;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldType;
import org.devgateway.importtool.services.processor.helper.FieldValue;
import org.devgateway.importtool.services.processor.helper.InternalDocument;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Component("IATI1X")
@Scope("session")
abstract public class IATI1XProcessor extends IATIProcessor {

	protected Log log = LogFactory.getLog(getClass());



	// Field names on the source document that hold key information
	protected String DEFAULT_TITLE_FIELD = "title";

	protected String descriptiveName = "";
	private String PROCESSOR_SUPER_VERSION = "1x";

	public IATI1XProcessor(){
		labelsTranslationsLocation = "IATI1X/fields/labels/labels";
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

	protected NodeList getNodeListForCodeListValues(Document doc, String standardFieldName) {
		NodeList nodeList = doc.getElementsByTagName(standardFieldName);
		if (nodeList.getLength() == 0) {
			nodeList = doc.getElementsByTagName("codelist-item");
		}
		return nodeList;
	}
	protected String extractNameElementForCodeListValues(Element nameElement) {
		return  nameElement.getChildNodes().item(0).getNodeValue();

	}

	protected String extractProvidingOrganization(Element providerNode) {
		return (providerNode != null
				&& providerNode.getChildNodes().getLength() > 0)
				? providerNode.getChildNodes().item(0).getNodeValue() : "";
	}

	protected String extractReceivingOrganization(Element e) {
		Element receiverNode = e.getElementsByTagName("receiver-org").item(0) != null
				? (Element) e.getElementsByTagName("receiver-org").item(0) : null;
		return (receiverNode != null
				&& receiverNode.getChildNodes().item(0) != null)
				? receiverNode.getChildNodes().item(0).getNodeValue() : "";
	}

	protected void processMultiLangElementType(XPath xPath, InternalDocument document, Element element, String defaultLanguageCode) {
		Consumer<Field> multiLangConsumer = field -> {
			NodeList fieldNodeList;
			Map<String, String> mlv = new HashMap<>();
			fieldNodeList = element.getElementsByTagName(field.getFieldName());
			for (int k = 0; k < fieldNodeList.getLength(); ++k) {
				Element fieldElement = (Element) fieldNodeList.item(k);
				if (fieldElement.getParentNode().isSameNode(element)) {
					if (fieldElement.getChildNodes().getLength() == 1) {
						String mlStringValue = fieldElement.getChildNodes().item(0).getNodeValue();
						Node langAttr = fieldElement.getAttributes().getNamedItem("xml:lang");
						if (mlStringValue != null && !("".equals(mlStringValue))) {
							if (langAttr != null) {
								String lang = langAttr.getNodeValue();
								Optional<Language> selectedLanguage = this.getFilterLanguages().stream().filter(language -> lang.equalsIgnoreCase(language.getCode()) && language.getSelected() == true).findFirst();
								if (selectedLanguage.isPresent()) {
									mlv.put(lang, mlStringValue);
								}
							} else {
								mlv.put(defaultLanguageCode, mlStringValue);
							}
						}
					}

				}
			}
			document.addMultilangStringField(field.getFieldName(), mlv);
		};
		processForEachFilteredByType(multiLangConsumer, FieldType.MULTILANG_STRING);
	}

	protected void instantiateStaticFields() {
		super.instantiateStaticFields();
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
}
