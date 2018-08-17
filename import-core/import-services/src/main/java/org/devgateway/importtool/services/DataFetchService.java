package org.devgateway.importtool.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.endpoint.DataFetchServiceConstants;
import org.devgateway.importtool.endpoint.Param;
import org.devgateway.importtool.model.FetchResult;
import org.hibernate.annotations.Fetch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

@org.springframework.stereotype.Service
public class DataFetchService {
	private static Integer LIMIT = 50;
	@Autowired
	private DataSourceService dataSourceService;

	private Log log = LogFactory.getLog(getClass());

	public FetchResult fetchResult(String reportingOrg,List<Param>parameters) {
		Document doc = fetch(reportingOrg, parameters);
		return buildResult(doc);
	}
	private String getUrlForReportingOrg(String reportingOrg) {

		String customUrl = dataSourceService.getDataSourceURL(reportingOrg);
		customUrl = (customUrl != null) ? customUrl : DataFetchServiceConstants.DEFAULT_URL;
		
		return customUrl;
	}

	public Document fetch(String reportingOrg, List<Param> parameters ) {
		 Integer total = 0;
		 Integer offset = 0;

		 RestTemplate restTemplate = new RestTemplate();
		 //TODO: iterate - pulling data in batches of 50 and adding to results object
		 log.info(getUrlForReportingOrg(reportingOrg) +
				 getParameters(parameters));
		 
		 String responseText = restTemplate.getForObject(getUrlForReportingOrg(reportingOrg) +
				 getParameters(parameters), String.class);

		Document doc = this.createXMLDocument(responseText);
		 return doc;
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

	private Document createXMLDocument(String responseText) {
		Document doc = null;
		if (responseText != null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringElementContentWhitespace(true);
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				InputStream inputStream = new ByteArrayInputStream(responseText.getBytes());			
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
