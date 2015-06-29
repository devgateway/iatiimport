package org.devgateway.importtool.services.processor.helper;

import java.io.InputStream;
import java.util.List;

public interface ISourceProcessor {
	public String getDescriptiveName();

	public void setInput(InputStream input);

	public List<Field> getFields();

	public List<InternalDocument> getDocuments();

	public String getIdField();

	public List<String> getLanguages();

	public List<Field> getFilterFields();

	public String getTitleField();

	//public Boolean isFormatValid();
}
