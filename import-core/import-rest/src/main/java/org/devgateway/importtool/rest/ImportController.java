package org.devgateway.importtool.rest;

import static org.devgateway.importtool.services.processor.helper.Constants.DESTINATION_PROCESSOR;
import static org.devgateway.importtool.services.processor.helper.Constants.SOURCE_PROCESSOR;
import static org.devgateway.importtool.services.processor.helper.Constants.SESSION_TOKEN;
import static org.devgateway.importtool.services.processor.helper.Constants.DOCUMENT_MAPPER;
import static org.devgateway.importtool.services.processor.helper.Constants.WORKFLOW_LIST;
import static org.devgateway.importtool.services.processor.helper.Constants.CURRENT_FILE_ID;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.services.File;
import org.devgateway.importtool.services.FileRepository;
import org.devgateway.importtool.services.ImportSummary;
import org.devgateway.importtool.services.Project;
import org.devgateway.importtool.services.ProjectRepository;
import org.devgateway.importtool.services.Workflow;
import org.devgateway.importtool.services.WorkflowService;
import org.devgateway.importtool.services.processor.XMLGenericProcessor;
import org.devgateway.importtool.services.processor.helper.ActionResult;
import org.devgateway.importtool.services.processor.helper.DocumentMapper;
import org.devgateway.importtool.services.processor.helper.DocumentMapping;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldValueMapping;
import org.devgateway.importtool.services.processor.helper.IDestinationProcessor;
import org.devgateway.importtool.services.processor.helper.IDocumentMapper;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/import")
class ImportController {

	@Autowired
	private FileRepository repository;
	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	WorkflowService workflowService;

	private Log log = LogFactory.getLog(getClass());

	@RequestMapping(method = RequestMethod.GET, value = "/new/{sourceProcessorName}/{destinationProcessorName}/{authenticationToken}/{userName}")
	ResponseEntity<ImportSessionToken> initiateImport(@PathVariable String sourceProcessorName, @PathVariable String destinationProcessorName, @PathVariable String authenticationToken, @PathVariable String userName,
			HttpServletRequest request) {
		log.debug("Initialized import");
		request.getSession().removeAttribute(SOURCE_PROCESSOR);
		request.getSession().removeAttribute(DESTINATION_PROCESSOR);
		request.getSession().removeAttribute(SESSION_TOKEN);
		request.getSession().removeAttribute(DOCUMENT_MAPPER);
		ISourceProcessor srcProcessor = getSourceProcessor(sourceProcessorName, request);
		request.getSession().setAttribute(SOURCE_PROCESSOR, srcProcessor);

		IDestinationProcessor destProcessor = getDestinationProcessor(destinationProcessorName, authenticationToken, request);
		request.getSession().setAttribute(DESTINATION_PROCESSOR, destProcessor);
		log.debug(srcProcessor.getDescriptiveName());
		log.debug(destProcessor.getDescriptiveName());
		ImportSessionToken importSessionToken = new ImportSessionToken(authenticationToken, userName, new Date(), srcProcessor.getDescriptiveName(), destProcessor.getDescriptiveName());
		request.getSession().setAttribute(SESSION_TOKEN, importSessionToken);

		return new ResponseEntity<>(importSessionToken, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/uploaded")
	public ResponseEntity<List<File>> listFiles(HttpServletRequest request) {
		List<File> fileList = new ArrayList<File>();
		ImportSessionToken importSessionToken = (ImportSessionToken) request.getSession().getAttribute(SESSION_TOKEN);
		if (repository == null || importSessionToken == null) {
			return new ResponseEntity<>(fileList, HttpStatus.OK);
		}
		Iterable<File> list = repository.findBySessionId(importSessionToken.getImportTokenSessionId());
		list.forEach(n -> {
			fileList.add(n);
		});
		return new ResponseEntity<>(fileList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/wipeall", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> wipe(HttpServletRequest request) {
		request.getSession().removeAttribute(SOURCE_PROCESSOR);
		request.getSession().removeAttribute(DESTINATION_PROCESSOR);
		request.getSession().removeAttribute(SESSION_TOKEN);
		request.getSession().removeAttribute(DOCUMENT_MAPPER);
		return new ResponseEntity<>("{'error': ''}", HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/upload")
	public ResponseEntity<String> handleFileUpload(@RequestParam("file_data") MultipartFile file, HttpServletRequest request) {
		if (!file.isEmpty()) {
			try {
				InputStream is = new ByteArrayInputStream(file.getBytes());
				ISourceProcessor srcProcessor = (ISourceProcessor) request.getSession().getAttribute(SOURCE_PROCESSOR);
				ImportSessionToken authToken = (ImportSessionToken) request.getSession().getAttribute(SESSION_TOKEN);
				srcProcessor.setInput(is);
				File uploadedFile = new File();
				uploadedFile.setData(file.getBytes());
				uploadedFile.setCreatedDate(new Date());
				uploadedFile.setFileName(file.getOriginalFilename());
				uploadedFile.setAuthor(authToken.getAuthenticationToken());
				uploadedFile.setSessionId(authToken.getImportTokenSessionId());

				repository.save(uploadedFile);
				request.getSession().setAttribute(CURRENT_FILE_ID, uploadedFile.getId());

				return new ResponseEntity<>("{}", HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<>("{\"error\": \"Error uploading file. Check if the initial steps are done.\"}", HttpStatus.OK);
			}
		} else {
			return new ResponseEntity<>("{\"error\": \"Error uploading file.\"}", HttpStatus.OK);
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/filter")
	ResponseEntity<ImportSessionToken> filter(@PathVariable String authenticationToken) {
		ImportSessionToken authObject = new ImportSessionToken(authenticationToken, "", new Date(), "", null);
		// TODO: Execute the filters
		return new ResponseEntity<>(authObject, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/projects")
	ResponseEntity<List<DocumentMapping>> processedProjects(HttpServletRequest request) {
		ISourceProcessor srcProcessor = (ISourceProcessor) request.getSession().getAttribute(SOURCE_PROCESSOR);
		IDestinationProcessor destProcessor = (IDestinationProcessor) request.getSession().getAttribute(DESTINATION_PROCESSOR);
		IDocumentMapper documentMapper = (IDocumentMapper) request.getSession().getAttribute(DOCUMENT_MAPPER);
		if (documentMapper == null) {
			documentMapper = new DocumentMapper();
			request.getSession().setAttribute(DOCUMENT_MAPPER, documentMapper);
		}

		documentMapper.setSourceProcessor(srcProcessor);
		documentMapper.setDestinationProcessor(destProcessor);
		// if (!documentMapper.isInitialized()) {
		try {
			documentMapper.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// }

		return new ResponseEntity<>(documentMapper.getDocumentMappings(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/execute")
	ResponseEntity<List<ActionResult>> execute(HttpServletRequest request) {
		IDocumentMapper documentMapper = (IDocumentMapper) request.getSession().getAttribute(DOCUMENT_MAPPER);
		if (documentMapper == null) {
			documentMapper = new DocumentMapper();
			request.getSession().setAttribute(DOCUMENT_MAPPER, documentMapper);
		}
		List<ActionResult> results = documentMapper.execute();

		Long fileId = (Long)request.getSession().getAttribute(CURRENT_FILE_ID);
		results.forEach(n -> {
			insertLog(n, fileId);
		});

		return new ResponseEntity<>(results, HttpStatus.OK);
	}

	private void insertLog(ActionResult result, Long id) {
		Project project = new Project();
		File file = repository.findById(id);
		project.setFile(file);
		project.setTitle(result.getMessage());
		project.setNotes(result.getOperation());
		project.setStatus(result.getStatus());
		projectRepository.save(project);
	}

	@Transactional
	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id, HttpServletRequest request) {
		projectRepository.deleteByFileId(id);
		repository.delete(id);
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/summary")
	ResponseEntity<ImportSummary> getSummary(HttpServletRequest request) {
		IDocumentMapper documentMapper = (IDocumentMapper) request.getSession().getAttribute(DOCUMENT_MAPPER);
		if (documentMapper == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		ImportSummary importSummmary = new ImportSummary();
		importSummmary.setProjectCount(documentMapper.getDocumentMappings().size());
		importSummmary.setFieldMappingCount(documentMapper.getFieldMappingObject().size());

		ImportSessionToken importSessionToken = (ImportSessionToken) request.getSession().getAttribute(SESSION_TOKEN);
		if (repository != null || importSessionToken != null) {
			importSummmary.setFileCount(repository.countBySessionId(importSessionToken.getImportTokenSessionId()));
		}

		// filter count
		ISourceProcessor processor = (ISourceProcessor) request.getSession().getAttribute(SOURCE_PROCESSOR);
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
		return new ResponseEntity<>(importSummmary, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	private IDestinationProcessor getDestinationProcessor(String processorName, String authenticationToken,HttpServletRequest request) {
		   IDestinationProcessor processor = null;		  
			List<Workflow> workflows = (List<Workflow>)request.getSession().getAttribute(WORKFLOW_LIST);
			if(workflows == null){
				workflows = workflowService.getWorkflows();	
				request.getSession().setAttribute(WORKFLOW_LIST, workflows);
			}			
			
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

	@SuppressWarnings("unchecked")
	private ISourceProcessor getSourceProcessor(String processorName, HttpServletRequest request) {		
		ISourceProcessor processor = null;		
		List<Workflow> workflows = (List<Workflow>)request.getSession().getAttribute(WORKFLOW_LIST);
		
		if(workflows == null){
			workflows = workflowService.getWorkflows();	
			request.getSession().setAttribute(WORKFLOW_LIST, workflows);
		}
				
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

}
