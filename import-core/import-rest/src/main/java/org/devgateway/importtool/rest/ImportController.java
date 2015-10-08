package org.devgateway.importtool.rest;

import static org.devgateway.importtool.services.processor.helper.Constants.CURRENT_FILE_ID;
import static org.devgateway.importtool.services.processor.helper.Constants.DESTINATION_PROCESSOR;
import static org.devgateway.importtool.services.processor.helper.Constants.DOCUMENT_MAPPER;
import static org.devgateway.importtool.services.processor.helper.Constants.SESSION_TOKEN;
import static org.devgateway.importtool.services.processor.helper.Constants.SOURCE_PROCESSOR;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.dao.FileRepository;
import org.devgateway.importtool.dao.ProjectRepository;
import org.devgateway.importtool.model.File;
import org.devgateway.importtool.model.ImportSummary;
import org.devgateway.importtool.security.ImportSessionToken;
import org.devgateway.importtool.services.ImportService;
import org.devgateway.importtool.services.WorkflowService;
import org.devgateway.importtool.services.processor.helper.ActionResult;
import org.devgateway.importtool.services.processor.helper.DocumentMapper;
import org.devgateway.importtool.services.processor.helper.DocumentMapping;
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
	private WorkflowService workflowService;
	
	@Autowired
	private ImportService importService;

	private Log log = LogFactory.getLog(getClass());

	@RequestMapping(method = RequestMethod.GET, value = "/new/{sourceProcessorName}/{destinationProcessorName}/{authenticationToken}/{userName}")
	ResponseEntity<ImportSessionToken> initiateImport(@PathVariable String sourceProcessorName, @PathVariable String destinationProcessorName, @PathVariable String authenticationToken, @PathVariable String userName,
			HttpServletRequest request) {
		log.debug("Initialized import");
		request.getSession().removeAttribute(SOURCE_PROCESSOR);
		request.getSession().removeAttribute(DESTINATION_PROCESSOR);
		request.getSession().removeAttribute(SESSION_TOKEN);
		request.getSession().removeAttribute(DOCUMENT_MAPPER);
		ISourceProcessor srcProcessor = importService.getSourceProcessor(sourceProcessorName);
		request.getSession().setAttribute(SOURCE_PROCESSOR, srcProcessor);
		IDestinationProcessor destProcessor = importService.getDestinationProcessor(destinationProcessorName, authenticationToken);
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
		return new ResponseEntity<>("{'error': ''}", HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/upload")
	public ResponseEntity<String> handleFileUpload(@RequestParam("file_data") MultipartFile file, HttpServletRequest request) {
		if (!file.isEmpty()) {
			try {				
				ISourceProcessor srcProcessor = (ISourceProcessor) request.getSession().getAttribute(SOURCE_PROCESSOR);
				ImportSessionToken authToken = (ImportSessionToken) request.getSession().getAttribute(SESSION_TOKEN);
				File uploadedFile = importService.uploadFile(file, srcProcessor, authToken);				
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
			importService.insertLog(n, fileId);
		});

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
		ISourceProcessor processor = (ISourceProcessor) request.getSession().getAttribute(SOURCE_PROCESSOR);
		ImportSessionToken importSessionToken = (ImportSessionToken) request.getSession().getAttribute(SESSION_TOKEN);		
		if (documentMapper == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		return new ResponseEntity<>(importService.getSummary(documentMapper, importSessionToken, processor), HttpStatus.OK);
	}

	

}
