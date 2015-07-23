package org.devgateway.importtool.rest;

import static org.devgateway.importtool.services.processor.helper.Constants.DESTINATION_PROCESSOR;
import static org.devgateway.importtool.services.processor.helper.Constants.SOURCE_PROCESSOR;
import static org.devgateway.importtool.services.processor.helper.Constants.AUTH_TOKEN;
import static org.devgateway.importtool.services.processor.helper.Constants.DOCUMENT_MAPPER;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.services.File;
import org.devgateway.importtool.services.FileRepository;
import org.devgateway.importtool.services.processor.AMPStaticProcessor;
import org.devgateway.importtool.services.processor.IATI104Processor;
import org.devgateway.importtool.services.processor.IATI105Processor;
import org.devgateway.importtool.services.processor.IATI201Processor;
import org.devgateway.importtool.services.processor.XMLGenericProcessor;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/importer/import")
class ImportController {

	@Autowired
	private FileRepository repository;
	
	private Log log = LogFactory.getLog(getClass());

	@RequestMapping(method = RequestMethod.GET, value = "/new/{sourceProcessorName}/{destinationProcessorName}/{authenticationToken}")
	ResponseEntity<AuthenticationToken> initiateImport(
			@PathVariable String sourceProcessorName,
			@PathVariable String destinationProcessorName,
			@PathVariable String authenticationToken, HttpServletRequest request) {
		ISourceProcessor srcProcessor;
		log.debug("Initializing srcProcessor.");
		if (request.getSession().getAttribute(SOURCE_PROCESSOR) == null) {
			log.debug("Creating new processor and putting it in session.");
			srcProcessor = getSourceProcessor(sourceProcessorName);
			request.getSession().setAttribute(SOURCE_PROCESSOR, srcProcessor);
		} else {
			log.debug("Already in session.");
			srcProcessor = (ISourceProcessor) request.getSession()
					.getAttribute(SOURCE_PROCESSOR);
		}
		log.debug(srcProcessor);

		log.debug("Initializing destProcessor:");
		IDestinationProcessor destProcessor = getDestinationProcessor(destinationProcessorName, authenticationToken);
		log.debug(destProcessor);
		
		request.getSession().setAttribute(DESTINATION_PROCESSOR, destProcessor);

		AuthenticationToken authObject = new AuthenticationToken(
				authenticationToken, new Date(),
				srcProcessor.getDescriptiveName(),
				destProcessor.getDescriptiveName());
		request.getSession().setAttribute(AUTH_TOKEN, authObject);

		return new ResponseEntity<>(authObject, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/new/{sourceProcessorName}/{destinationProcessorName}")
	ResponseEntity<String> initiateImport() {

		return new ResponseEntity<>("{}", HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/uploaded")
	public ResponseEntity<List<File>> listFiles(HttpServletRequest request) {
		List<File> fileList = new ArrayList<File>();
		AuthenticationToken authToken = (AuthenticationToken) request
				.getSession().getAttribute(AUTH_TOKEN);
		if (repository == null || authToken == null)
			return new ResponseEntity<>(fileList, HttpStatus.OK);
		Iterable<File> list = repository.findByAuthor(authToken
				.getAuthenticationToken());
		list.forEach(n -> {
			fileList.add(n);
		});
		return new ResponseEntity<>(fileList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/wipeall", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> wipe(HttpServletRequest request) {
		request.getSession().removeAttribute(SOURCE_PROCESSOR);
		request.getSession().removeAttribute(DESTINATION_PROCESSOR);
		request.getSession().removeAttribute(AUTH_TOKEN);
		request.getSession().removeAttribute(DOCUMENT_MAPPER);
		try {
			repository.deleteAll();
			// Iterable<File> list = repository.findAll();
			// repository.delete(list);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(
					"{ 'error': ' " + e.getMessage() + "'}",
					HttpStatus.SERVICE_UNAVAILABLE);
		}
		return new ResponseEntity<>("{'error': ''}", HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/upload")
	public ResponseEntity<String> handleFileUpload(
			@RequestParam("file_data") MultipartFile file,
			HttpServletRequest request) {
		if (!file.isEmpty()) {
			try {
				InputStream is = new ByteArrayInputStream(file.getBytes());
				ISourceProcessor srcProcessor = (ISourceProcessor) request
						.getSession().getAttribute(SOURCE_PROCESSOR);
				AuthenticationToken authToken = (AuthenticationToken) request
						.getSession().getAttribute(AUTH_TOKEN);
				srcProcessor.setInput(is);
				File uploadedFile = new File();
				uploadedFile.setData(file.getBytes());
				uploadedFile.setCreatedDate(new Date());
				uploadedFile.setFileName(file.getOriginalFilename());
				uploadedFile.setAuthor(authToken.getAuthenticationToken());

				repository.save(uploadedFile);

				return new ResponseEntity<>("{}", HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<>(
						"{\"error\": \"Error uploading file. Check if the initial steps are done.\"}",
						HttpStatus.OK);
			}
		} else {
			return new ResponseEntity<>(
					"{\"error\": \"Error uploading file.\"}", HttpStatus.OK);
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/filter")
	ResponseEntity<AuthenticationToken> filter(
			@PathVariable String authenticationToken) {
		AuthenticationToken authObject = new AuthenticationToken(
				authenticationToken, new Date(), "", null);
		return new ResponseEntity<>(authObject, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/projects")
	ResponseEntity<List<DocumentMapping>> processedProjects(
			HttpServletRequest request) {
		ISourceProcessor srcProcessor = (ISourceProcessor) request.getSession()
				.getAttribute(SOURCE_PROCESSOR);
		IDestinationProcessor destProcessor = (IDestinationProcessor) request
				.getSession().getAttribute(DESTINATION_PROCESSOR);
		IDocumentMapper documentMapper = (IDocumentMapper) request.getSession()
				.getAttribute(DOCUMENT_MAPPER);
		if (documentMapper == null) {
			documentMapper = new DocumentMapper();
			request.getSession().setAttribute(DOCUMENT_MAPPER, documentMapper);
		}

		documentMapper.setSourceProcessor(srcProcessor);
		documentMapper.setDestinationProcessor(destProcessor);
		if (!documentMapper.isInitialized()) {
			try {
				documentMapper.initialize();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return new ResponseEntity<>(documentMapper.getDocumentMappings(),
				HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/execute")
	ResponseEntity<List<ActionResult>> execute(HttpServletRequest request) {
		IDocumentMapper documentMapper = (IDocumentMapper) request.getSession()
				.getAttribute(DOCUMENT_MAPPER);
		if (documentMapper == null) {
			documentMapper = new DocumentMapper();
			request.getSession().setAttribute(DOCUMENT_MAPPER, documentMapper);
		}
		List<ActionResult> results = documentMapper.execute();
		
//		List<ActionResult> results = new ArrayList<ActionResult>();
//		results.add(new ActionResult("1", "INSERT", "ok", "project 1"));
//		results.add(new ActionResult("2", "INSERT", "ok", "project 2"));
//		results.add(new ActionResult("3", "INSERT", "ok", "project 3"));
//		results.add(new ActionResult("4", "INSERT", "ok", "project 4"));
//		
		return new ResponseEntity<>(results, HttpStatus.OK);
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
