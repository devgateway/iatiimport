package org.devgateway.importtool.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.endpoint.DataFetchServiceConstants;
import org.devgateway.importtool.endpoint.Param;
import org.devgateway.importtool.model.FetchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

@org.springframework.stereotype.Service
public class ActivityFetchService {

	@Autowired
	private DataSourceService dataSourceService;
    @Value("${server.default_country}")
    private String defaultCountry;

	private Log log = LogFactory.getLog(getClass());

	public FetchResult fetchResult(String reportingOrg,List<Param>parameters) throws FileNotFoundException {

        File f = new File(getFileName(reportingOrg));
        Document doc;
        if (f.exists()) {
            InputStream fileInputStream = new FileInputStream(f);
            doc = this.createXMLDocument(fileInputStream);
        } else {
            doc = this.createXMLDocument(fetch(reportingOrg, parameters));
        }
        return this.buildResult(doc);
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
		return this.createXMLDocument(fetchFetchFromDataStoreAsString(reportingOrg, parameters));
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
	public String fetch(String reportingOrg, List<Param> params) {
		String fileName = getFileName(reportingOrg);
		String responseText = fetchFetchFromDataStoreAsString(reportingOrg, params);
		try {
			Files.write(Paths.get(fileName), responseText.getBytes("UTF-8"));
		} catch (IOException e) {
		    //if the file could not be sent, we return all the same the string so it can be processed online
			log.error("cannot save file ", e);
		}
		return responseText;
	}


	private String getFileName(String reportingOrg) {
		return System.getProperty(DataFetchServiceConstants.ACTIVITIES_FILES_STORE) + File.separator +
                reportingOrg + ".xml";
	}

	private Document createXMLDocument(String responseText) {
		InputStream inputStream = new ByteArrayInputStream(responseText.getBytes());
		return createXMLDocument(inputStream);

	}

	private Document createXMLDocument(InputStream inputStream ) {
		Document doc = null;
		if (inputStream != null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringElementContentWhitespace(true);
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();

				doc = builder.parse(inputStream);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				//TODO properly handle errors
				log.error("Error parsing fetched data : " , e);
			}	
		}	
		
		return doc;
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
