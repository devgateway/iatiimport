package org.devgateway.importtool.services.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldValue;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.devgateway.importtool.services.processor.helper.InternalDocument;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Component("IATI201")
@Scope("session")
public class IATI201Processor implements ISourceProcessor {

	private String DEFAULT_ID_FIELD = "iati-identifier";
	private Log log = LogFactory.getLog(getClass());
	private Document doc;
	private static Map<String, String> mappingNameFile = new HashMap<String, String>();
	private String descriptiveName = "IATI 2.01";
	static {
		mappingNameFile.put("activity-status", "ActivityStatus");
		mappingNameFile.put("activity-scope", "ActivityScope");
	}

	private static Map<String, Boolean> fieldsEnabled = new HashMap<String, Boolean>();
	static {
		fieldsEnabled.put("activity-status", true);
		fieldsEnabled.put("activity-scope", true);
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
	public List<Field> getFields() {
		List<Field> fieldList = new ArrayList<Field>();
		fieldList = extract(doc);
		processPossibleValues(fieldList);

		return fieldList;
	}

	private void processPossibleValues(List<Field> fieldList) {
		for (Field field : fieldList) {
			List<FieldValue> possibleValues = new ArrayList<FieldValue>();
			String fieldName = field.getFieldName();
			String standardFieldName = mappingNameFile.get(fieldName);
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
					NodeList nodeList = doc
							.getElementsByTagName(standardFieldName);
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
				} catch (ParserConfigurationException | SAXException
						| IOException e) {
					log.error("IOException Parsing Source File: " + e);
				}
			}
			field.setPossibleValues(possibleValues);
		}
	}

	private List<Field> extract(Document doc) {
		NodeList activities = doc.getElementsByTagName("iati-activity");
		List<Field> list = new ArrayList<Field>();
		for (int i = 0; i < activities.getLength(); i++) {
			NodeList childFields = activities.item(i).getChildNodes();
			list.addAll(extractFields(childFields));
		}
		return list;
	}

	/****
	 * Recursive function. Proceed with caution.
	 * 
	 * @param nodeList
	 * @return
	 */
	private List<Field> extractFields(NodeList nodeList) {
		List<Field> list = new ArrayList<Field>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() != Node.TEXT_NODE) {
				Field field = new Field();
				if (fieldsEnabled.containsKey(node.getNodeName())) {
					field.setFieldName(node.getNodeName());
					field.setAttributes(extractAttributes(node));
					if (node.getChildNodes().getLength() > 0) {
						field.setChildFields(extractFields(node.getChildNodes()));
					}
					if (!list.contains(field)) {
						list.add(field);
					}
				}
			}
		}

		return list;
	}

	private Map<String, String> extractAttributes(Node node) {
		Map<String, String> attrs = new HashMap<String, String>();
		NamedNodeMap nmap = node.getAttributes();
		for (int j = 0; nmap != null && j < nmap.getLength(); j++) {
			attrs.put(nmap.item(j).getNodeName(), nmap.item(j).getNodeValue());
		}
		return attrs;
	}

	@Override
	public List<InternalDocument> getDocuments() {
		List<InternalDocument> docList = new ArrayList<InternalDocument>();
		docList = extractDocuments(doc);
		return docList;
	}

	private List<InternalDocument> extractDocuments(Document doc) {
		NodeList nodeList = doc.getElementsByTagName("iati-activity");
		List<InternalDocument> list = new ArrayList<InternalDocument>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			InternalDocument document = new InternalDocument();
			Node node = nodeList.item(i);
			Element element = (Element) node;
			for (int j = 0; j < element.getChildNodes().getLength(); j++) {
				if (element.getChildNodes().item(j).getNodeType() == Node.ELEMENT_NODE) {
					Element currentNode = (Element) element.getChildNodes()
							.item(j);
					if (currentNode.getChildNodes().getLength() == 1) {
						document.addStringField(currentNode.getNodeName(),
								currentNode.getChildNodes().item(0)
										.getNodeValue());
					} else if (currentNode.getChildNodes().getLength() > 1) {
						NodeList narratives = currentNode
								.getElementsByTagName("narrative");
						for (int k = 0; k < narratives.getLength(); k++) {
							Element narrative = (Element) narratives.item(k);
							String suffix = "".equals(narrative
									.getAttribute("xml:lang")) ? "" : "_"
									+ narrative.getAttribute("xml:lang");
							document.addStringField(currentNode.getNodeName()
									+ suffix, narrative.getChildNodes().item(0)
									.getNodeValue());
						}

					}
				}
			}

			list.add(document);
		}

		return list;
	}

	@Override
	public String getIdField() {
		return DEFAULT_ID_FIELD;
	}

	@Override
	public List<String> getLanguages() {
		// The strategy for getting the languages included in the file is to
		// parse the file, from the xml:lang attribute
		Document doc = this.doc;
		List<String> list = new ArrayList<String>();

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
	public List<Field> getFilterFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescriptiveName() {
		return this.descriptiveName;
	}

	@Override
	public String getTitleField() {
		// TODO Auto-generated method stub
		return null;
	}

}
