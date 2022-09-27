package org.devgateway.importtool.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.devgateway.importtool.dao.ProjectRepository;
import org.devgateway.importtool.endpoint.DataFetchServiceConstants;
import org.devgateway.importtool.endpoint.Param;
import org.devgateway.importtool.model.FetchResult;
import org.devgateway.importtool.model.Project;
import org.devgateway.importtool.services.processor.IATIProcessor;
import org.devgateway.importtool.services.processor.helper.ReportingOrganizationHelper;
import org.devgateway.importtool.services.processor.helper.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static org.devgateway.importtool.endpoint.DataFetchServiceConstants.PARAMETER_REPLACE_VALUE;
import static org.devgateway.importtool.endpoint.DataFetchServiceConstants.PARAM_AND_VALUE;
import static org.devgateway.importtool.services.processor.helper.IATIProcessorHelper.getStringFromElement;

@org.springframework.stereotype.Service
public class ActivityFetchService {
    //this is not ISO Format however iati site says it should be
    private static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final SimpleDateFormat ISO8601_DATE_FORMAT_PARSER = new SimpleDateFormat(ISO8601_DATE_FORMAT);

    @Value("${IATIProcessor.default_country}")
    private String defaultCountry;

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectTranslator projectTranslator;

    @Autowired
    private IATIDatastoreProperties iatiDatastoreProperties;

    private Log log = LogFactory.getLog(getClass());
    private Integer FILE_EXPIRATION_TIME = 24;

    @Async
    public void fetchResult(String reportingOrg, List<Param> parameters, FetchResult result) throws FileNotFoundException,
            IOException {
        fetchResultsAsync(reportingOrg, parameters, result);
    }

    private void fetchResultsAsync(String reportingOrg, List<Param> parameters, FetchResult result) throws IOException {
        String fileName = ReportingOrganizationHelper.getFileName(reportingOrg);
        try {
            File f = new File(fileName);
            Boolean shouldUseFile = Boolean.TRUE;
            Document doc;

            if (f.exists()) {
                LocalDateTime now = LocalDateTime.ofInstant((new Date()).toInstant(), ZoneId.systemDefault());
                LocalDateTime modifiedDate =
                        LocalDateTime.ofInstant(new Date(f.lastModified()).toInstant(), ZoneId.systemDefault())
                                .plusHours(FILE_EXPIRATION_TIME);
                if (now.isAfter(modifiedDate)) {
                    shouldUseFile = Boolean.FALSE;
                }
            } else {
                shouldUseFile = Boolean.FALSE;
            }

            if (shouldUseFile) {
                InputStream fileInputStream = new FileInputStream(f);
                doc = this.createXMLDocument(fileInputStream);
            } else {
                doc = this.createXMLDocument(fetch(reportingOrg, parameters));
            }
            FetchResult o = this.buildResult(doc);
            result.setActivities(o.getActivities());
            result.setVersions(o.getVersions());
            result.setStatus(Status.COMPLETED);
        } catch (RuntimeException ex) {
            //we should probably not save it until is valid. leaving it here
            //for simplicity
            Files.deleteIfExists(Paths.get(fileName));
            result.setStatus(Status.FAILED_WITH_ERROR);
            result.setMessage("Error fetching activities from the datastore: " + ex.getMessage());
            throw ex;
        }
    }

    private Document createXMLDocument(String responseText) {
        InputStream inputStream = new ByteArrayInputStream(responseText.getBytes());
        return createXMLDocument(inputStream);

    }

    private Document createXMLDocument(InputStream inputStream) {
        Document doc = null;
        if (inputStream != null) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();

                doc = builder.parse(inputStream);
            } catch (ParserConfigurationException | IOException | org.xml.sax.SAXException e) {
                //TODO properly handle errors
                log.error("Error parsing fetched data : ", e);
                throw new RuntimeException("Error parsing data");
            }
        }

        return doc;
    }

    private class IATIAuthInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                throws IOException {
            request.getHeaders().add("Ocp-Apim-Subscription-Key", iatiDatastoreProperties.getApiKey());
            return execution.execute(request, body);
        }
    }

    public Document fetchFetchFromDataStore(List<Param> parameters) {
        return XMLUtils.createXMLDocument(fetchFetchFromDataStoreAsString(parameters));
    }

    public String fetchFetchFromDataStoreAsString(List<Param> queryParameters) {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.getInterceptors().add(new IATIAuthInterceptor());
        if (queryParameters == null) {
            queryParameters = new ArrayList<>();
        }
        //SET DEFAULT PARAMS
        List<Param> defaultParameters = new ArrayList<>();
        defaultParameters.add(new Param("wt", "iati", "="));
        defaultParameters.add(new Param("rows", "5000000", "="));

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(iatiDatastoreProperties.getUrl());
        uriBuilder.pathSegment("activity", "iati");
        String url = uriBuilder.toUriString() + getParameters(queryParameters, defaultParameters);
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (RestClientException ex) {
            log.error("Cannot get activities from datastore from the following url:" + url, ex);
            throw new RuntimeException(ex.getLocalizedMessage(), ex);
        }
    }

    private String getParameters(List<Param> queryParam, List<Param> parameters) {
        StringBuffer params = new StringBuffer();
        if (queryParam != null && queryParam.size() > 0) {
            params.append("?q=");
            if (queryParam.size() > 0) {
                queryParam.stream().filter(parameter -> !parameter.getParamValueEncoded().
                        trim().isEmpty()).collect(Collectors.toList()).stream().forEach(param -> {
                    params.append(param.getParamName().replace(PARAMETER_REPLACE_VALUE, param.getParamValueEncoded()));
                    params.append(param.getParameterOperator());
                });
            }
            params.setLength(params.length() - PARAM_AND_VALUE.length());
            params.append("&");
            parameters.stream().forEach(param -> {
                params.append(param.getParamName());
                params.append(param.getParameterOperator());
                params.append(param.getParamValueEncoded());
                params.append("&");
            });
            params.setLength(params.length() - 1);
        }
        return params.toString();
    }

    public String fetch(String reportingOrg) {
        return fetch(reportingOrg, DataFetchServiceConstants.getCommonParams(reportingOrg, defaultCountry));
    }

    /**
     * Saves and return in case its being called online
     *
     * @param reportingOrg
     * @return
     */
    private String fetch(String reportingOrg, List<Param> params) {
        String fileName = ReportingOrganizationHelper.getFileName(reportingOrg);
        String responseText = fetchFetchFromDataStoreAsString(params);

        if (projectTranslator.isEnabled()) {
            try {
                projectTranslator.translate(responseText);
            } catch (Exception e) {
                log.error("Failed to translate.", e);
            }
        }

        try {
            Files.write(Paths.get(fileName), responseText.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            //if the file could not be sent, we return all the same the string so it can be processed online
            log.error("cannot save file ", e);
        }
        return responseText;
    }


    private FetchResult buildResult(Document doc) {
        FetchResult result = new FetchResult();
        if (doc != null) {
            String iatiVersionXPatch = "/iati-activities[*]/@*[name()='version']";
            XPath xPath = XPathFactory.newInstance().newXPath();
            final StringBuilder query = new StringBuilder(iatiVersionXPatch);
            try {
                NodeList iatiVersions = (NodeList) xPath.compile(iatiVersionXPatch.toString()).evaluate(doc,
                        XPathConstants.NODESET);
                for (int i = 0; i < iatiVersions.getLength(); i++) {
                    log.info((iatiVersions.item(i).getNodeValue()));
                    result.getVersions().add(iatiVersions.item(i).getNodeValue());
                }
                result.setActivities(doc);
            } catch (XPathExpressionException e) {
                //TODO properly handle errors
                log.error(e);
            }
        }
        return result;
    }

    private Integer getTotalCount(Document doc) {
        Node totalCountEl = doc.getElementsByTagName("total-count").item(0);
        Integer totalCount = 0;
        if (totalCountEl != null) {
            totalCount = Integer.parseInt(totalCountEl.getTextContent());
        }

        return totalCount;
    }

    public static void printXmlDocument(Document document) {
        DOMImplementationLS domImplementationLS =
                (DOMImplementationLS) document.getImplementation();
        LSSerializer lsSerializer =
                domImplementationLS.createLSSerializer();
        String string = lsSerializer.writeToString(document);
        System.out.println(string);
    }

    public void checkForUpdates() {
        XPath xPath = XPathFactory.newInstance().newXPath();

        StringBuilder query = new StringBuilder(IATIProcessor.DEFAULT_PATH_API);
        getIatiIdentifiers().forEach((String reportingOrg, List<String> iatiIdentifiers) -> {
            List<Param> params = DataFetchServiceConstants.getCommonParams(reportingOrg, defaultCountry);
            params.add(new Param(DataFetchServiceConstants.IATI_IDENTIFIER_PARAMETER, iatiIdentifiers,PARAM_AND_VALUE));
            Document doc = this.fetchFetchFromDataStore(params);
            NodeList activities;
            try {
                activities = (NodeList) xPath.compile(query.toString()).evaluate(doc, XPathConstants.NODESET);

                for (int i = 0; i < activities.getLength(); i++) {
                    Element element = (Element) activities.item(i);
                    String lastUpdatedDateTime = element.getAttribute(IATIProcessor.LAST_UPDATED_DATE);
                    if (lastUpdatedDateTime == null || lastUpdatedDateTime.isEmpty()) {
                        continue;
                    } else {
                        projectRepository.updateLastUpdatedDateByProjectIdentifier(ISO8601_DATE_FORMAT_PARSER.parse(
                                lastUpdatedDateTime), getStringFromElement(element, IATIProcessor.DEFAULT_ID_FIELD));
                    }
                }
            } catch (XPathExpressionException | ParseException e) {
                log.error("Cannot fetch activities due to a malformed XPATH", e);
            }
        });
    }

    private Map<String, List<String>> getIatiIdentifiers() {
        List<Project> p = projectRepository.findProjectLastSyncedDate();
        return p.stream()
                .collect(groupingBy(Project::getGroupingCriteria, mapping(Project::getProjectIdentifier,
                        Collectors.toList())));
    }
}
