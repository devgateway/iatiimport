package org.devgateway.importtool.scheduler;

import org.devgateway.importtool.services.ActivityFetchService;
import org.devgateway.importtool.services.OrganizationFetchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReportingOrganizationFetcher {
    @Autowired
    private OrganizationFetchService organizationFetchService;
    //They will be fetched once evey week we dont expect
    //new reporting orgs.
    @Scheduled(fixedRate = 500000)
    public void fetchReportingOrganizations() {
        organizationFetchService.fetch();

    }
}
