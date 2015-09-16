package org.devgateway.importtool.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.devgateway.importtool.services.File;
import org.devgateway.importtool.services.ImportProcess;
import org.devgateway.importtool.services.ImportProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/process")
public class ImportProcessController {

	@Autowired
	private ImportProcessRepository importProcessRepository;
	
	@RequestMapping(method = RequestMethod.GET, value = "/list")
	public ResponseEntity<List<ImportProcess>> list(HttpServletRequest request) {
		List<ImportProcess> processes  =  new ArrayList<ImportProcess>();		
		Iterable<ImportProcess> list =  importProcessRepository.findAll();
		list.forEach(process -> {
			processes.add(process);
		});	
		return new ResponseEntity<>(processes, HttpStatus.OK);		
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/populate")
	public ResponseEntity<String> populate(HttpServletRequest request) {		
		ImportProcess process1 = new ImportProcess();
		process1.setSourceProcessor("IATI104");
		process1.setDestinationProcessor("AMP");		
		process1.setLabel("IATI 1.04 to AMP 2.11");
		process1.setDescription("Import process for IATI 1.04 to AMP 2.11");
		importProcessRepository.save(process1);
		
		ImportProcess process2 = new ImportProcess();
		process2.setSourceProcessor("IATI105");
		process2.setDestinationProcessor("AMP");
		process2.setLabel("IATI 1.05 to AMP 2.11");
		process2.setDescription("Import process for IATI 1.05 to AMP 2.11");
		importProcessRepository.save(process2);
		
		ImportProcess process3 = new ImportProcess();
		process3.setSourceProcessor("IATI201");
		process3.setDestinationProcessor("AMP");
		process3.setLabel("IATI 2.01 to AMP 2.11");		
		process3.setDescription("Import process for IATI 2.01 to AMP 2.11");
		importProcessRepository.save(process3);
		
		return new ResponseEntity<>("Successful!", HttpStatus.OK);
		
	}
}
