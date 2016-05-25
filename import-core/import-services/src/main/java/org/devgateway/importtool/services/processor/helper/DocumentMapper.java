package org.devgateway.importtool.services.processor.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class DocumentMapper implements IDocumentMapper {

	// TODO: Defensively prevent operations if any processor is not defined

	private ISourceProcessor sourceProcessor;
	private IDestinationProcessor destinationProcessor;
	private List<FieldMapping> fieldMappingObject = new ArrayList<FieldMapping>();
	private List<FieldValueMapping> valueMappingObject = new ArrayList<FieldValueMapping>();
	private List<DocumentMapping> documentMappings = new ArrayList<DocumentMapping>();
	private boolean isInitialized = false;
	private ActionStatus importStatus;
	private ActionStatus documentMappingStatus;
		
	public ActionStatus getImportStatus() {
		return importStatus;
	}

	public void setImportStatus(ActionStatus importStatus) {
		this.importStatus = importStatus;
	}

	
	private List<ActionResult> results = new ArrayList<ActionResult>();
		
	public List<ActionResult> getResults() {
		return results;
	}

	public void setResults(List<ActionResult> results) {
		this.results = results;
	}

	public ActionStatus getDocumentMappingStatus() {
		return documentMappingStatus;
	}

	public void setDocumentMappingStatus(ActionStatus documentMappingStatus) {
		this.documentMappingStatus = documentMappingStatus;
	}

	public ISourceProcessor getSourceProcessor() {
		return sourceProcessor;
	}

	public void setSourceProcessor(ISourceProcessor sourceProcessor) {
		this.sourceProcessor = sourceProcessor;
	}

	public IDestinationProcessor getDestinationProcessor() {
		return destinationProcessor;
	}

	public void setDestinationProcessor(IDestinationProcessor destinationProcessor) {
		this.destinationProcessor = destinationProcessor;
	}

	// Mapping and transformation operations go here
	@Override
	public List<ActionResult> execute() {	
		this.destinationProcessor.preImportProcessing(this.documentMappings);
		importStatus  = new ActionStatus(Constants.IMPORT_STATUS_MESSAGE, documentMappings.stream().filter(m -> m.getSelected() == true).count());
		importStatus.setStatus(Status.IN_PROGRESS);
		
		results = new ArrayList<ActionResult>();
		for (DocumentMapping doc : documentMappings) {
			if (doc.getSelected()) {
				importStatus.incrementProcessed();
				results.add(processDocumentMapping(doc));
			}
		}
		importStatus.setStatus(Status.COMPLETED);		
		return results;
	}

	private ActionResult processDocumentMapping(DocumentMapping doc) {
		InternalDocument source = doc.getSourceDocument();
		InternalDocument destination = doc.getDestinationDocument();
		ActionResult result = null;
		switch (doc.getOperation()) {
		case INSERT:
			// For now, we pass the mapping. Find a better more efficient way.
			result = this.destinationProcessor.insert(source, this.getFieldMappingObject(), this.getValueMappingObject());
			break;
		case UPDATE:
			result = this.destinationProcessor.update(source, destination, this.getFieldMappingObject(), this.getValueMappingObject(),doc.isOverrideTitle());
			break;
		case NOOP:
			break;
		default:
			break;
		}

		return result;
	}

	public void setValueMapping(Field firstFieldSource, String valueSrc, String valueDest) {

	}

	public void initialize() throws Exception {
		if (sourceProcessor == null || destinationProcessor == null) {
			throw new Exception("Missing prerequirements to initialize this mapping");
		}
				
		this.setDocumentMappings(new ArrayList<DocumentMapping>());		
		// Get the document lists and field that will be used for matching and
		// prepare the list of documents to be updated
		
		documentMappingStatus = new ActionStatus("Extracting project %s of %s", 0L);
		documentMappingStatus.setStatus(Status.IN_PROGRESS);
		sourceProcessor.setActionStatus(documentMappingStatus);
		List<InternalDocument> sourceDocuments = sourceProcessor.getDocuments();
		
		documentMappingStatus = new ActionStatus("Fetching destination projects", 0L);
		documentMappingStatus.setStatus(Status.IN_PROGRESS);		
		destinationProcessor.setActionStatus(documentMappingStatus);
		
		List<InternalDocument> destinationDocuments = destinationProcessor.getDocuments(false);				
		documentMappingStatus = new ActionStatus("Mapping %s of %s", Long.valueOf(sourceDocuments.size()));
		documentMappingStatus.setStatus(Status.IN_PROGRESS);				
		for (InternalDocument srcDoc : sourceDocuments) {
			documentMappingStatus.incrementProcessed();
			String sourceIdField = sourceProcessor.getIdField();
			String destinationIdField = destinationProcessor.getIdField();
			String sourceIdValue = srcDoc.getStringFields().get(sourceIdField);
			srcDoc.setIdentifier(sourceIdValue);
			Optional<InternalDocument> optionalDestDoc = destinationDocuments.stream().filter(n -> {
				return sourceIdValue.equals(n.getStringFields().get(destinationIdField));
			}).findFirst();
			if (optionalDestDoc.isPresent()) {
				InternalDocument destDoc = optionalDestDoc.get();
				String destinationIdValue = (String) destDoc.getStringFields().get(destinationIdField);
				destDoc.setIdentifier(destinationIdValue);
				addDocumentMapping(srcDoc, destDoc, OperationType.UPDATE);
			} else {
				addDocumentMapping(srcDoc, null, OperationType.INSERT);
			}
		}
		
	    documentMappingStatus.setStatus(Status.COMPLETED);
		this.setInitialized(true);
	}
   
	private void addDocumentMapping(InternalDocument srcDoc, InternalDocument destDoc, OperationType operation) {
		// Look for an existing src mapping
		Optional<DocumentMapping> mapping = this.getDocumentMappings().stream().filter(n -> {
			return n.getSourceDocument().getIdentifier().equals(srcDoc.getIdentifier());
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

	@Override
	public boolean isInitialized() {
		return isInitialized;
	}

	@Override
	public void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}

	@Override
	public List<FieldMapping> getFieldMappingObject() {
		return fieldMappingObject;
	}

	@Override
	public void setFieldMappingObject(List<FieldMapping> fieldMappingObject) {
		this.fieldMappingObject = fieldMappingObject;
	}

	@Override
	public List<FieldValueMapping> getValueMappingObject() {
		return valueMappingObject;
	}

	@Override
	public void setValueMappingObject(List<FieldValueMapping> valueMappingObject) {
		this.valueMappingObject = valueMappingObject;
	}

	@Override
	public void setDocumentMappingStatus(Status documentMappingStatus) {
		// TODO Auto-generated method stub
		
	}

}
