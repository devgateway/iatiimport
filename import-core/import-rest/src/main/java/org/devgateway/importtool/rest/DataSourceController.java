package org.devgateway.importtool.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.devgateway.importtool.model.DataSource;
import org.devgateway.importtool.model.ReportingOrganization;
import org.devgateway.importtool.services.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/data-source")
class DataSourceController {	
	@Autowired
	private DataSourceService dataSourceService;
	
	@RequestMapping(method = RequestMethod.GET)
	ResponseEntity<DataSource> getDataSource(HttpServletRequest request) {		
		return new ResponseEntity<>(dataSourceService.getDataSource(), HttpStatus.OK);	
	}
	
	@RequestMapping(method = RequestMethod.POST)
	ResponseEntity<DataSource> saveDataSource(@RequestBody DataSource ds, HttpServletRequest request) {
		return new ResponseEntity<>(dataSourceService.save(ds), HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="reporting-orgs")
	ResponseEntity<List<ReportingOrganization>> getOrganizations(HttpServletRequest request) {	
		List<ReportingOrganization> orgs = new ArrayList<>();
		ReportingOrganization org1 = new ReportingOrganization("01", "Organization 1");
		orgs.add(org1);
		ReportingOrganization org2 = new ReportingOrganization("02", "Organization 2");
		orgs.add(org2);
		ReportingOrganization org3 = new ReportingOrganization("03", "Organization 3");
		orgs.add(org3);
		ReportingOrganization org4 = new ReportingOrganization("04", "Organization 4");
		orgs.add(org4);
		
		return new ResponseEntity<>(orgs, HttpStatus.OK);	
	}
	
}
