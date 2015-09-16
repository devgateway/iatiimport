package org.devgateway.importtool.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.devgateway.importtool.services.File;
import org.devgateway.importtool.services.Workflow;
import org.devgateway.importtool.services.WorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/workflow")
public class WorkflowController {

	@Autowired
	private WorkflowRepository workflowRepository;
	
	@RequestMapping(method = RequestMethod.GET, value = "/list")
	public ResponseEntity<List<Workflow>> list(HttpServletRequest request) {
		List<Workflow> processes  =  new ArrayList<Workflow>();		
		Iterable<Workflow> list =  workflowRepository.findAll();
		list.forEach(process -> {
			processes.add(process);
		});	
		return new ResponseEntity<>(processes, HttpStatus.OK);		
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/populate")
	public ResponseEntity<String> populate(HttpServletRequest request) {		
		Workflow process1 = new Workflow();
		process1.setSourceProcessor("IATI104");
		process1.setDestinationProcessor("AMP");		
		process1.setLabel("IATI 1.04 to AMP 2.11");
		process1.setDescription("Import process for IATI 1.04 to AMP 2.11");
		workflowRepository.save(process1);
		
		Workflow process2 = new Workflow();
		process2.setSourceProcessor("IATI105");
		process2.setDestinationProcessor("AMP");
		process2.setLabel("IATI 1.05 to AMP 2.11");
		process2.setDescription("Import process for IATI 1.05 to AMP 2.11");
		workflowRepository.save(process2);
		
		Workflow process3 = new Workflow();
		process3.setSourceProcessor("IATI201");
		process3.setDestinationProcessor("AMP");
		process3.setLabel("IATI 2.01 to AMP 2.11");		
		process3.setDescription("Import process for IATI 2.01 to AMP 2.11");
		workflowRepository.save(process3);
		
		return new ResponseEntity<>("Successful!", HttpStatus.OK);
		
	}
}
