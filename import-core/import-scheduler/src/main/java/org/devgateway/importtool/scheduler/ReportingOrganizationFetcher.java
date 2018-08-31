package org.devgateway.importtool.scheduler;

import org.devgateway.importtool.services.OrganizationFetchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReportingOrganizationFetcher {
    @Autowired
    private OrganizationFetchService organizationFetchService;
    //Every friday at 5 am
    @Scheduled(cron="${ReportingOrganizationFetcher.fetchReportingOrganizations.cron}")
    public void fetchReportingOrganizations() {
        organizationFetchService.fetch();

    }
}
