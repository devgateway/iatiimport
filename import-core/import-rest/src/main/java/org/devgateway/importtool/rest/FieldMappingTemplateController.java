package org.devgateway.importtool.rest;



import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.services.FieldMappingTemplate;
import org.devgateway.importtool.services.FieldMappingTemplateRepository;
import org.devgateway.importtool.services.processor.helper.FieldMapping;
import org.devgateway.importtool.services.request.FieldMappingTemplateRequest;
import org.devgateway.importtool.services.response.FieldMappingTemplateReponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

@RestController
@RequestMapping(value = "/importer/fieldmappingtemplate")
class FieldMappingTemplateController {
	@Autowired
	private FieldMappingTemplateRepository fieldMappingTemplateRepository;
	
	
	private Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(method = RequestMethod.POST, value = "/save")
	public ResponseEntity<String> save(@RequestBody FieldMappingTemplateRequest fieldMappingTemplateRequest, HttpServletRequest request) {	
		FieldMappingTemplate fieldMappingTemplate = new FieldMappingTemplate();
		fieldMappingTemplate.setName(fieldMappingTemplateRequest.getName());
		ObjectMapper mapper = new ObjectMapper();  
		try{
		   String mapping = mapper.writeValueAsString(fieldMappingTemplateRequest.getFieldMapping());
		   fieldMappingTemplate.setMappingTemplate(mapping);
		   fieldMappingTemplateRepository.save(fieldMappingTemplate);		
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("{\"error\": \"Error saving template.\"}",HttpStatus.OK);
		}
		
		
		return new ResponseEntity<>("{}", HttpStatus.OK);
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
		FieldMappingTemplate  fieldMappingTemplate = fieldMappingTemplateRepository.findById(id);		
		FieldMappingTemplateReponse fieldMappingTemplateReponse = new FieldMappingTemplateReponse();
	    try{
			List<FieldMapping> fieldMappings = mapper.readValue(fieldMappingTemplate.getMappingTemplate(), mapper.getTypeFactory().constructCollectionType(List.class, FieldMapping.class));
			fieldMappingTemplateReponse.setFieldMapping(fieldMappings);
	     }catch(IOException ioex){
				ioex.printStackTrace();
		}			
	    fieldMappingTemplateReponse.setId(fieldMappingTemplate.getId());
		fieldMappingTemplateReponse.setName(fieldMappingTemplate.getName());			
		
		return new ResponseEntity<>(fieldMappingTemplateReponse, HttpStatus.OK);
	}
	
	
	

}
