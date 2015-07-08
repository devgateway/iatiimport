package org.devgateway.importtool.rest;

import static org.devgateway.importtool.services.processor.helper.Constants.DESTINATION_PROCESSOR;
import static org.devgateway.importtool.services.processor.helper.Constants.SOURCE_PROCESSOR;
import static org.devgateway.importtool.services.processor.helper.Constants.AUTH_TOKEN;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.devgateway.importtool.services.File;
import org.devgateway.importtool.services.FileRepository;
import org.devgateway.importtool.services.processor.AMPProcessor;
import org.devgateway.importtool.services.processor.IATI105StaticProcessor;
import org.devgateway.importtool.services.processor.IATI201StaticProcessor;
import org.devgateway.importtool.services.processor.XMLGenericProcessor;
import org.devgateway.importtool.services.processor.helper.DocumentMapper;
import org.devgateway.importtool.services.processor.helper.DocumentMapping;
import org.devgateway.importtool.services.processor.helper.IDestinationProcessor;
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

	private final FileRepository repository;

	@Autowired
	ImportController(FileRepository repository) {
		this.repository = repository;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/new/{sourceProcessorName}/{destinationProcessorName}/{authenticationToken}")
	ResponseEntity<AuthenticationToken> initiateImport(
			@PathVariable String sourceProcessorName,
			@PathVariable String destinationProcessorName,
			@PathVariable String authenticationToken, HttpServletRequest request) {
		ISourceProcessor srcProcessor;
		if(request.getSession().getAttribute(SOURCE_PROCESSOR) == null) {
			srcProcessor = getSourceProcessor(sourceProcessorName);
			request.getSession().setAttribute(SOURCE_PROCESSOR, srcProcessor);
		}
		else {
			srcProcessor = (ISourceProcessor)request.getSession().getAttribute(SOURCE_PROCESSOR);
		}

		IDestinationProcessor destProcessor = getDestinationProcessor(destinationProcessorName);
		request.getSession().setAttribute(DESTINATION_PROCESSOR, destProcessor);

		AuthenticationToken authObject = new AuthenticationToken(
				authenticationToken, new Date(),
				srcProcessor.getDescriptiveName(),
				destProcessor.getDescriptiveName());
		request.getSession().setAttribute(AUTH_TOKEN, authObject);

		return new ResponseEntity<>(authObject, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/uploaded")
	public ResponseEntity<List<File>> listFiles(HttpServletRequest request) {
		List<File> fileList = new ArrayList<File>();
		AuthenticationToken authToken = (AuthenticationToken) request
				.getSession().getAttribute(AUTH_TOKEN);
		if(repository == null || authToken == null) return new ResponseEntity<>(fileList, HttpStatus.OK);
		Iterable<File> list = repository.findByAuthor(authToken.getAuthenticationToken());
		list.forEach(n -> {
			fileList.add(n);
		});
		return new ResponseEntity<>(fileList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/wipeall", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<String> wipe(HttpServletRequest request) {
		request.getSession().removeAttribute(SOURCE_PROCESSOR);
		request.getSession().removeAttribute(DESTINATION_PROCESSOR);
		request.getSession().removeAttribute(AUTH_TOKEN);
		try {
			Iterable<File> list = repository.findAll();
			repository.delete(list);
		}
		catch(Exception e) {
//			e.printStackTrace();
			return new ResponseEntity<>("{ 'error': ' " + e.getMessage() + "'}", HttpStatus.SERVICE_UNAVAILABLE);
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
		ISourceProcessor srcProcessor = (ISourceProcessor)request.getSession().getAttribute(SOURCE_PROCESSOR);
		IDestinationProcessor destProcessor = (IDestinationProcessor)request.getSession().getAttribute(DESTINATION_PROCESSOR);
		
		DocumentMapper documentMapper = new DocumentMapper();
		// Assign Source and Destination Processor to the document mapper
		documentMapper.setSourceProcessor(srcProcessor);
		documentMapper.setDestinationProcessor(destProcessor);
		try {
			documentMapper.initialize();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<>(documentMapper.getDocumentMappings(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/projects")
	ResponseEntity<AuthenticationToken> selectProjects(
			@PathVariable String authenticationToken) {
		AuthenticationToken authObject = new AuthenticationToken(
				authenticationToken, new Date(), "", null);
		return new ResponseEntity<>(authObject, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/fields/mapping")
	ResponseEntity<AuthenticationToken> mapFields(
			@PathVariable String authenticationToken) {
		AuthenticationToken authObject = new AuthenticationToken(
				authenticationToken, new Date(), "", null);
		return new ResponseEntity<>(authObject, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/validate")
	ResponseEntity<AuthenticationToken> validate(
			@PathVariable String authenticationToken) {
		AuthenticationToken authObject = new AuthenticationToken(
				authenticationToken, new Date(), "", null);
		return new ResponseEntity<>(authObject, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/execute")
	ResponseEntity<AuthenticationToken> execute(
			@PathVariable String authenticationToken) {
		AuthenticationToken authObject = new AuthenticationToken(
				authenticationToken, new Date(), "", null);
		return new ResponseEntity<>(authObject, HttpStatus.OK);
	}

	private IDestinationProcessor getDestinationProcessor(String processorName) {
		IDestinationProcessor processor;
		switch (processorName) {
		case "AMP":
			processor = new AMPProcessor();
			break;
		default:
			processor = new AMPProcessor();
			break;
		}
		return processor;
	}

	private ISourceProcessor getSourceProcessor(String processorName) {
		ISourceProcessor processor;
		switch (processorName) {
		case "IATI201":
			processor = new IATI201StaticProcessor();
			break;
		case "IATI105":
			//TODO: Update to 105 PRocessor when done
			processor = new IATI105StaticProcessor();
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
