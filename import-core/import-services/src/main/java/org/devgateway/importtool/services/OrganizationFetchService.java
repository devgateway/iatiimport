package org.devgateway.importtool.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.dao.ReportingOrgRepository;
import org.devgateway.importtool.endpoint.DataFetchServiceConstants;
import org.devgateway.importtool.model.ReportingOrganization;
import org.devgateway.importtool.services.dto.IatiOrganizationsOutput;
import org.devgateway.importtool.services.dto.ResultEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;

@org.springframework.stereotype.Service
public class OrganizationFetchService {
    private Log log = LogFactory.getLog(getClass());

    @Autowired
    ReportingOrgRepository reportingOrgRepository;

    @Transactional
    public void fetchIfEmpty() {
        if (reportingOrgRepository.findAll().size() == 0) {
            this.fetch();
        }
    }
    
    @Transactional
    public void fetch() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Integer offSet = 0;
            reportingOrgRepository.deleteAll();
            IatiOrganizationsOutput response;
            //Integer PAGE_SIZE = 200;
            //do {
                //Uncomment the following line to fetchFetchFromDataStore the organizations 200 at a time
                //at the moment of testing it was faster to fetchFetchFromDataStore them all together
                String finalUrl = DataFetchServiceConstants.IATI_ORGANIZATIONS_DEFAULT_URL;  // + "&limit=" +
                // PAGE_SIZE + "&offset=" + offSet;
                response = restTemplate.getForObject(finalUrl, IatiOrganizationsOutput.class);
                //offSet += PAGE_SIZE;
                response.getResult().stream().forEach(r ->  reportingOrgRepository.save(getOrgFromJson(r)));
            //} while (response.getResult().size() == PAGE_SIZE);
        } catch (RestClientException ex) {
            log.error("Cannot get organizations from datastore", ex);
            throw new RuntimeException(ex);
        }
    }
    private ReportingOrganization getOrgFromJson(ResultEntry entry) {
        return new ReportingOrganization(entry.getPublisherIatiId(), entry.getTitle());
    }
}
