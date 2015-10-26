package org.devgateway.importtool.services.processor.helper;

import java.io.InputStream;
import java.util.List;

import org.w3c.dom.Document;

public interface ISourceProcessor {
	public String getDescriptiveName();

	public void setInput(InputStream input);

	public List<Field> getFields();

	public List<Field> getFilterFields();

	public void setFilterFields(List<Field> fields);
	
	public List<String> getLanguages();

	public List<InternalDocument> getDocuments() throws Exception;

	public String getIdField();

	public String getTitleField();

	public Document getDoc();

	public Boolean isValidInput();
}
