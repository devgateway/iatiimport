package org.devgateway.importtool.services;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.dao.FileRepository;
import org.devgateway.importtool.dao.ProjectRepository;
import org.devgateway.importtool.model.File;
import org.devgateway.importtool.model.ImportSummary;
import org.devgateway.importtool.model.Project;
import org.devgateway.importtool.model.Workflow;
import org.devgateway.importtool.security.ImportSessionToken;
import org.devgateway.importtool.services.processor.config.AmpStaticProcessorConfig;
import org.devgateway.importtool.services.processor.XMLGenericProcessor;
import org.devgateway.importtool.services.processor.config.DestinationProcessorConfiguration;
import org.devgateway.importtool.services.processor.helper.*;
import org.devgateway.importtool.services.processor.helper.FieldType;
import org.devgateway.importtool.services.request.ImportRequest;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@org.springframework.stereotype.Service
public class ImportService {

	@Autowired
	private FileRepository fileRepository;
	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private WorkflowService workflowService;

	@Value("${AMPStaticProcessor.processor_version}")
	private String processorVersion;
	@Autowired
	BeanFactory beanFactory;

	private Log log = LogFactory.getLog(getClass());
	
	public ImportSummary getSummary(IDocumentMapper documentMapper, ImportSessionToken importSessionToken, ISourceProcessor processor){
		ImportSummary importSummmary = new ImportSummary();
		Long projectCount = documentMapper.getDocumentMappings().stream().filter(m -> m.getSelected() == true).count();
		importSummmary.setProjectCount(projectCount);
		importSummmary.setFieldMappingCount(documentMapper.getFieldMappingObject().size());

		boolean transactionFieldMapped = documentMapper.getFieldMappingObject().stream().filter(mapping -> {
		    return FieldType.TRANSACTION.equals(mapping.getSourceField().getType());
		}).count() > 0;
		
		boolean hasTransactionData = documentMapper.getDocumentMappings().stream().filter(docMapping -> {
		    return docMapping.getSourceDocument().getTransactionFields().size() > 0;
		}).count() > 0;
		        
		importSummmary.setHasTransactions(transactionFieldMapped && hasTransactionData);
		
		if (fileRepository != null || importSessionToken != null) {
			importSummmary.setFileCount(fileRepository.countBySessionId(importSessionToken.getImportTokenSessionId()));
		}

		if (processor != null) {
			List<Field> fields = processor.getFilterFields();
			importSummmary.setFilterCount(fields.stream().filter(f -> f.getFilters().size() > 0).count());
		}

		// value mapping count
		int mappedValuesCount = 0;
		for (FieldValueMapping mapping : documentMapper.getValueMappingObject()) {
			for (Map.Entry<Integer, Integer> entry : mapping.getValueIndexMapping().entrySet()) {
				if (entry.getValue() != null) {
					++mappedValuesCount;
				}

			}
		}
		importSummmary.setValueMappingCount(mappedValuesCount);
		return importSummmary;
	}

	/**
	 * 
	 * @param name - name of file or reporting org for automate imports
	 * @param srcProcessor - Source processor
	 * @param authToken - authentication token
	 * @return
	 * @throws IOException
	 */
	public File logImportSession(String name, ISourceProcessor srcProcessor,
						   ImportSessionToken authToken) throws
			IOException{

		File uploadedFile = new File();
		uploadedFile.setCreatedDate(new Date());
		uploadedFile.setFileName(name);
		uploadedFile.setAuthor(authToken.getAuthenticationToken());
		uploadedFile.setSessionId(authToken.getImportTokenSessionId());
		
		Boolean isValid = srcProcessor != null ? srcProcessor.isValidInput() : true;
		uploadedFile.setValid(isValid);		
		fileRepository.save(uploadedFile);
		return uploadedFile;
	}
		
	public void insertLog(ActionResult result, Long id) {
		Project project = new Project();
		File file = fileRepository.findById(id);
		project.setFile(file);
		project.setTitle(result.getMessage());
		project.setNotes(result.getOperation());
		project.setStatus(result.getStatus());
		project.setProjectIdentifier(result.getSourceProjectIdentifier());
		project.setGroupingCriteria(result.getSourceGroupingCriteria());
		project.setLastSyncedOn(new Date());
		projectRepository.save(project);
	}
	
	public void deleteImport(Long id){
		projectRepository.deleteByFileId(id);
		fileRepository.delete(id);
	}
	
	@SuppressWarnings("unchecked")
	public ISourceProcessor getSourceProcessor(String processorName) {		
		ISourceProcessor processor = null;		
		List<Workflow> workflows = workflowService.getWorkflows();;
		Optional<Workflow> optional = workflows.stream().filter(w -> w.getSourceProcessor().getName().equals(processorName)).findFirst();
		if(optional.isPresent()){				
			try {					
				Class<ISourceProcessor> clazz = (Class<ISourceProcessor>)Class.forName(optional.get().getSourceProcessor().getClassName());
				processor = (ISourceProcessor)clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {					
				log.error("Error loading processor class: " + e);
			}

		}		
		if(processor == null){
			processor = new XMLGenericProcessor();
		}
		return processor;
	}
	
	public IDestinationProcessor getDestinationProcessor(String processorName, String authenticationToken) {

		DestinationProcessorConfiguration configuration = null;
		IDestinationProcessor processor = null;
		List<Workflow> workflows = workflowService.getWorkflows();

		Optional<Workflow> optional = workflows.stream().filter(w -> w.getDestinationProcessor().
				getName().equals(processorName)).findFirst();

		if (optional.isPresent()) {
			try {

				configuration = beanFactory.getBean(optional.get().getDestinationProcessor().getName(), DestinationProcessorConfiguration.class);
				//TODO if we have a processor that does not requires this data
				//TODO then the config needs to be taken from a factory to make it generic
				configuration.setAuthenticationToken(authenticationToken);
				configuration.setProcessorVersion(processorVersion);

				Constructor<?> c = Class.forName(optional.get().getDestinationProcessor().getClassName()).getDeclaredConstructor(DestinationProcessorConfiguration.class);
				c.setAccessible(true);
				processor = (IDestinationProcessor) c.newInstance(new Object[]{configuration});

				processor.initialize();


			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException |
					SecurityException | IllegalArgumentException | InvocationTargetException e) {
				//TODO DO NOT swallow this exception, properly handle now that we can notifiy the importer
				//TODO what the problem was
				e.printStackTrace();
				log.error("Error loading destination processor class: " + optional.get().getDestinationProcessor().getClassName() + " " + e);
			}
		}
		return processor;
	}
	
	@Async
	public void initialize(IDocumentMapper documentMapper){		
		documentMapper.initialize();		
	}
	
	@Async
	public void execute(IDocumentMapper documentMapper, Long fileId, ImportRequest importRequest){
		List<ActionResult> results = documentMapper.execute(importRequest);		
		results.forEach(n -> {
			insertLog(n, fileId);
		});
	}
	
}
