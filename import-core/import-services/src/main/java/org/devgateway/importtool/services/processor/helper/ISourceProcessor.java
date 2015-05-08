package org.devgateway.importtool.services.processor.helper;

import java.io.InputStream;
import java.util.List;

public interface ISourceProcessor {
	public void setInput(InputStream input);

	public List<Field> getFields();

	public List<InternalDocument> getDocuments();

	public String getIdField();
	
	//public Boolean isFormatValid();
}
