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
    private static Integer PAGE_SIZE = 200;

    @Transactional
    public void fetch() {
        StopWatch elapsedTimer = new StopWatch();

        try {
            RestTemplate restTemplate = new RestTemplate();
            Integer offSet = 0;
            elapsedTimer.start();
            reportingOrgRepository.deleteAll();
            IatiOrganizationsOutput response;
            do {
                //Uncomment the following line to fetch the organizations 200 at a time
                //at the moment of testing it was faster to fetch them all together
                String finalUrl = DataFetchServiceConstants.IATI_ORGANIZATIONS_DEFAULT_URL;  // + "&limit=" +
                // PAGE_SIZE + "&offset=" + offSet;
                response = restTemplate.getForObject(finalUrl, IatiOrganizationsOutput.class);
                offSet += PAGE_SIZE;
                response.getResult().stream().forEach(r -> {
                    reportingOrgRepository.save(getOrgFromJson(r));
                });
            } while (response.getResult().size() == PAGE_SIZE);
            elapsedTimer.stop();
            log.error("Elapsed time " + elapsedTimer.prettyPrint());
        } catch (RestClientException ex) {
            log.error("Cannot get organization ", ex);
            throw new RuntimeException(ex);
        }
    }

    private ReportingOrganization getOrgFromJson(ResultEntry entry) {
        return new ReportingOrganization(entry.getPublisherIatiId(), entry.getName());
    }
}
