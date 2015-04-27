package org.devgateway.importtool.services.processor.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentMapper implements IDocumentMapper {

	// TODO: Defensively prevent operations if any processor is not defined

	private ISourceProcessor sourceProcessor;
	private IDestinationProcessor destinationProcessor;
	private Map<Field, Field> fieldMapping = new HashMap<Field, Field>();
	private Map<String, String> valueMapping = new HashMap<String, String>();
	private Map<InternalDocument, InternalDocument> documentMapping = new HashMap<InternalDocument, InternalDocument>();

	public ISourceProcessor getSourceProcessor() {
		return sourceProcessor;
	}

	public void setSourceProcessor(ISourceProcessor sourceProcessor) {
		this.sourceProcessor = sourceProcessor;
	}

	public IDestinationProcessor getDestinationProcessor() {
		return destinationProcessor;
	}

	public void setDestinationProcessor(
			IDestinationProcessor destinationProcessor) {
		this.destinationProcessor = destinationProcessor;
	}

	public void addFieldMapping(Field sourceField, Field destinationField) {
		fieldMapping.put(sourceField, destinationField);
	}

	public void addValueMapping(String sourceValue, String destinationValue) {
		valueMapping.put(sourceValue, destinationValue);
	}

	public Map<String, String> getValueMapping() {
		return valueMapping;
	}

	public void setValueMapping(Map<String, String> valueMapping) {
		this.valueMapping = valueMapping;
	}

	public Map<Field, Field> getFieldMapping() {
		return fieldMapping;
	}

	public void setFieldMapping(Map<Field, Field> fieldMapping) {
		this.fieldMapping = fieldMapping;
	}


	public void addDocumentMapping(InternalDocument internalDocument,
			InternalDocument internalDocument2) {
		// TODO Auto-generated method stub
		
	}

	// Mapping and transformation operations go here
	public List<MappingResult> execute() {
		List<MappingResult> results = new ArrayList<MappingResult>();
		for(InternalDocument doc : documentMapping.keySet()) {
			results.add(this.destinationProcessor.insertOrUpdate(doc));
		}
		return results;
	}

}
