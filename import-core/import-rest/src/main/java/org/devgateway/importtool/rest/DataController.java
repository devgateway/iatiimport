package org.devgateway.importtool.rest;

import static org.devgateway.importtool.services.processor.helper.Constants.DESTINATION_PROCESSOR;
import static org.devgateway.importtool.services.processor.helper.Constants.DOCUMENT_MAPPER;
import static org.devgateway.importtool.services.processor.helper.Constants.SOURCE_PROCESSOR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.model.Language;
import org.devgateway.importtool.services.DataService;
import org.devgateway.importtool.services.processor.helper.DocumentMapping;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldMapping;
import org.devgateway.importtool.services.processor.helper.FieldValue;
import org.devgateway.importtool.services.processor.helper.FieldValueMapping;
import org.devgateway.importtool.services.processor.helper.IDestinationProcessor;
import org.devgateway.importtool.services.processor.helper.IDocumentMapper;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.devgateway.importtool.services.processor.helper.InternalDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/data")
class DataController {
	
	@Autowired
	private DataService dataService;
	
	private Log log = LogFactory.getLog(getClass());

	@RequestMapping(method = RequestMethod.GET, value = "/source/filters")
	ResponseEntity<List<Field>> getFilterFields(HttpServletRequest request) {
		ISourceProcessor processor = (ISourceProcessor) request.getSession().getAttribute(SOURCE_PROCESSOR);
		if (processor == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(processor.getFilterFields(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/source/filters")
	ResponseEntity<List<Field>> setFilterFields(@RequestBody List<Field> fields, HttpServletRequest request) {
		ISourceProcessor processor = (ISourceProcessor) request.getSession().getAttribute(SOURCE_PROCESSOR);
		if (processor == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		processor.setFilterFields(fields);
		return new ResponseEntity<>(new ArrayList<Field>(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/source/languages")
	ResponseEntity<List<Language>> getLanguages(HttpServletRequest request) {
		ISourceProcessor processor = (ISourceProcessor) request.getSession().getAttribute(SOURCE_PROCESSOR);
		if (processor == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(processor.getFilterLanguages(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/source/languages")
	ResponseEntity<List<Language>> setFilterLanguages(@RequestBody List<Language> languages, HttpServletRequest request) {		
		ISourceProcessor processor = (ISourceProcessor) request.getSession().getAttribute(SOURCE_PROCESSOR);
		if (processor == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		processor.setFilterLanguages(languages);
		return new ResponseEntity<>(new ArrayList<Language>(), HttpStatus.OK);
	}
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/source/field")
	ResponseEntity<List<Field>> getSourceFields(HttpServletRequest request) {
		ISourceProcessor processor = (ISourceProcessor) request.getSession().getAttribute(SOURCE_PROCESSOR);
		if (processor == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(processor.getFields(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/source/field/mapping")
	ResponseEntity<List<FieldMapping>> getFieldMapping(HttpServletRequest request) {
		IDocumentMapper documentMapper = (IDocumentMapper) request.getSession().getAttribute(DOCUMENT_MAPPER);
		if (documentMapper == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(documentMapper.getFieldMappingObject(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/source/field/mapping")
	ResponseEntity<List<FieldMapping>> setFieldMapping(@RequestBody List<FieldMapping> fieldMapping, HttpServletRequest request) {
		IDocumentMapper documentMapper = (IDocumentMapper) request.getSession().getAttribute(DOCUMENT_MAPPER);
		if (documentMapper == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		return new ResponseEntity<>(dataService.setFieldMapping(documentMapper, fieldMapping), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/source/field/valuemapping")
	ResponseEntity<List<FieldValueMapping>> getValueMapping(HttpServletRequest request) {
		IDocumentMapper documentMapper = (IDocumentMapper) request.getSession().getAttribute(DOCUMENT_MAPPER);
		if (documentMapper == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		List<FieldValueMapping> valueMapping = dataService.getValueMapping(documentMapper);

		
		// Get the providing organization field
		ISourceProcessor processor = (ISourceProcessor) request.getSession().getAttribute(SOURCE_PROCESSOR);
		List<Field> sourceFields = processor.getFilterFields();
		Optional<Field> optProvOrg = sourceFields.stream().filter(n -> {
			return "provider-org".equals(n.getFieldName());
		}).findFirst();

		// Get the funding organization field
		Optional<FieldValueMapping> optValueMapping = valueMapping.stream().filter(n -> {
			return ("participating-org".equals(n.getSourceField().getFieldName()) && "Funding".equals(n.getSourceField().getSubType()) && "1".equals(n.getSourceField().getSubTypeCode()));
		}).findFirst();
		
		if(optValueMapping.isPresent()){
			FieldValueMapping fvm = optValueMapping.get();
			Field fundOrg = fvm.getSourceField();
			// Get Filters
			if(optProvOrg.isPresent() && optProvOrg.get().getFilters() != null && optProvOrg.get().getFilters().size() > 0) {
				Field provOrg = optProvOrg.get();
				List<String> filters = provOrg.getFilters();
				List<FieldValue> reducedList = new ArrayList<FieldValue>();

				for (int i = 0; i < filters.size(); i++) {
					final String filter = filters.get(i);
					fundOrg.getPossibleValues().forEach(n -> {
						if(n.getCode().equals(filter)) {
							reducedList.add(n);
						}					
					});
				}
				fundOrg.setPossibleValues(reducedList);
			}
		}
		
		return new ResponseEntity<>(dataService.getValueMapping(documentMapper), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/source/field/valuemapping")
	ResponseEntity<List<FieldValueMapping>> setValueMapping(@RequestBody List<FieldValueMapping> valueMapping, HttpServletRequest request) {
		IDocumentMapper documentMapper = (IDocumentMapper) request.getSession().getAttribute(DOCUMENT_MAPPER);
		if (documentMapper == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}


		documentMapper.setValueMappingObject(valueMapping);
		
		return new ResponseEntity<>(documentMapper.getValueMappingObject(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/source/field/{fieldName}")
	ResponseEntity<List<FieldValue>> getSourceFieldValues(@PathVariable String fieldName, HttpServletRequest request) {
		ISourceProcessor processor = (ISourceProcessor) request.getSession().getAttribute(SOURCE_PROCESSOR);
		List<FieldValue> possibleValues = dataService.getSourceFieldValues(processor, fieldName);
		if(possibleValues == null){
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		return new ResponseEntity<>(possibleValues, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/destination/field")
	ResponseEntity<List<Field>> getDestinationFields(HttpServletRequest request) {
		List<Field> fieldList = new ArrayList<Field>();
		IDestinationProcessor processor = (IDestinationProcessor) request.getSession().getAttribute(DESTINATION_PROCESSOR);
		fieldList = processor.getFields();
		return new ResponseEntity<>(new ArrayList<Field>(fieldList), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/destination/field/{fieldName}")
	ResponseEntity<List<FieldValue>> getDestinationFieldValues(@PathVariable String fieldName, HttpServletRequest request) {
		IDestinationProcessor processor = (IDestinationProcessor) request.getSession().getAttribute(DESTINATION_PROCESSOR);
		List<FieldValue> possibleValues = dataService.getDestinationFieldValues(processor, fieldName);	
		if(possibleValues == null){
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		return new ResponseEntity<>(possibleValues, HttpStatus.OK);		
	}

	@RequestMapping(method = RequestMethod.POST, value = "/source/project")
	ResponseEntity<List<DocumentMapping>> setDocumentMapping(@RequestBody List<DocumentMapping> mappings, HttpServletRequest request) {
		IDocumentMapper documentMapper = (IDocumentMapper) request.getSession().getAttribute(DOCUMENT_MAPPER);
		if (documentMapper == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		documentMapper.setDocumentMappings(mappings);
		return new ResponseEntity<>(mappings, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/source/project")
	ResponseEntity<List<InternalDocument>> getSourceProjects(HttpServletRequest request) {
		List<InternalDocument> docList = new ArrayList<InternalDocument>();
		ISourceProcessor processor = (ISourceProcessor) request.getSession().getAttribute(SOURCE_PROCESSOR);
		try {
			docList = processor.getDocuments();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<>(docList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/destination/project")
	ResponseEntity<List<InternalDocument>> getDestinationProjects(HttpServletRequest request) {
		List<InternalDocument> docList = new ArrayList<InternalDocument>();
		IDestinationProcessor processor = (IDestinationProcessor) request.getSession().getAttribute(DESTINATION_PROCESSOR);
		docList = processor.getDocuments(true);

		return new ResponseEntity<>(docList, HttpStatus.OK);
	}
	
	


}
