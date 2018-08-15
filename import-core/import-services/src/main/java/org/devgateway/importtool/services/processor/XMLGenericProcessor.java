package org.devgateway.importtool.services.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.model.Language;
import org.devgateway.importtool.services.processor.helper.ActionStatus;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.devgateway.importtool.services.processor.helper.InternalDocument;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Component("XMLGeneric")
public class XMLGenericProcessor implements ISourceProcessor {

	private String DEFAULT_ID_FIELD = "xml-identifier";
	private InputStream input;
	private Log log = LogFactory.getLog(getClass());
	private String descriptiveName = "XML Generic";
	private Document doc;


	@Override
	public void setInput(InputStream input) {
		this.input = input;
	}

	@Override
	public void setInput(Document input) {
		this.doc = input;
	}
	@Override
	public List<Field> getFields() {
		List<Field> fieldList = new ArrayList<Field>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setIgnoringElementContentWhitespace(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(input);
			fieldList = extract(doc);
			
			// Do something with the document here.
		} catch (ParserConfigurationException | SAXException | IOException e) {
			log.error("IOException Parsing Source File: " + e);
		}

		return fieldList;
	}

	private List<Field> extract(Document doc) {
		NodeList activities = doc.getElementsByTagName("iati-activity");
		List<Field> list = extractFields(activities);
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

			Field field = new Field();
			field.setFieldName(node.getNodeName());
			//field.appendPossibleValue(node.getNodeName(), node.getNodeValue());
			field.setAttributes(extractAttributes(node));
			if (node.getChildNodes().getLength() > 0) {
				field.setChildFields(extractFields(node.getChildNodes()));
			}
			if (!list.contains(field)) {
				list.add(field);
			}
		}

		return list;
	}

	private Map<String, String> extractAttributes(Node node) {
		Map<String, String> attrs = new HashMap<String, String>();
		NamedNodeMap nmap = node.getAttributes();
	    for (int j = 0; j < nmap.getLength(); j++) {
	    	attrs.put(nmap.item(j).getNodeName(), nmap.item(j).getNodeValue());
	    }
		return attrs;
	}

	@Override
	public List<InternalDocument> getDocuments() {
		List<InternalDocument> list = new ArrayList<InternalDocument>();
		InternalDocument doc1 = new InternalDocument();
		doc1.addStringField("iati-identifier", "1");
		doc1.addStringField("title", "Title 1");
		doc1.addStringField("activity-status", "Status 1");
		list.add(doc1);

		InternalDocument doc2 = new InternalDocument();
		doc2.addStringField("iati-identifier", "2");
		doc2.addStringField("title", "Title 2");
		doc2.addStringField("activity-status", "Status 2");
		list.add(doc2);

		InternalDocument doc3 = new InternalDocument();
		doc3.addStringField("iati-identifier", "3");
		doc3.addStringField("title", "Title 3");
		doc3.addStringField("activity-status", "Status 3");
		list.add(doc3);

		return list;
	}

	@Override
	public String getIdField() {
		return DEFAULT_ID_FIELD;
	}

	@Override
	public List<String> getLanguages() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Field> getFilterFields() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getDescriptiveName() {
		return this.descriptiveName ;
	}

	@Override
	public String getTitleField() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFilterFields(List<Field> fields) {
		// TODO Auto-generated method stub
		
	}

	public Document getDoc() {
		return doc;
	}

	@Override
	public Boolean isValidInput() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<Language> getFilterLanguages() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFilterLanguages(List<Language> filterLanguages) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActionStatus(ActionStatus documentMappingStatus) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ActionStatus getActionStatus() {
		// TODO Auto-generated method stub
		return null;
	}

}
