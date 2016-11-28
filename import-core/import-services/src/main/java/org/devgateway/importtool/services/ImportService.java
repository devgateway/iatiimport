package org.devgateway.importtool.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.dao.FileRepository;
import org.devgateway.importtool.dao.ProjectRepository;
import org.devgateway.importtool.model.File;
import org.devgateway.importtool.model.ImportSummary;
import org.devgateway.importtool.model.Project;
import org.devgateway.importtool.model.Workflow;
import org.devgateway.importtool.security.ImportSessionToken;
import org.devgateway.importtool.services.processor.XMLGenericProcessor;
import org.devgateway.importtool.services.processor.helper.ActionResult;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldValueMapping;
import org.devgateway.importtool.services.processor.helper.IDestinationProcessor;
import org.devgateway.importtool.services.processor.helper.IDocumentMapper;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.multipart.MultipartFile;
@EnableAsync
@org.springframework.stereotype.Service
public class ImportService {
	@Autowired
	private FileRepository fileRepository;
	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private WorkflowService workflowService;
	
	private Log log = LogFactory.getLog(getClass());
	
	public ImportSummary getSummary(IDocumentMapper documentMapper, ImportSessionToken importSessionToken, ISourceProcessor processor){
		ImportSummary importSummmary = new ImportSummary();
		Long projectCount = documentMapper.getDocumentMappings().stream().filter(m -> m.getSelected() == true).count();
		importSummmary.setProjectCount(projectCount);
		importSummmary.setFieldMappingCount(documentMapper.getFieldMappingObject().size());

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

	public File uploadFile(MultipartFile file, ISourceProcessor srcProcessor, ImportSessionToken authToken) throws IOException{
		InputStream is = new ByteArrayInputStream(file.getBytes());
		srcProcessor.setInput(is);
		File uploadedFile = new File();
		//uploadedFile.setData(file.getBytes());
		uploadedFile.setCreatedDate(new Date());
		uploadedFile.setFileName(file.getOriginalFilename());
		uploadedFile.setAuthor(authToken.getAuthenticationToken());
		uploadedFile.setSessionId(authToken.getImportTokenSessionId());
		uploadedFile.setValid(srcProcessor.isValidInput());
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
		   IDestinationProcessor processor = null;		  
			List<Workflow> workflows =  workflowService.getWorkflows();
						
			Optional<Workflow> optional = workflows.stream().filter(w -> w.getDestinationProcessor().getName().equals(processorName)).findFirst();
			if(optional.isPresent()){				
				try {						
					Constructor<?> c = Class.forName(optional.get().getDestinationProcessor().getClassName()).getDeclaredConstructor(String.class);
					c.setAccessible(true);
					processor = (IDestinationProcessor)c.newInstance(new Object[] {authenticationToken});
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
					log.error("Error loading destination processor class: " + optional.get().getDestinationProcessor().getClassName() + " " + e);
				}
			}
			
		return processor;
	}
	
	
	public String initialize(IDocumentMapper documentMapper){
		try {
			documentMapper.initialize();
		} catch (Exception e) {
			e.printStackTrace();
			String error = "An error occurred while extracting projects from the IATI file. Please check the file format";			
			log.error("Error parsing document " + e);	
			return error;
		}	
		return null;
	}
	
	@Async
	public void execute(IDocumentMapper documentMapper, Long fileId){
		List<ActionResult> results = documentMapper.execute();		
		results.forEach(n -> {
			insertLog(n, fileId);
		});
	}
	
}
