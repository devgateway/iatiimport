package org.devgateway.importtool.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.model.Workflow;
import org.devgateway.importtool.services.processor.helper.Processor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static org.devgateway.importtool.services.processor.helper.Constants.*;

@org.springframework.stereotype.Service
public class WorkflowService {

	private Log log = LogFactory.getLog(getClass());

	
	public List<Workflow> getWorkflows() {
		List<Workflow> workflows = new ArrayList<Workflow>();
		InputStream stream = this.getClass().getResourceAsStream(WORKFLOW_FILE);
		if (stream != null) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();				
				factory.setIgnoringElementContentWhitespace(true);
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(stream);
				NodeList nodeList = doc.getElementsByTagName(WORKFLOW_TAG_NAME);
				for (int i = 0; i < nodeList.getLength(); i++) {
					Element workflowEl = (Element) nodeList.item(i);
					Element destination = (Element) workflowEl.getElementsByTagName(DESTINATION_PROCESSOR_TAG_NAME).item(0);
					Element source = (Element) workflowEl.getElementsByTagName(SOURCE_PROCESSOR_TAG_NAME).item(0);
					
					Workflow workflow = new Workflow();					
					Processor destinationProcessor = new Processor();
					destinationProcessor.setName(destination.getElementsByTagName(NAME_TAG).item(0).getTextContent());
					destinationProcessor.setLabel(destination.getElementsByTagName(LABEL_TAG).item(0).getTextContent());
					destinationProcessor.setClassName(destination.getElementsByTagName(CLASS_NAME_TAG).item(0).getTextContent());
					workflow.setDestinationProcessor(destinationProcessor);
					
					Processor sourceProcessor = new Processor();
					sourceProcessor.setName(source.getElementsByTagName(NAME_TAG).item(0).getTextContent());
					sourceProcessor.setLabel(source.getElementsByTagName(LABEL_TAG).item(0).getTextContent());
					sourceProcessor.setClassName(source.getElementsByTagName(CLASS_NAME_TAG).item(0).getTextContent());
					workflow.setSourceProcessor(sourceProcessor);
					
					workflow.setDescription(workflowEl.getElementsByTagName(DESCRIPTION_TAG).item(0).getTextContent());
					workflow.setEnabled(Boolean.parseBoolean(workflowEl.getElementsByTagName(ENABLED_TAG).item(0).getTextContent()));
					workflow.setTranslationKey(workflowEl.getElementsByTagName(TRANSLATION_KEY_TAG).item(0).getTextContent());
					workflows.add(workflow);
				}
			} catch (ParserConfigurationException | SAXException | IOException e) {
				log.error("Error Parsing workflow file: " + e);
			}

		}
		return workflows;
	}
}
