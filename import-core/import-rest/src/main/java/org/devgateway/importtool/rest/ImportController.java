package org.devgateway.importtool.rest;

import static org.devgateway.importtool.services.processor.destination.AmpStaticProcessorConstants.SESSION_COOKIE_NAME;
import static org.devgateway.importtool.services.processor.helper.Constants.CURRENT_FILE_ID;
import static org.devgateway.importtool.services.processor.helper.Constants.DESTINATION_PROCESSOR;
import static org.devgateway.importtool.services.processor.helper.Constants.DOCUMENT_MAPPER;
import static org.devgateway.importtool.services.processor.helper.Constants.IATI_STORE_ACTIVITIES;
import static org.devgateway.importtool.services.processor.helper.Constants.REPORTING_ORG;
import static org.devgateway.importtool.services.processor.helper.Constants.SESSION_TOKEN;
import static org.devgateway.importtool.services.processor.helper.Constants.SOURCE_PROCESSOR;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.dao.FileRepository;
import org.devgateway.importtool.dao.ProjectRepository;
import org.devgateway.importtool.dao.ReportingOrgRepository;
import org.devgateway.importtool.endpoint.DataFetchServiceConstants;
import org.devgateway.importtool.endpoint.EPMessages;
import org.devgateway.importtool.endpoint.Param;
import org.devgateway.importtool.model.FetchResult;
import org.devgateway.importtool.model.File;
import org.devgateway.importtool.model.ImportSummary;
import org.devgateway.importtool.model.ReportingOrganization;
import org.devgateway.importtool.rest.dto.FetchOrganizationDetails;
import org.devgateway.importtool.security.ImportSessionToken;
import org.devgateway.importtool.services.ActivityFetchService;
import org.devgateway.importtool.services.ImportService;
import org.devgateway.importtool.services.ProjectTranslator;
import org.devgateway.importtool.services.processor.IATIProcessor;
import org.devgateway.importtool.services.processor.helper.DocumentMapper;
import org.devgateway.importtool.services.processor.helper.IDestinationProcessor;
import org.devgateway.importtool.services.processor.helper.IDocumentMapper;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.devgateway.importtool.services.processor.helper.Status;
import org.devgateway.importtool.services.request.ImportRequest;
import org.devgateway.importtool.services.response.DocumentMappingResponse;
import org.devgateway.importtool.services.response.ImportExecuteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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


@RestController
@RequestMapping(value = "/import")
class ImportController  {

	@Autowired
	private FileRepository repository;
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	ReportingOrgRepository reportingOrgRepository;

	@Autowired
	private ImportService importService;

	@Autowired
	private ProjectTranslator projectTranslator;

	@Autowired
	private ActivityFetchService activityFetchService;
	
	private Log log = LogFactory.getLog(getClass());

	@Value("${IATIProcessor.default_country}")
	private String defaultCountry;

	@RequestMapping(method = RequestMethod.GET, value = "/new/{sourceProcessorName}/{destinationProcessorName}/{userName}")
	ResponseEntity<ImportSessionToken> initiateImport(@PathVariable String sourceProcessorName,
													  @PathVariable String destinationProcessorName,
													  @PathVariable String userName,
													  HttpServletRequest request) {
		log.debug("Initialized import");
		String ampJSessionId = getAmpJSessionIdFromRequest(request);
		request.getSession().removeAttribute(SOURCE_PROCESSOR);
		request.getSession().removeAttribute(DESTINATION_PROCESSOR);
		request.getSession().removeAttribute(SESSION_TOKEN);
		request.getSession().removeAttribute(DOCUMENT_MAPPER);
		ISourceProcessor srcProcessor = importService.getSourceProcessor(sourceProcessorName);
		IDestinationProcessor destProcessor = importService.getDestinationProcessor(destinationProcessorName, ampJSessionId);
		request.getSession().setAttribute(DESTINATION_PROCESSOR, destProcessor);
		ImportSessionToken importSessionToken = new ImportSessionToken(ampJSessionId, userName, new Date(),
				srcProcessor.getDescriptiveName(), destProcessor.getDescriptiveName());
		request.getSession().setAttribute(SESSION_TOKEN, importSessionToken);
		if(request.getSession().getAttribute(IATI_STORE_ACTIVITIES) != null) {
            FetchResult fr = (FetchResult)
                    request.getSession().getAttribute(IATI_STORE_ACTIVITIES);
            srcProcessor.setFromDataStore(true);
            srcProcessor.setInput(fr.getActivities());
            logAutomatedImport(request, srcProcessor);
        }
		request.getSession().setAttribute(SOURCE_PROCESSOR, srcProcessor);
			return new ResponseEntity<>(importSessionToken, HttpStatus.OK);
	}
    
    private String getAmpJSessionIdFromRequest(HttpServletRequest request) {
        Cookie jSessionCookie = Arrays.asList(request.getCookies()).stream()
				.filter(c -> c.getName().equals(SESSION_COOKIE_NAME))
				.findAny().orElse(null);
        
        return jSessionCookie != null ? jSessionCookie.getValue() : null;
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
		IDestinationProcessor destinationProcessor =
				(IDestinationProcessor)request.getSession().getAttribute(DESTINATION_PROCESSOR);
		//since the destinationProcessor is a bean managed by Spring LifeCycle we need to rest its values to default
		if(destinationProcessor!=null) {
			destinationProcessor.reset();
		}
		request.getSession().removeAttribute(SOURCE_PROCESSOR);
		request.getSession().removeAttribute(DESTINATION_PROCESSOR);
		request.getSession().removeAttribute(SESSION_TOKEN);
		request.getSession().removeAttribute(DOCUMENT_MAPPER);
		request.getSession().removeAttribute(IATI_STORE_ACTIVITIES);
		request.getSession().removeAttribute(REPORTING_ORG);
		request.getSession().removeAttribute(CURRENT_FILE_ID);
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
				log.error(e.getMessage(), e);
				return new ResponseEntity<>(EPMessages.ERROR_UPLOADING_FILE_CHECK_INITIAL_STEPS.toString(),
						HttpStatus.OK);
			}
		} else {				
			return new ResponseEntity<>(EPMessages.ERROR_UPLOADING_FILE.toString(), HttpStatus.OK);
		}
	}	
	
	
	private void processFile(String fileName, HttpServletRequest request, ISourceProcessor srcProcessor) {
		ImportSessionToken importSessionToken = (ImportSessionToken) request.getSession().getAttribute(SESSION_TOKEN);
		File uploadedFile = importService.logImportSession(fileName ,srcProcessor, importSessionToken);
		request.getSession().setAttribute(CURRENT_FILE_ID, uploadedFile.getId());
	}

	private void logAutomatedImport (HttpServletRequest request, ISourceProcessor srcProcessor) {
		String reportingOrgId = (String)request.getSession().getAttribute(REPORTING_ORG);
		List<ReportingOrganization> orgs = reportingOrgRepository.findByOrgIdIgnoreCase(reportingOrgId);
		String orgName = orgs.size() > 0 ? orgs.get(0).getName() : "";
		
		ImportSessionToken importSessionToken = (ImportSessionToken) request.getSession().getAttribute(SESSION_TOKEN);
		File uploadedFile;
		
		uploadedFile = importService.logImportSession(orgName + " - " + srcProcessor.getDescriptiveName(),
				srcProcessor, importSessionToken);
		request.getSession().setAttribute(CURRENT_FILE_ID, uploadedFile.getId());
    }

	
	@RequestMapping(method = RequestMethod.POST, value = "/fetch")
	ResponseEntity<ImportSessionToken> filter(HttpServletRequest request) {
		String ampJSessionId = getAmpJSessionIdFromRequest(request);
		ImportSessionToken authObject = new ImportSessionToken(ampJSessionId, "", new Date(), "", null);
		// TODO: Execute the filters
		return new ResponseEntity<>(authObject, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/initialize")
	 ResponseEntity<String> processedProjects(HttpServletRequest request) {
		ISourceProcessor srcProcessor = (ISourceProcessor) request.getSession().getAttribute(SOURCE_PROCESSOR);
		IDestinationProcessor destProcessor = (IDestinationProcessor) request.getSession().getAttribute(DESTINATION_PROCESSOR);
		IDocumentMapper documentMapper = getSessionDocumentMapper(request);
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
		IDocumentMapper documentMapper = getSessionDocumentMapper(request);
		Long fileId = (Long)request.getSession().getAttribute(CURRENT_FILE_ID);
		importService.execute(documentMapper, fileId, importRequest);		
	}

	private IDocumentMapper getSessionDocumentMapper(HttpServletRequest request) {
		IDocumentMapper documentMapper = (IDocumentMapper) request.getSession().getAttribute(DOCUMENT_MAPPER);
		if (documentMapper == null) {
			documentMapper = new DocumentMapper();
			documentMapper.setProjectTranslator(projectTranslator);
			request.getSession().setAttribute(DOCUMENT_MAPPER, documentMapper);
		}
		return documentMapper;
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
		repository.deleteById(id);
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


	@RequestMapping(method = RequestMethod.GET, value = "/fetch/results")
	ResponseEntity<FetchOrganizationDetails> fetchResult(HttpServletRequest request) {
		FetchOrganizationDetails organizationDetails = new FetchOrganizationDetails();

		FetchResult activitiesFromDataStore = (FetchResult) request.getSession().getAttribute(IATI_STORE_ACTIVITIES);
		if (activitiesFromDataStore != null) {
			if (activitiesFromDataStore.getStatus().equals(Status.FAILED_WITH_ERROR)
					|| activitiesFromDataStore.getStatus().equals(Status.COMPLETED)) { //activities  have
				if (activitiesFromDataStore.getStatus().equals(Status.COMPLETED)) {
					if (activitiesFromDataStore.getStatus().equals(Status.COMPLETED)) {
						activitiesFromDataStore.getVersions().retainAll(IATIProcessor.IMPLEMENTED_VERSIONS);
						organizationDetails.setVersions(activitiesFromDataStore.getVersions());
						organizationDetails.setProjectWithUpdates(projectRepository.findProjectUpdated());
						request.getSession().setAttribute(IATI_STORE_ACTIVITIES, activitiesFromDataStore);
						organizationDetails.setStatus(Status.COMPLETED);
					} else {
						organizationDetails.setStatus(Status.FAILED_WITH_ERROR);
					}
				} else {
					organizationDetails.setStatus(Status.IN_PROGRESS);
				}
			}
		}else{
			 organizationDetails.setStatus(Status.FAILED_WITH_ERROR);
		}
		return new ResponseEntity<>(organizationDetails, HttpStatus.OK);

	}

	@RequestMapping(method = RequestMethod.GET, value = "/fetch/initialize/{reportingOrgId}")
	ResponseEntity<FetchOrganizationDetails> fetch(HttpServletRequest request, @PathVariable("reportingOrgId") String
            reportingOrgId) {
		FetchOrganizationDetails organizationDetails = new FetchOrganizationDetails();

		try {
			FetchResult activitiesFromDataStore = new FetchResult();
			activitiesFromDataStore.setStatus(Status.NOT_STARTED);
        	List<Param> params = DataFetchServiceConstants.getCommonParams(reportingOrgId, defaultCountry);
             activityFetchService.fetchResult(reportingOrgId, params, activitiesFromDataStore);
			request.getSession().setAttribute(IATI_STORE_ACTIVITIES,activitiesFromDataStore);
			request.getSession().setAttribute(REPORTING_ORG, reportingOrgId);
            return new ResponseEntity<>(organizationDetails, HttpStatus.OK);

        } catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/translation-files")
	ResponseEntity<Map<String,List<String>>> generateTranslationFiles(HttpServletRequest request) {
	    Map<String,List<String>> missingVersions = new HashMap<String,List<String>>();

        IATIProcessor.IMPLEMENTED_VERSIONS.stream().forEach(version->{
            String versionName = "IATI" + version.replace(".", "");
            try {
                ISourceProcessor srcProcessor = importService.getSourceProcessor(versionName);
                missingVersions.put(versionName, srcProcessor.buildTooltipsFields());
            }catch (Exception ex){
                log.error("cannot process for version " + versionName,ex);
            }
        });
		return new ResponseEntity<>(missingVersions, HttpStatus.OK);
	}

}
