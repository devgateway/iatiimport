package org.devgateway.importtool.services.processor.helper;

import java.util.List;

public interface IDocumentMapper {

	void setSourceProcessor(ISourceProcessor srcProcessor);

	void setDestinationProcessor(IDestinationProcessor destProcessor);

	void initialize() throws Exception;

	List<DocumentMapping> getDocumentMappings();

	void setDocumentMappings(List<DocumentMapping> mappings);

	boolean isInitialized();

	void setInitialized(boolean isInitialized);

	List<FieldMapping> getFieldMappingObject();

	void setFieldMappingObject(List<FieldMapping> fieldMappingObject);

	List<FieldValueMapping> getValueMappingObject();

	void setValueMappingObject(List<FieldValueMapping> valueMappingObject);

	List<ActionResult> execute();
	
	
	void setDocumentMappingStatus(Status documentMappingStatus);
	Status getDocumentMappingStatus();

	List<ActionResult> getResults();

	ActionStatus getImportStatus();


	

}
