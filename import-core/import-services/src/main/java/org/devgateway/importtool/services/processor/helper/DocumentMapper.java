package org.devgateway.importtool.services.processor.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DocumentMapper implements IDocumentMapper {

	// TODO: Defensively prevent operations if any processor is not defined

	private ISourceProcessor sourceProcessor;
	private IDestinationProcessor destinationProcessor;
	private Map<Field, Field> fieldMapping = new HashMap<Field, Field>();
	private Map<Field, Map<FieldValue, FieldValue>> valueMapping = new HashMap<Field, Map<FieldValue, FieldValue>>();
	private List<DocumentMapping> documentMappings = new ArrayList<DocumentMapping>();

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

	public void addValueMapping(Field srcField, FieldValue sourceValue,
			FieldValue destinationValue) {
		Map<FieldValue, FieldValue> mapping = new HashMap<FieldValue, FieldValue>();
		mapping.put(sourceValue, destinationValue);
		valueMapping.put(srcField, mapping);
	}

	public Map<Field, Map<FieldValue, FieldValue>> getValueMapping() {
		return valueMapping;
	}

	public void setValueMapping(
			Map<Field, Map<FieldValue, FieldValue>> valueMapping) {
		this.valueMapping = valueMapping;
	}

	public Map<Field, Field> getFieldMapping() {
		return fieldMapping;
	}

	public void setFieldMapping(Map<Field, Field> fieldMapping) {
		this.fieldMapping = fieldMapping;
	}

	// Mapping and transformation operations go here
	public List<ActionResult> execute() {
		List<ActionResult> results = new ArrayList<ActionResult>();
		// // Key: Source Document
		// // Value: Dest Document
		for (DocumentMapping doc : documentMappings) {
			results.add(processDocumentMapping(doc));
		}
		return results;
	}

	private ActionResult processDocumentMapping(DocumentMapping doc) {
		InternalDocument source = doc.getSourceDocument();
		InternalDocument destination = doc.getDestinationDocument();
		ActionResult result = null;
		switch (doc.getOperation()) {
		case INSERT:
			result = this.destinationProcessor.insert(source);
			break;
		case UPDATE:
			result = this.destinationProcessor.update(source, destination);
			break;
		case NOOP:
			break;
		default:
			break;
		}

		return result;
	}

	public void setValueMapping(Field firstFieldSource, String valueSrc,
			String valueDest) {

	}

	public void initialize() throws Exception {
		if (sourceProcessor == null || destinationProcessor == null) {
			throw new Exception(
					"Missing prerequirements to initialize this mapping");
		}

		// Get the document lists and field that will be used for matching and
		// prepare the list of documents to be updated
		List<InternalDocument> sourceDocuments = sourceProcessor.getDocuments();
		List<InternalDocument> destinationDocuments = destinationProcessor
				.getDocuments();

		for (InternalDocument srcDoc : sourceDocuments) {
			String sourceIdField = sourceProcessor.getIdField();
			String sourceTitleField = sourceProcessor.getTitleField();
			String destinationIdField = destinationProcessor.getIdField();

			String sourceIdValue = (String) srcDoc.getField(sourceIdField);
			String sourceTitleValue = (String) srcDoc
					.getField(sourceTitleField);
			srcDoc.setIdentifier(sourceIdValue);
			srcDoc.setTitle(sourceTitleValue);
			Optional<InternalDocument> optionalDestDoc = destinationDocuments
					.stream()
					.filter(n -> {
						return sourceIdValue.equals(n
								.getField(destinationIdField));
					}).findFirst();
			if (optionalDestDoc.isPresent()) {
				InternalDocument destDoc = optionalDestDoc.get();
				String destinationTitleField = destinationProcessor.getTitleField();
				String destinationIdValue = (String) destDoc.getField(destinationIdField);
				String destinationTitleValue = (String) destDoc.getField(destinationTitleField);
				destDoc.setIdentifier(destinationIdValue);
				destDoc.setTitle(destinationTitleValue);
				addDocumentMapping(srcDoc, destDoc, OperationType.UPDATE);
			} else {
				addDocumentMapping(srcDoc, null, OperationType.INSERT);
			}
		}
	}

	private void addDocumentMapping(InternalDocument srcDoc,
			InternalDocument destDoc, OperationType operation) {
		// Look for an existing src mapping
		Optional<DocumentMapping> mapping = this.getDocumentMappings().stream()
				.filter(n -> {
					return n.getSourceDocument() == srcDoc;
				}).findFirst();
		if (!mapping.isPresent()) {
			DocumentMapping newMapping = new DocumentMapping();
			newMapping.setSourceDocument(srcDoc);
			newMapping.setDestinationDocument(destDoc);
			newMapping.setOperation(operation);
			this.documentMappings.add(newMapping);
		}

	}

	public List<DocumentMapping> getDocumentMappings() {
		return documentMappings;
	}

	public void setDocumentMappings(List<DocumentMapping> documentMappings) {
		this.documentMappings = documentMappings;
	}

}
