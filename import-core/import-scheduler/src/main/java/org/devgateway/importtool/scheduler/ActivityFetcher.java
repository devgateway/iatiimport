package org.devgateway.importtool.scheduler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ActivityFetcher {

    private static final Log log = LogFactory.getLog(ActivityFetcher.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    //@Scheduled(cron = "5 11 * * * *")
    @Scheduled(fixedRate = 5000)
    // we can use  to schedule every 5 seconds
    public void checkForUpdates() {
        log.error("timer executed at " + dateFormat.format(new Date()));
    }
}