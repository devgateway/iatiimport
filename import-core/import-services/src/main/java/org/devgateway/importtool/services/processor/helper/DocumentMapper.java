package org.devgateway.importtool.services.processor.helper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.endpoint.ApiMessage;
import org.devgateway.importtool.endpoint.EPMessages;
import org.devgateway.importtool.exceptions.CurrencyNotFoundException;
import org.devgateway.importtool.exceptions.MissingPrerequisitesException;
import org.devgateway.importtool.services.request.ImportRequest;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.util.StopWatch;


public class DocumentMapper implements IDocumentMapper {

	// TODO: Defensively prevent operations if any processor is not defined

	private ISourceProcessor sourceProcessor;
	private IDestinationProcessor destinationProcessor;
	private List<FieldMapping> fieldMappingObject = new ArrayList<FieldMapping>();
	private List<FieldValueMapping> valueMappingObject = new ArrayList<FieldValueMapping>();
	private List<DocumentMapping> documentMappings = new ArrayList<DocumentMapping>();
	private Map<String, Set<String>> valuesInSelectedProjects;
	private boolean isInitialized = false;
	private ActionStatus importStatus;
	private ActionStatus documentMappingStatus;
	private Log logger = LogFactory.getLog(getClass());
	private static Integer SIMILARITY_EDIT_DISTANCE = 10;

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
	public List<ActionResult> execute(ImportRequest importRequest) {	
		this.destinationProcessor.preImportProcessing(this.documentMappings);
		importStatus = new ActionStatus(EPMessages.IMPORT_STATUS_MESSAGE.getDescription(),
				documentMappings.stream().filter(m -> m.getSelected() == true).count(),
				EPMessages.IMPORT_STATUS_MESSAGE.getCode());
		importStatus.setStatus(Status.IN_PROGRESS);
		StopWatch documentProcessingStopWatch = new StopWatch("document processing");
		results = new ArrayList<>();

		//we only fetch the list of documents that have been selected
		List<DocumentMapping> filtered = documentMappings.stream().filter(d -> d.getSelected())
				.collect(Collectors.toList());
		//for the ones that have destination documents, we go and get the list of ampids
		documentProcessingStopWatch.start("Fetch projects by amp id");

		List<String> listOfAmpIds = filtered.stream().filter(update->
			update.getDestinationDocument()!=null
		).map(doc -> doc.getDestinationDocument().getStringFields().get("internalId")).collect(Collectors.toList());
		documentProcessingStopWatch.stop();
		//with this list of amp_ods we got and load projects from amp

		if(listOfAmpIds !=null && listOfAmpIds.size() >0) {
			this.destinationProcessor.loadProjectsForUpdate(listOfAmpIds);
		}

		documentProcessingStopWatch.start("Document processing");

		filtered.stream().forEach(doc -> {
			// if activity was mapped to an existing AMP activity, modify
			// the operation to UPDATE
			if (doc.getDestinationDocument() != null) {
				doc.setOperation(OperationType.UPDATE);
			}
			try {
				processDocumentMapping(doc, importRequest);
			} catch (ValueMappingException | CurrencyNotFoundException |ParseException e) {
				//we need to find a better way to process exceptions
				results.add(getActionResultFromException(doc, e));
			}
		});
		documentProcessingStopWatch.stop();

		documentProcessingStopWatch.start("Document processing processing");

		results.addAll(this.destinationProcessor.processProjectsInBatch(importStatus));
		documentProcessingStopWatch.stop();
		importStatus.setStatus(Status.COMPLETED);
		//the stop watch will be remove once the ticket is merged into hotfix branch
		logger.error(documentProcessingStopWatch.prettyPrint());
		return results;
	}

	private ActionResult getActionResultFromException(DocumentMapping doc, Exception e) {
		logger.error("Error importing activity ", e);
		return new ActionResult(doc.getSourceDocument().getIdentifier(), "ERROR", "ERROR",
				"Value Mapping Exception" + e.getMessage());
	}

	private void processDocumentMapping(DocumentMapping doc, ImportRequest importRequest)
			throws CurrencyNotFoundException, ValueMappingException, ParseException {
		InternalDocument source = doc.getSourceDocument();
		InternalDocument destination = doc.getDestinationDocument();
		//if source project is mapped to an existing project, update the existing project
		if (destination != null) {
			doc.setOperation(OperationType.UPDATE);
		}

		switch (doc.getOperation()) {
		case INSERT:
			// For now, we pass the mapping. Find a better more efficient way.
			 this.destinationProcessor.insert(source, this.getFieldMappingObject(),
					this.getValueMappingObject(), importRequest);
			break;
		case UPDATE:
			 this.destinationProcessor.update(source, destination, this.getFieldMappingObject(),
					this.getValueMappingObject(), doc.isOverrideTitle(), importRequest);
			break;
		case NOOP:
			break;
		default:
			break;
		}
	}

	public void setValueMapping(Field firstFieldSource, String valueSrc, String valueDest) {

	}

	public void initialize(){
		try{
			if (sourceProcessor == null || destinationProcessor == null) {
				throw new MissingPrerequisitesException("Missing prerequirements to initialize this mapping");
			}
			this.setDocumentMappings(new ArrayList<DocumentMapping>());
			//parse file
			this.updateStatus(EPMessages.PARSING_IN_PROGRESS, Status.IN_PROGRESS);
			this.sourceProcessor.setActionStatus(this.documentMappingStatus);
			List<InternalDocument> sourceDocuments = this.sourceProcessor.getDocuments();

			//fetchFetchFromDataStore destination system projects
			this.updateStatus(EPMessages.FETCHING_DESTINATION_PROJECTS, Status.IN_PROGRESS);
			this.destinationProcessor.setActionStatus(this.documentMappingStatus);
			List<InternalDocument> destinationDocuments = this.destinationProcessor.getDocuments(false);

			//map projects
			this.mapProjects(sourceDocuments, destinationDocuments);
			this.setInitialized(true);
		}catch(MissingPrerequisitesException mpex){
			logger.error("Missing prerequirements to initialize this mapping " + mpex);			
			this.updateStatus(EPMessages.ERROR_EXCTRACTING_PROJECT, Status.FAILED_WITH_ERROR);			
		}catch (Exception e) {
			logger.error("Error parsing document ", e);
			this.updateStatus(EPMessages.ERROR_EXCTRACTING_PROJECT, Status.FAILED_WITH_ERROR);			
		}		
	}
	
	private void mapProjects(List<InternalDocument> sourceDocuments, List<InternalDocument> destinationDocuments) {
		this.updateStatus(EPMessages.MAPPING_STATUS_MESSAGE, Status.IN_PROGRESS);
		for (InternalDocument srcDoc : sourceDocuments) {
			this.documentMappingStatus.incrementProcessed();
			String sourceIdField = this.sourceProcessor.getIdField();
			String destinationIdField = this.destinationProcessor.getIdField();
			String sourceIdValue = srcDoc.getStringFields().get(sourceIdField);
			srcDoc.setIdentifier(sourceIdValue);
			Optional<InternalDocument> optionalDestDoc = destinationDocuments.stream().filter(n -> {
				return sourceIdValue.equals(n.getStringFields().get(destinationIdField));
			}).findFirst();
			if (optionalDestDoc.isPresent()) {
				InternalDocument destDoc = optionalDestDoc.get();
				String destinationIdValue = (String) destDoc.getStringFields().get(destinationIdField);
				destDoc.setIdentifier(destinationIdValue);
				this.addDocumentMapping(srcDoc, destDoc, OperationType.UPDATE);
			} else {
				this.addDocumentMapping(srcDoc, null, OperationType.INSERT,
						this.findProjectsWithSimilarTitle(srcDoc, destinationDocuments));
			}

		}
		this.documentMappingStatus.setStatus(Status.COMPLETED);
	}
    
    private List<InternalDocument> findProjectsWithSimilarTitle(InternalDocument srcDoc, List<InternalDocument> destinationDocuments) {
		Map<String, String> srcTitles = srcDoc.getMultilangFields().get("title");
		List<InternalDocument> similarProjects = new ArrayList<>();
		for (InternalDocument destDoc : destinationDocuments) {
			Map<String, String> destTitles = destDoc.getMultilangFields().get("title");
			Iterator<Entry<String, String>> it = srcTitles.entrySet().iterator();
			boolean foundSimilar = false;
			while (it.hasNext() && foundSimilar == false) {
				Map.Entry item = (Map.Entry) it.next();
				String srcTitle = (String) item.getValue();
				String destTitle = destTitles.get(item.getKey());
				if (srcTitle != null && destTitle != null) {
					int editDistance = LevenshteinDistance.getDefaultInstance().apply(srcTitle, destTitle);
					if (editDistance <= SIMILARITY_EDIT_DISTANCE) {
						foundSimilar = true;
						similarProjects.add(destDoc);
					}
				}
			}
		}

		return similarProjects;
    }
    
    private void updateStatus(ApiMessage message, Status status){
    	if(this.documentMappingStatus == null){
    		this.documentMappingStatus = new ActionStatus();
    	}
    	this.documentMappingStatus.setMessage(message.getDescription());
		this.documentMappingStatus.setCode(message.getCode());
		this.documentMappingStatus.setTotal(0L);
		this.documentMappingStatus.setStatus(status);
    }
	private void addDocumentMapping(InternalDocument srcDoc, InternalDocument destDoc, OperationType operation, List<InternalDocument> projectsWithSimilarTitles) {
		// Look for an existing src mapping
		Optional<DocumentMapping> mapping = this.getDocumentMappings().stream().filter(n -> {
			return n.getSourceDocument().getIdentifier().equals(srcDoc.getIdentifier());
		}).findFirst();
		if (!mapping.isPresent()) {
			DocumentMapping newMapping = new DocumentMapping();
			newMapping.setSourceDocument(srcDoc);
			newMapping.setDestinationDocument(destDoc);
			newMapping.setOperation(operation);
			newMapping.setProjectsWithSimilarTitles(projectsWithSimilarTitles);
			this.documentMappings.add(newMapping);
		}

	}
	
	private void addDocumentMapping(InternalDocument srcDoc, InternalDocument destDoc, OperationType operation) {
		addDocumentMapping(srcDoc, destDoc, operation, null);
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
	
	 /**
     * Get values used in selected projects     * 
     * @param documentMapper
     * @return Map where the key is the field name and the value is a set of all values that are in the selected project for the field
     */
    public Map<String, Set<String>> getValuesUsedInSelectedProjects() {
        valuesInSelectedProjects = new HashMap<>();
        List<DocumentMapping> selectedActivities = getDocumentMappings().stream().filter(m -> { return Boolean.TRUE.equals(m.getSelected());}).collect(Collectors.toList());
        
        for (DocumentMapping docMapping : selectedActivities) {            
            addSelectedStringMultiFieldValues(docMapping);
            addSelectedStringFieldValues(docMapping);
            addSelectedOrganizationFieldValues(docMapping);           
        }
        
        return valuesInSelectedProjects;
    }

    private void addSelectedStringMultiFieldValues(DocumentMapping docMapping) {
        for (Entry<String, String[]> entry : docMapping.getSourceDocument().getStringMultiFields().entrySet()) {
            Set<String> valuesSet = valuesInSelectedProjects.get(entry.getKey());               
            if (valuesSet == null) {
                valuesSet = new HashSet<>();
                valuesInSelectedProjects.put(entry.getKey(), valuesSet);
            }                       
            
            String[] stringValues = (String[]) entry.getValue();
            if (stringValues != null ) {
                valuesSet.addAll(Arrays.asList(entry.getValue())); 
            }               
        }   
    }
    
    private void addSelectedStringFieldValues(DocumentMapping docMapping) {
        for (Entry<String, String> entry : docMapping.getSourceDocument().getStringFields().entrySet()) {
            Set<String> valuesSet = valuesInSelectedProjects.get(entry.getKey());               
            if (valuesSet == null) {
                valuesSet = new HashSet<>();
                valuesInSelectedProjects.put(entry.getKey(), valuesSet);
            }                       
            
            String stringValue = (String) entry.getValue();
            if (stringValue != null ) {
                valuesSet.add(entry.getValue()); 
            }               
        }  
    }
    
    private void addSelectedOrganizationFieldValues(DocumentMapping docMapping) {
        for (Entry<String, Map<String, String>> entry : docMapping.getSourceDocument().getOrganizationFields().entrySet()) {
            
            String fieldKey = entry.getKey().substring(0, entry.getKey().lastIndexOf("_"));                
            Set<String> valuesSet = valuesInSelectedProjects.get(fieldKey);               
            if (valuesSet == null) {
                valuesSet = new HashSet<>();
                valuesInSelectedProjects.put(fieldKey, valuesSet);
            }                       
            
            Map<String, String> values =  entry.getValue();
            if (values != null ) {
                for (Entry<String, String> org : values.entrySet()) {                         
                    if ("value".equals(org.getKey())) {                           
                        valuesSet.add(org.getValue());
                    }
                      
                }
                 
            }               
        }    
    }    
}
