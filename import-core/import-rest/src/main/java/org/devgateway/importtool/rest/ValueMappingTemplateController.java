package org.devgateway.importtool.rest;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.dao.ValueMappingTemplateRepository;
import org.devgateway.importtool.model.FieldMappingTemplate;
import org.devgateway.importtool.model.ValueMappingTemplate;
import org.devgateway.importtool.services.processor.helper.FieldValueMapping;
import org.devgateway.importtool.services.request.ValueMappingTemplateRequest;
import org.devgateway.importtool.services.response.ValueMappingTemplateReponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/valuemappingtemplate")
class ValueMappingTemplateController {
	@Autowired
	private ValueMappingTemplateRepository valueMappingTemplateRepository;
	
	
	private Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(method = RequestMethod.POST, value = "/save")
	public ResponseEntity<String> save(@RequestBody ValueMappingTemplateRequest valueMappingTemplateRequest, HttpServletRequest request) {	
		
		ValueMappingTemplate  valueMappingTemplate = valueMappingTemplateRepository.findById(valueMappingTemplateRequest.getId());
		if(valueMappingTemplate == null) {
			valueMappingTemplate = valueMappingTemplateRepository.findByName(valueMappingTemplateRequest.getName());
			if (valueMappingTemplate != null) {
				return new ResponseEntity<>("{\"error\": \"mapping_exists\"}", HttpStatus.OK);
			} else {
				valueMappingTemplate = new ValueMappingTemplate();
			}
		}
		
		valueMappingTemplate.setName(valueMappingTemplateRequest.getName());
		ObjectMapper mapper = new ObjectMapper();  
		try{
		   String mapping = mapper.writeValueAsString(valueMappingTemplateRequest.getFieldValueMapping());
		   valueMappingTemplate.setMappingTemplate(mapping);
		   valueMappingTemplateRepository.save(valueMappingTemplate);		
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>("{\"error\": \"Error saving template.\"}",HttpStatus.OK);
		}
		
		
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/list")
	public ResponseEntity<List<ValueMappingTemplateReponse>> list(HttpServletRequest request) {		
		List<ValueMappingTemplateReponse> valueMappingTemplateList = new ArrayList<ValueMappingTemplateReponse>();
		Iterable<ValueMappingTemplate> list = valueMappingTemplateRepository.findAll();
		list.forEach(n -> {
			ValueMappingTemplateReponse valueMappingTemplateReponse = new ValueMappingTemplateReponse();
		    valueMappingTemplateReponse.setId(n.getId());
			valueMappingTemplateReponse.setName(n.getName());			
			valueMappingTemplateList.add(valueMappingTemplateReponse);
		});
		return new ResponseEntity<>(valueMappingTemplateList, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ResponseEntity<ValueMappingTemplateReponse> findById(@PathVariable Long id, HttpServletRequest request) {
		ObjectMapper mapper = new ObjectMapper();
		ValueMappingTemplate  valueMappingTemplate = valueMappingTemplateRepository.findById(id);		
		ValueMappingTemplateReponse valueMappingTemplateReponse = new ValueMappingTemplateReponse();
	    try{
			List<FieldValueMapping> valueMappings = mapper.readValue(valueMappingTemplate.getMappingTemplate(), mapper.getTypeFactory().constructCollectionType(List.class, FieldValueMapping.class));
			valueMappingTemplateReponse.setFieldValueMapping(valueMappings);
	     }catch(IOException ioex){
				log.error(ioex.getMessage());
				ioex.printStackTrace();
		}			
	    valueMappingTemplateReponse.setId(valueMappingTemplate.getId());
		valueMappingTemplateReponse.setName(valueMappingTemplate.getName());			
		
		return new ResponseEntity<>(valueMappingTemplateReponse, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id, HttpServletRequest request) {
		valueMappingTemplateRepository.delete(id);	
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}
	
	

}
