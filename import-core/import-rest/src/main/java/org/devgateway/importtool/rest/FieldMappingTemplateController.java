package org.devgateway.importtool.rest;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.dao.FieldMappingTemplateRepository;
import org.devgateway.importtool.model.FieldMappingTemplate;
import org.devgateway.importtool.services.processor.helper.FieldMapping;
import org.devgateway.importtool.services.request.FieldMappingTemplateRequest;
import org.devgateway.importtool.services.response.FieldMappingTemplateReponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/fieldmappingtemplate")
class FieldMappingTemplateController {
	
	@Autowired
	private FieldMappingTemplateRepository fieldMappingTemplateRepository;
		
	private Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(method = RequestMethod.POST, value = "/save")
	public ResponseEntity<String> save(@RequestBody FieldMappingTemplateRequest fieldMappingTemplateRequest, HttpServletRequest request) {	
		FieldMappingTemplate fieldMappingTemplate = fieldMappingTemplateRepository
				.findById(fieldMappingTemplateRequest.getId());
		if (fieldMappingTemplate == null) {
			fieldMappingTemplate = fieldMappingTemplateRepository.findByName(fieldMappingTemplateRequest.getName());
			if (fieldMappingTemplate != null) {
				return new ResponseEntity<>("{\"error\": \"mapping_exists\"}", HttpStatus.OK);
			} else {
				fieldMappingTemplate = new FieldMappingTemplate();
			}
		}
		fieldMappingTemplate.setName(fieldMappingTemplateRequest.getName());

		try {
			ObjectMapper mapper = new ObjectMapper();
			String mapping = mapper.writeValueAsString(fieldMappingTemplateRequest.getFieldMapping());
			fieldMappingTemplate.setMappingTemplate(mapping);
			fieldMappingTemplateRepository.save(fieldMappingTemplate);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>("{\"error\": \"msg_error_saving\"}", HttpStatus.OK);
		}
		
		
		return new ResponseEntity<>("{\"id\": " + fieldMappingTemplate.getId() + ", \"name\": \"" + fieldMappingTemplate.getName() + "\" }", HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/list")
	public ResponseEntity<List<FieldMappingTemplateReponse>> list(HttpServletRequest request) {		
		List<FieldMappingTemplateReponse> fieldMappingTemplateList = new ArrayList<FieldMappingTemplateReponse>();
		Iterable<FieldMappingTemplate> list = fieldMappingTemplateRepository.findAll();
		list.forEach(n -> {
			FieldMappingTemplateReponse fieldMappingTemplateReponse = new FieldMappingTemplateReponse();
		    fieldMappingTemplateReponse.setId(n.getId());
			fieldMappingTemplateReponse.setName(n.getName());			
			fieldMappingTemplateList.add(fieldMappingTemplateReponse);
		});
		return new ResponseEntity<>(fieldMappingTemplateList, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ResponseEntity<FieldMappingTemplateReponse> findById(@PathVariable Long id, HttpServletRequest request) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		FieldMappingTemplate  fieldMappingTemplate = fieldMappingTemplateRepository.findById(id);		
		FieldMappingTemplateReponse fieldMappingTemplateReponse = new FieldMappingTemplateReponse();
	    try{
	        List<FieldMapping> fieldMappings = mapper.readValue(fieldMappingTemplate.getMappingTemplate(), mapper.getTypeFactory().constructCollectionType(List.class, FieldMapping.class));
			fieldMappingTemplateReponse.setFieldMapping(fieldMappings);
	     }catch(IOException ioex){
				log.error(ioex.getMessage());
				ioex.printStackTrace();
		}			
	    fieldMappingTemplateReponse.setId(fieldMappingTemplate.getId());
		fieldMappingTemplateReponse.setName(fieldMappingTemplate.getName());			
		
		return new ResponseEntity<>(fieldMappingTemplateReponse, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id, HttpServletRequest request) {
		fieldMappingTemplateRepository.delete(id);	
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}
	
	

}
