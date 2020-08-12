package org.devgateway.importtool.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.endpoint.DataFetchServiceConstants;
import org.devgateway.importtool.endpoint.Param;
import org.devgateway.importtool.model.FetchResult;
import org.devgateway.importtool.services.processor.helper.ReportingOrganizationHelper;
import org.devgateway.importtool.services.processor.helper.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

@org.springframework.stereotype.Service
public class ActivityFetchService {

	@Autowired
	private DataSourceService dataSourceService;
    @Value("${IATIProcessor.default_country}")
    private String defaultCountry;

    @Autowired
    private ProjectTranslator projectTranslator;

	private Log log = LogFactory.getLog(getClass());
	private Integer FILE_EXPIRATION_TIME = 24;

	@Async
	public void fetchResult(String reportingOrg, List<Param>parameters, FetchResult result) throws FileNotFoundException,
			IOException{
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
						LocalDateTime.ofInstant(new Date(f.lastModified()).toInstant(),ZoneId.systemDefault())
								.plusHours(FILE_EXPIRATION_TIME);
				if (now.isAfter(modifiedDate)) {
					shouldUseFile = Boolean.FALSE;
				}
			}else {
				shouldUseFile = Boolean.FALSE;
			}

			if (shouldUseFile) {
				InputStream fileInputStream = new FileInputStream(f);
				doc = XMLUtils.createXMLDocument(fileInputStream);
			} else {
				doc = XMLUtils.createXMLDocument(fetch(reportingOrg, parameters));
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
			throw ex;
		}
	}

	/**
     * If we have a custom datasource for the reporting org, fetch it, if not return the default one
     * @param reportingOrg
     * @return
     */
	private String getUrlForReportingOrg(String reportingOrg) {

		String customUrl = dataSourceService.getDataSourceURL(reportingOrg);
        customUrl = customUrl != null ? customUrl : DataFetchServiceConstants.IATI_DATASTORE_DEFAULT_URL;
		return customUrl;
	}

	public Document fetchFetchFromDataStore(String reportingOrg, List<Param> parameters ) {
		return XMLUtils.createXMLDocument(fetchFetchFromDataStoreAsString(reportingOrg, parameters));
	}

	public String fetchFetchFromDataStoreAsString(String reportingOrg, List<Param> parameters ) {

		RestTemplate restTemplate = new RestTemplate();

		if (parameters == null) {
			parameters = new ArrayList<>();
		}
		setDefaultParametersForReportingOrg(parameters);
		String url = getUrlForReportingOrg(reportingOrg) + getParameters(parameters);
		log.info(url);

		return restTemplate.getForObject(url, String.class);
	}

	private void setDefaultParametersForReportingOrg(List<Param> parameters) {
		parameters.add(new Param("stream","true"));
	}

	private String getParameters(List<Param> parameters) {
		StringBuffer params = new StringBuffer();
		if (parameters != null && parameters.size() > 0) {
			params.append("?");
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
	 * @param reportingOrg
	 * @return
	 */
	private String fetch(String reportingOrg, List<Param> params) {
		String fileName = ReportingOrganizationHelper.getFileName(reportingOrg);
		String responseText = fetchFetchFromDataStoreAsString(reportingOrg, params);

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
			String iatiVersionXPatch = "/result/iati-activities/iati-activity[not(@*[name()" +
					"='iati-extra:version']=preceding::iati-activity/@*[name()='iati-extra:version'])]/@*[name()='iati-extra:version']";
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
}
