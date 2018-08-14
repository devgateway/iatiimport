package org.devgateway.importtool.scheduler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.dao.ProjectRepository;
import org.devgateway.importtool.endpoint.Param;
import org.devgateway.importtool.model.Project;
import org.devgateway.importtool.services.DataFetchService;
import org.devgateway.importtool.endpoint.DataFetchServiceConstants;
import org.devgateway.importtool.services.DataSourceService;
import org.devgateway.importtool.services.processor.IATIProcessor;
import org.devgateway.importtool.services.processor.helper.IATIProcessorHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

@Component
public class ActivityFetcher {

    private static final Log log = LogFactory.getLog(ActivityFetcher.class);
    //this is not ISO Format however iati site says it should be
    private static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final SimpleDateFormat ISO8601_DATE_FORMAT_PARSER = new SimpleDateFormat(ISO8601_DATE_FORMAT);

    @Autowired
    private DataFetchService dataFetchService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private DataSourceService dataSourceService;
    //to be scheduled with cron like syntax
    @Scheduled(fixedRate = 500000)
    public void checkForUpdates() {
        XPath xPath = XPathFactory.newInstance().newXPath();

        StringBuilder query = new StringBuilder(IATIProcessor.DEFAULT_PATH_API);
        getIatiIdentifiers().forEach((String reportingOrg, List<String> iatiIdentifiers) -> {
            List<Param> params = new ArrayList<>();
            //SN should be taken from AMP
            params.add(new Param(DataFetchServiceConstants.RECIPIENT_COUNTRY_PARAMETER, "SN"));
            params.add(new Param(DataFetchServiceConstants.REPORTING_ORGANISATION_PARAMETER, reportingOrg));
            params.add(new Param(DataFetchServiceConstants.IATI_IDENTIFIER_PARAMETER, iatiIdentifiers));

            String customUrl = dataSourceService.getDataSourceURL(reportingOrg);
            Document doc = dataFetchService.fetch((customUrl != null) ? customUrl : DataFetchServiceConstants
                    .DEFAULT_URL, params);
            NodeList activities;
            try {
                activities = (NodeList) xPath.compile(query.toString()).evaluate(doc, XPathConstants.NODESET);

                for (int i = 0; i < activities.getLength(); i++) {
                    Element element = (Element) activities.item(i);
                    String lastUpdatedDateTime = element.getAttribute(IATIProcessor.LAST_UPDATED_DATE);
                    projectRepository.updateLastUpdatedDateByProjectIdentifier(ISO8601_DATE_FORMAT_PARSER.parse(
                            lastUpdatedDateTime),IATIProcessorHelper.getStringFromElement(element,
                            IATIProcessor.DEFAULT_ID_FIELD));
                }
            } catch (Exception e) {
                log.error("Cannot fetch activities", e);
            }
        });
    }
    private  Map<String, List<String>> getIatiIdentifiers() {
        List<Project> p = projectRepository.findProjectLastSyncedDate();
        return p.stream()
                .collect(groupingBy(Project::getGroupingCriteria, mapping(Project::getProjectIdentifier,
                        Collectors.toList())));
    }
}