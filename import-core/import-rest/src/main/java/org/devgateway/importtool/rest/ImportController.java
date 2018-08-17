package org.devgateway.importtool.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.dao.FileRepository;
import org.devgateway.importtool.dao.ProjectRepository;
import org.devgateway.importtool.endpoint.DataFetchServiceConstants;
import org.devgateway.importtool.endpoint.EPMessages;
import org.devgateway.importtool.endpoint.Param;
import org.devgateway.importtool.model.FetchResult;
import org.devgateway.importtool.model.File;
import org.devgateway.importtool.model.ImportSummary;
import org.devgateway.importtool.security.ImportSessionToken;
import org.devgateway.importtool.services.DataFetchService;
import org.devgateway.importtool.services.ImportService;
import org.devgateway.importtool.services.processor.helper.DocumentMapper;
import org.devgateway.importtool.services.processor.helper.IDestinationProcessor;
import org.devgateway.importtool.services.processor.helper.IDocumentMapper;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.devgateway.importtool.services.request.ImportRequest;
import org.devgateway.importtool.services.response.DocumentMappingResponse;
import org.devgateway.importtool.services.response.ImportExecuteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.devgateway.importtool.services.processor.helper.Constants.CURRENT_FILE_ID;
import static org.devgateway.importtool.services.processor.helper.Constants.DESTINATION_PROCESSOR;
import static org.devgateway.importtool.services.processor.helper.Constants.DOCUMENT_MAPPER;
import static org.devgateway.importtool.services.processor.helper.Constants.IATI_STORE_ACTIVITIES;
import static org.devgateway.importtool.services.processor.helper.Constants.SESSION_TOKEN;
import static org.devgateway.importtool.services.processor.helper.Constants.SOURCE_PROCESSOR;


@RestController
@RequestMapping(value = "/import")
class ImportController  {

	@Autowired
	private FileRepository repository;
	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private ImportService importService;
	
	@Autowired
	private DataFetchService dataFetchService;
	
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
		IDestinationProcessor destProcessor = importService.getDestinationProcessor(destinationProcessorName, authenticationToken);
		request.getSession().setAttribute(DESTINATION_PROCESSOR, destProcessor);
		ImportSessionToken importSessionToken = new ImportSessionToken(authenticationToken, userName, new Date(), srcProcessor.getDescriptiveName(), destProcessor.getDescriptiveName());
		request.getSession().setAttribute(SESSION_TOKEN, importSessionToken);
		if(request.getSession().getAttribute("IATI_STORE_ACTIVITIES")!= null) {
            FetchResult fr = (FetchResult)
                    request.getSession().getAttribute("IATI_STORE_ACTIVITIES");
            srcProcessor.setFromDataStore(true);
            srcProcessor.setInput(fr.getActivities());
        }
		request.getSession().setAttribute(SOURCE_PROCESSOR, srcProcessor);
		return new ResponseEntity<>(importSessionToken, HttpStatus.OK);
	}
	@RequestMapping(method = RequestMethod.GET, value = "/refresh/{authenticationToken}")
	public ResponseEntity<List<File>> refreshToken(@PathVariable String authenticationToken, HttpServletRequest request) {
		ImportSessionToken authToken = (ImportSessionToken) request.getSession().getAttribute(SESSION_TOKEN);
		authToken.setAuthenticationToken(authenticationToken);
		IDestinationProcessor destProcessor = (IDestinationProcessor) request.getSession().getAttribute(DESTINATION_PROCESSOR);
		destProcessor.setAuthenticationToken(authenticationToken);

		return new ResponseEntity<>( HttpStatus.OK);
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
				InputStream is = new ByteArrayInputStream(file.getBytes());
				srcProcessor.setInput(is);
				processFile(file.getOriginalFilename(), request,srcProcessor);
				return new ResponseEntity<>("{}", HttpStatus.OK);
			} catch (Exception e) {
				log.error(e);
				return new ResponseEntity<>(EPMessages.ERROR_UPLOADING_FILE_CHECK_INITIAL_STEPS.toString(),
						HttpStatus.OK);
			}
		} else {				
			return new ResponseEntity<>(EPMessages.ERROR_UPLOADING_FILE.toString(), HttpStatus.OK);
		}
	}	
	
	
	private void processFile(String fileName, HttpServletRequest request, ISourceProcessor srcProcessor)
			throws IOException {

		ImportSessionToken authToken = (ImportSessionToken) request.getSession().getAttribute(SESSION_TOKEN);
		File uploadedFile = importService.uploadFile(fileName ,srcProcessor, authToken);
		request.getSession().setAttribute(CURRENT_FILE_ID, uploadedFile.getId());
	}


	
	@RequestMapping(method = RequestMethod.POST, value = "/fetch")
	ResponseEntity<ImportSessionToken> filter(@PathVariable String authenticationToken) {
		ImportSessionToken authObject = new ImportSessionToken(authenticationToken, "", new Date(), "", null);
		// TODO: Execute the filters
		return new ResponseEntity<>(authObject, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/initialize")
	 ResponseEntity<String> processedProjects(HttpServletRequest request) {
		ISourceProcessor srcProcessor = (ISourceProcessor) request.getSession().getAttribute(SOURCE_PROCESSOR);
		IDestinationProcessor destProcessor = (IDestinationProcessor) request.getSession().getAttribute(DESTINATION_PROCESSOR);
		IDocumentMapper documentMapper = (IDocumentMapper) request.getSession().getAttribute(DOCUMENT_MAPPER);
		if (documentMapper == null) {
			documentMapper = new DocumentMapper();
			request.getSession().setAttribute(DOCUMENT_MAPPER, documentMapper);
		}		
		documentMapper.setSourceProcessor(srcProcessor);
		documentMapper.setDestinationProcessor(destProcessor);
		importService.initialize(documentMapper);		
		return new ResponseEntity<>("{}", HttpStatus.OK);		
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/projects")
	ResponseEntity<DocumentMappingResponse> getProjects(HttpServletRequest request) {		
		IDocumentMapper documentMapper = (IDocumentMapper) request.getSession().getAttribute(DOCUMENT_MAPPER);	
		DocumentMappingResponse documentMappingResponse = new DocumentMappingResponse();
		documentMappingResponse.setDocumentMappingStatus(documentMapper.getDocumentMappingStatus());
		documentMappingResponse.setDocumentMappings(documentMapper.getDocumentMappings());
		return new ResponseEntity<>(documentMappingResponse, HttpStatus.OK);		
	}

	@RequestMapping(method = RequestMethod.POST, value = "/execute")
	void execute(@RequestBody ImportRequest importRequest, HttpServletRequest request) {
		log.info(importRequest.getImportOption());
		IDocumentMapper documentMapper = (IDocumentMapper) request.getSession().getAttribute(DOCUMENT_MAPPER);
		if (documentMapper == null) {
			documentMapper = new DocumentMapper();
			request.getSession().setAttribute(DOCUMENT_MAPPER, documentMapper);
		}		
		Long fileId = (Long)request.getSession().getAttribute(CURRENT_FILE_ID);
		importService.execute(documentMapper, fileId, importRequest);		
	}

	
	@RequestMapping(method = RequestMethod.GET, value = "/execute/status")
	ResponseEntity<ImportExecuteResponse> getExecuteStatus(HttpServletRequest request) {
		IDocumentMapper documentMapper = (IDocumentMapper) request.getSession().getAttribute(DOCUMENT_MAPPER);
		ImportExecuteResponse importExecuteResponse =  new ImportExecuteResponse();
		importExecuteResponse.setResults(documentMapper.getResults());
		importExecuteResponse.setImportStatus(documentMapper.getImportStatus());	
		return new ResponseEntity<>(importExecuteResponse, HttpStatus.OK);
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

	
	@RequestMapping(method = RequestMethod.GET, value = "/fetch/{reportingOrgId}")
	ResponseEntity<Set<String>> fetch(HttpServletRequest request, @PathVariable("reportingOrgId") String
            reportingOrgId) {
        try {
            List<Param> params = DataFetchServiceConstants.getCommonParams(reportingOrgId);
            FetchResult activitiesFromDataStore = dataFetchService.fetchResult(reportingOrgId, params);
            request.getSession().setAttribute(IATI_STORE_ACTIVITIES,activitiesFromDataStore);
            return new ResponseEntity<>(activitiesFromDataStore.getVersions(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
