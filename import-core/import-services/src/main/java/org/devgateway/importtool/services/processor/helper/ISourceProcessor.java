package org.devgateway.importtool.services.processor.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.devgateway.importtool.model.Language;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

public interface ISourceProcessor {
	public String getDescriptiveName();

	public void setInput(InputStream input);

	public List<Field> getFields();

	public List<Field> getFilterFields();

	public void setFilterFields(List<Field> fields);
	
	public List<String> getLanguages();
	
	public List<Language> getFilterLanguages();
	public void setFilterLanguages(List<Language> filterLanguages);

	public List<InternalDocument> getDocuments() throws Exception;

	public String getIdField();

	public String getTitleField();

	public Document getDoc();

	void setInput(Document input);

	public Boolean isValidInput();

	public void setActionStatus(ActionStatus documentMappingStatus);

	public ActionStatus getActionStatus();


	void setFromDataStore(boolean fromDatastore);

	boolean isFromDataStore();

	 List<String> buildTooltipsFields() throws IOException, SAXException,
			 ParserConfigurationException,XPathExpressionException;
}
