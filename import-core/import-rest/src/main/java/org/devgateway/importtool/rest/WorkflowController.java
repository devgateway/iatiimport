package org.devgateway.importtool.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.devgateway.importtool.model.Workflow;
import org.devgateway.importtool.services.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.devgateway.importtool.services.processor.helper.Constants.WORKFLOW_LIST;

@RestController
@RequestMapping(value = "/workflow")
public class WorkflowController {
	
	@Autowired
	WorkflowService workflowService;
	
	@RequestMapping(method = RequestMethod.GET, value = "/list")
	public ResponseEntity<List<Workflow>> list(HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		List<Workflow> workflows = (List<Workflow>)request.getSession().getAttribute(WORKFLOW_LIST);		
		if(workflows == null){
			workflows = workflowService.getWorkflows();	
			request.getSession().setAttribute(WORKFLOW_LIST, workflows);
		}		
		return new ResponseEntity<>(workflows, HttpStatus.OK);		
	}
	
	
}
