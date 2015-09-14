package org.devgateway.importtool.rest;

import static org.devgateway.importtool.services.processor.helper.Constants.DESTINATION_PROCESSOR;
import static org.devgateway.importtool.services.processor.helper.Constants.DOCUMENT_MAPPER;
import static org.devgateway.importtool.services.processor.helper.Constants.SOURCE_PROCESSOR;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.devgateway.importtool.services.processor.helper.DocumentMapping;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldMapping;
import org.devgateway.importtool.services.processor.helper.FieldType;
import org.devgateway.importtool.services.processor.helper.FieldValue;
import org.devgateway.importtool.services.processor.helper.FieldValueMapping;
import org.devgateway.importtool.services.processor.helper.IDestinationProcessor;
import org.devgateway.importtool.services.processor.helper.IDocumentMapper;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.devgateway.importtool.services.processor.helper.InternalDocument;
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
		List<Language> listLanguages = new ArrayList<Language>();

		processor.getLanguages().stream().forEach(lang -> {
			Locale tmp = new Locale(lang);
			listLanguages.add(new Language(tmp.getLanguage(), tmp.getDisplayLanguage()));
		});
		return new ResponseEntity<>(listLanguages, HttpStatus.OK);
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
		documentMapper.setFieldMappingObject(fieldMapping);
		return new ResponseEntity<>(documentMapper.getFieldMappingObject(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/source/field/valuemapping")
	ResponseEntity<List<FieldValueMapping>> getValueMapping(HttpServletRequest request) {
		IDocumentMapper documentMapper = (IDocumentMapper) request.getSession().getAttribute(DOCUMENT_MAPPER);
		if (documentMapper == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// Initialize the mapping of values based on the existing selected
		// fields
		if (documentMapper.getFieldMappingObject() != null && documentMapper.getFieldMappingObject().size() > 0 && (documentMapper.getValueMappingObject() == null || documentMapper.getValueMappingObject().size() == 0)) {
			if (documentMapper.getValueMappingObject() == null) {
				documentMapper.setValueMappingObject(new ArrayList<FieldValueMapping>());
			}
			documentMapper.getFieldMappingObject().stream().forEach(fieldMapping -> {
				FieldValueMapping fvm = new FieldValueMapping();
				fvm.setSourceField(fieldMapping.getSourceField());
				fvm.setDestinationField(fieldMapping.getDestinationField());
				if (fieldMapping.getSourceField().getType() == FieldType.LIST || fieldMapping.getSourceField().getType() == FieldType.ORGANIZATION) {
					Field source = fieldMapping.getSourceField();
					source.getPossibleValues().stream().forEach(fieldValue -> {
						fvm.getValueIndexMapping().put(fieldValue.getIndex(), null);
					});
				}

				documentMapper.getValueMappingObject().add(fvm);
			});

		}
		else if (documentMapper.getValueMappingObject().size() != documentMapper.getFieldMappingObject().size())
		{
			documentMapper.getFieldMappingObject().stream().forEach(fieldMapping -> {
				Boolean alreadyInserted = documentMapper.getValueMappingObject().stream().anyMatch(n -> { return n.getSourceField().getUniqueFieldName().equals(fieldMapping.getSourceField().getUniqueFieldName());});
				if(!alreadyInserted) {
					FieldValueMapping fvm = new FieldValueMapping();
					fvm.setSourceField(fieldMapping.getSourceField());
					fvm.setDestinationField(fieldMapping.getDestinationField());
					if (fieldMapping.getSourceField().getType() == FieldType.LIST || fieldMapping.getSourceField().getType() == FieldType.ORGANIZATION) {
						Field source = fieldMapping.getSourceField();
						source.getPossibleValues().stream().forEach(fieldValue -> {
							fvm.getValueIndexMapping().put(fieldValue.getIndex(), null);
						});
					}
					documentMapper.getValueMappingObject().add(fvm);
				}
			});
			
		}
		return new ResponseEntity<>(documentMapper.getValueMappingObject(), HttpStatus.OK);
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
		List<Field> fieldList = processor.getFields();
		Field field = fieldList.stream().filter(n -> {
			return fieldName.equals(n.getFieldName());
		}).findFirst().get();
		if (field == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<FieldValue> possibleValues = field.getPossibleValues();
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
		List<Field> fieldList = processor.getFields();
		Field field = fieldList.stream().filter(n -> {
			return fieldName.equals(n.getFieldName());
		}).findFirst().get();
		if (field == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<FieldValue> possibleValues = field.getPossibleValues();
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
