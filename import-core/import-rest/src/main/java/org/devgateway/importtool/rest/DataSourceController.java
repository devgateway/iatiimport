package org.devgateway.importtool.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.devgateway.importtool.dao.ReportingOrgRepository;
import org.devgateway.importtool.model.DataSource;
import org.devgateway.importtool.model.ReportingOrganization;
import org.devgateway.importtool.services.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/data-source")
class DataSourceController {	
	@Autowired
	private DataSourceService dataSourceService;
	@Autowired
	private ReportingOrgRepository reportingOrgRepository;

	@RequestMapping(method = RequestMethod.GET)
	ResponseEntity<DataSource> getDataSource(HttpServletRequest request) {		
		return new ResponseEntity<>(dataSourceService.getDataSource(), HttpStatus.OK);	
	}
	
	@RequestMapping(method = RequestMethod.POST)
	ResponseEntity<DataSource> saveDataSource(@RequestBody DataSource ds, HttpServletRequest request) {
		return new ResponseEntity<>(dataSourceService.save(ds), HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value={"/reporting-orgs", "/reporting-orgs/{orgName}"})
	ResponseEntity<List<ReportingOrganization>> getOrganizations(HttpServletRequest request,
																 @PathVariable Optional<String> orgName) {
		List<ReportingOrganization> reportingOrganizations;
		if (orgName.isPresent()) {
			reportingOrganizations = reportingOrgRepository.findByNameContainsIgnoreCase(orgName.get());
		} else {
			reportingOrganizations = reportingOrgRepository.findAll();
		}
		return new ResponseEntity<>(reportingOrganizations, HttpStatus.OK);
	}
	@RequestMapping(method = RequestMethod.GET, value="reporting-orgs/with-updates")
	ResponseEntity<List<ReportingOrganization>> getOrganizationsWithUpdates(HttpServletRequest request) {
		return new ResponseEntity<>(reportingOrgRepository.groupingCriteriaWithUpdates(), HttpStatus.OK);
	}
	
}
