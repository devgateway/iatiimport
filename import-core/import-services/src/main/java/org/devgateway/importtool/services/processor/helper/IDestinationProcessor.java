package org.devgateway.importtool.services.processor.helper;

import java.io.InputStream;
import java.util.List;

public interface IDestinationProcessor {

	public List<Field> getFields();

	public List<InternalDocument> getDocuments();

	public String getIdField();
	
	public MappingResult insertOrUpdate(InternalDocument doc);
}
