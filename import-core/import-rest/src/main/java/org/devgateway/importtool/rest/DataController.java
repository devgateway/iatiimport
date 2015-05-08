package org.devgateway.importtool.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.IDestinationProcessor;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.devgateway.importtool.services.processor.helper.InternalDocument;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.devgateway.importtool.services.processor.helper.Constants.*;

@RestController
@RequestMapping(value = "/data")
class DataController {

	@RequestMapping(method = RequestMethod.GET, value = "/source/field")
	ResponseEntity<List<Field>> getSourceFields(HttpServletRequest request) {
		ISourceProcessor processor = (ISourceProcessor) request.getSession()
				.getAttribute(SOURCE_PROCESSOR);
		if (processor == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(processor.getFields(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/source/field/{fieldName}")
	ResponseEntity<Map<String, String>> getSourceFieldValues(
			@PathVariable String fieldName, HttpServletRequest request) {
		ISourceProcessor processor = (ISourceProcessor) request.getSession()
				.getAttribute(SOURCE_PROCESSOR);
		List<Field> fieldList = processor.getFields();
		Field field = fieldList.stream().filter(n -> {
			return fieldName.equals(n.getFieldName());
		}).findFirst().get();
		if (field == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Map<String, String> possibleValues = field.getPossibleValues();
		return new ResponseEntity<>(possibleValues, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/destination/field")
	ResponseEntity<List<Field>> getDestinationFields(HttpServletRequest request) {
		List<Field> fieldList = new ArrayList<Field>();
		IDestinationProcessor processor = (IDestinationProcessor) request
				.getSession().getAttribute(DESTINATION_PROCESSOR);
		fieldList = processor.getFields();
		return new ResponseEntity<>(fieldList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/destination/field/{fieldName}")
	ResponseEntity<Map<String, String>> getDestinationFieldValues(
			@PathVariable String fieldName, HttpServletRequest request) {
		IDestinationProcessor processor = (IDestinationProcessor) request
				.getSession().getAttribute(DESTINATION_PROCESSOR);
		List<Field> fieldList = processor.getFields();
		Field field = fieldList.stream().filter(n -> {
			return fieldName.equals(n.getFieldName());
		}).findFirst().get();
		if (field == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Map<String, String> possibleValues = field.getPossibleValues();
		return new ResponseEntity<>(possibleValues, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/source/project")
	ResponseEntity<List<InternalDocument>> getSourceProjects(
			HttpServletRequest request) {
		List<InternalDocument> docList = new ArrayList<InternalDocument>();
		ISourceProcessor processor = (ISourceProcessor) request.getSession()
				.getAttribute(SOURCE_PROCESSOR);
		docList = processor.getDocuments();

		return new ResponseEntity<>(docList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/destination/project")
	ResponseEntity<List<InternalDocument>> getDestinationProjects(
			HttpServletRequest request) {
		List<InternalDocument> docList = new ArrayList<InternalDocument>();
		IDestinationProcessor processor = (IDestinationProcessor) request
				.getSession().getAttribute(DESTINATION_PROCESSOR);
		docList = processor.getDocuments();

		return new ResponseEntity<>(docList, HttpStatus.OK);
	}

}
