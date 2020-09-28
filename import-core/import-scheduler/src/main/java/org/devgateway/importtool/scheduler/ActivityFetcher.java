package org.devgateway.importtool.scheduler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.dao.ReportingOrgRepository;
import org.devgateway.importtool.services.ActivityFetchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActivityFetcher {

    private static final Log log = LogFactory.getLog(ActivityFetcher.class);

    @Value("${IATIProcessor.default_country}")
    private String defaultCountry;
    @Autowired
    private ActivityFetchService activityFetchService;

    @Autowired
    private ReportingOrgRepository reportingOrgRepository;

    @Scheduled(cron = "${ActivityFetcher.checkForUpdates.cron}")
    public void checkForUpdates() {
        activityFetchService.checkForUpdates();
    }

    @Scheduled(cron = "${ActivityFetcher.fetchActivitiesForSyncedReportingOrgs.cron}")
    public void fetchActivitiesForSyncedReportingOrgs() {
        List<String> gropingCriteriaList = reportingOrgRepository.getSyncedGroupingCriteria();
        gropingCriteriaList.stream().forEach(reportingOrg -> activityFetchService.fetch(reportingOrg));
    }

    @EventListener
    public void onApplicationEvent(ContextStartedEvent event) {
        this.fetchActivitiesForSyncedReportingOrgs();
        activityFetchService.checkForUpdates();
    }
}