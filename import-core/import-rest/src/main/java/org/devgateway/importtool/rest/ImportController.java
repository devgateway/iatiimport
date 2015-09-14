package org.devgateway.importtool.rest;

import static org.devgateway.importtool.services.processor.helper.Constants.DESTINATION_PROCESSOR;
import static org.devgateway.importtool.services.processor.helper.Constants.SOURCE_PROCESSOR;
import static org.devgateway.importtool.services.processor.helper.Constants.SESSION_TOKEN;
import static org.devgateway.importtool.services.processor.helper.Constants.DOCUMENT_MAPPER;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.services.File;
import org.devgateway.importtool.services.FileRepository;
import org.devgateway.importtool.services.ImportSummary;
import org.devgateway.importtool.services.ProjectRepository;
import org.devgateway.importtool.services.processor.AMPStaticProcessor;
import org.devgateway.importtool.services.processor.IATI104Processor;
import org.devgateway.importtool.services.processor.IATI105Processor;
import org.devgateway.importtool.services.processor.IATI201Processor;
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

	private Log log = LogFactory.getLog(getClass());

	@RequestMapping(method = RequestMethod.GET, value = "/new/{sourceProcessorName}/{destinationProcessorName}/{authenticationToken}/{userName}")
	ResponseEntity<ImportSessionToken> initiateImport(@PathVariable String sourceProcessorName, @PathVariable String destinationProcessorName, @PathVariable String authenticationToken, @PathVariable String userName, HttpServletRequest request) {
		log.debug("Initialized import");
		request.getSession().removeAttribute(SOURCE_PROCESSOR);
		request.getSession().removeAttribute(DESTINATION_PROCESSOR);
		request.getSession().removeAttribute(SESSION_TOKEN);
		request.getSession().removeAttribute(DOCUMENT_MAPPER);
		ISourceProcessor srcProcessor = getSourceProcessor(sourceProcessorName);
		request.getSession().setAttribute(SOURCE_PROCESSOR, srcProcessor);

		IDestinationProcessor destProcessor = getDestinationProcessor(destinationProcessorName, authenticationToken);
		request.getSession().setAttribute(DESTINATION_PROCESSOR, destProcessor);

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
		try {
			// repository.deleteAll();
			// Iterable<File> list = repository.findAll();
			// repository.delete(list);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("{ 'error': ' " + e.getMessage() + "'}", HttpStatus.SERVICE_UNAVAILABLE);
		}
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
		ImportSessionToken authObject = new ImportSessionToken(authenticationToken,"", new Date(), "", null);
		//TODO: Execute the filters
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
//		if (!documentMapper.isInitialized()) {
			try {
				documentMapper.initialize();
			} catch (Exception e) {
				e.printStackTrace();
			}
	//	}

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

		// List<ActionResult> results = new ArrayList<ActionResult>();
		// results.add(new ActionResult("1", "INSERT", "ok", "project 1"));
		// results.add(new ActionResult("2", "INSERT", "ok", "project 2"));
		// results.add(new ActionResult("3", "INSERT", "ok", "project 3"));
		// results.add(new ActionResult("4", "INSERT", "ok", "project 4"));
		//
		return new ResponseEntity<>(results, HttpStatus.OK);
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

		//filter count
		ISourceProcessor processor = (ISourceProcessor) request.getSession().getAttribute(SOURCE_PROCESSOR);
		if (processor != null) {			
			List<Field> fields = processor.getFilterFields();
			importSummmary.setFilterCount(fields.stream().filter(f -> f.getFilters().size() > 0).count());		
		}
		
		
		//value mapping count
		int mappedValuesCount = 0;				
		for(FieldValueMapping mapping : documentMapper.getValueMappingObject()){			
			for (Map.Entry<Integer, Integer> entry : mapping.getValueIndexMapping().entrySet())
			{
				if(entry.getValue() != null){
					++mappedValuesCount;
				}
			    
			}
		}
		importSummmary.setValueMappingCount(mappedValuesCount);
		
		return new ResponseEntity<>(importSummmary, HttpStatus.OK);
	}
	
	private IDestinationProcessor getDestinationProcessor(String processorName, String authenticationToken) {
		IDestinationProcessor processor;
		switch (processorName) {
		case "AMP":
		default:
			processor = new AMPStaticProcessor(authenticationToken);
			break;
		}
		return processor;
	}

	private ISourceProcessor getSourceProcessor(String processorName) {
		ISourceProcessor processor;
		switch (processorName) {
		case "IATI201":
			processor = new IATI201Processor();
			break;
		case "IATI105":
			processor = new IATI105Processor();
			break;
		case "IATI104":
			processor = new IATI104Processor();
			break;
		case "XMLGeneric":
			processor = new XMLGenericProcessor();
			break;
		default:
			processor = new XMLGenericProcessor();
			break;
		}
		return processor;
	}

}
