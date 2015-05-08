package org.devgateway.importtool.services.processor.helper;

import java.util.List;

public interface IDestinationProcessor {
	public void setAuthenticationToken(String authToken);
	
	public List<Field> getFields();

	public List<InternalDocument> getDocuments();

	public String getIdField();
	
	public MappingResult insertOrUpdate(InternalDocument doc);
}
