package org.devgateway.importtool.rest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.devgateway.importtool.services.processor.AMPProcessor;
import org.devgateway.importtool.services.processor.IATI105Processor;
import org.devgateway.importtool.services.processor.IATI201Processor;
import org.devgateway.importtool.services.processor.XMLGenericProcessor;
import org.devgateway.importtool.services.processor.helper.IDestinationProcessor;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.devgateway.importtool.services.processor.helper.Constants.*;

@RestController
@RequestMapping(value = "/import")
class ImportController {

	@RequestMapping(method = RequestMethod.GET, value = "/new/{processorName}/{authenticationToken}")
	ResponseEntity<AuthenticationToken> initiateImport(
			@PathVariable String processorName,
			@PathVariable String authenticationToken, HttpServletRequest request) {

		ISourceProcessor srcProcessor = getSourceProcessor(processorName);
		request.getSession().setAttribute(SOURCE_PROCESSOR, srcProcessor);

		IDestinationProcessor destProcessor = getDestinationProcessor("AMP");
		request.getSession().setAttribute(DESTINATION_PROCESSOR, destProcessor);

		AuthenticationToken authObject = new AuthenticationToken(
				authenticationToken, new Date(), srcProcessor.getClass().getName(), destProcessor.getClass().getName());

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
			processor = new IATI201Processor();
			InputStream is201 = processor
					.getClass()
					.getResourceAsStream(
							"IATI201/sample_files/activity-standard-example-minimal.xml");
			processor.setInput(is201);

			break;
		case "IATI105":
			processor = new IATI105Processor();
			InputStream is105 = processor
					.getClass()
					.getResourceAsStream(
							"IATI201/sample_files/activity-standard-example-minimal.xml");
			processor.setInput(is105);

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

	@RequestMapping(method = RequestMethod.POST, value = "/upload")
	public @ResponseBody String handleFileUpload(
			@RequestParam("name") String name,
			@RequestParam("file") MultipartFile file) {
		if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(new File(name)));
				stream.write(bytes);
				stream.close();
				return "You successfully uploaded " + name + "!";
			} catch (Exception e) {
				return "You failed to upload " + name + " => " + e.getMessage();
			}
		} else {
			return "You failed to upload " + name
					+ " because the file was empty.";
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/filter")
	ResponseEntity<AuthenticationToken> filter(
			@PathVariable String authenticationToken) {
		AuthenticationToken authObject = new AuthenticationToken(
				authenticationToken, new Date(), "", null);
		return new ResponseEntity<>(authObject, HttpStatus.OK);
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

}
