package org.devgateway.importtool.services.processor.helper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.devgateway.importtool.services.request.ImportRequest;

public interface IDocumentMapper {

	void setSourceProcessor(ISourceProcessor srcProcessor);

	void setDestinationProcessor(IDestinationProcessor destProcessor);

	void initialize();

	List<DocumentMapping> getDocumentMappings();

	void setDocumentMappings(List<DocumentMapping> mappings);

	boolean isInitialized();

	void setInitialized(boolean isInitialized);

	List<FieldMapping> getFieldMappingObject();

	void setFieldMappingObject(List<FieldMapping> fieldMappingObject);

	List<FieldValueMapping> getValueMappingObject();

	void setValueMappingObject(List<FieldValueMapping> valueMappingObject);

	List<ActionResult> execute(ImportRequest importRequest);
	
	
	void setDocumentMappingStatus(Status documentMappingStatus);
	ActionStatus getDocumentMappingStatus();

	List<ActionResult> getResults();

	ActionStatus getImportStatus();

    Map<String, Set<String>> getValuesUsedInSelectedProjects();   
}
