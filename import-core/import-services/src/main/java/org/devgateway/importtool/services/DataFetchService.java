package org.devgateway.importtool.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.endpoint.Param;
import org.devgateway.importtool.model.FetchResult;
import org.hibernate.annotations.Fetch;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@org.springframework.stereotype.Service
public class DataFetchService {
	private static Integer LIMIT = 50;
	private Log log = LogFactory.getLog(getClass());


	public FetchResult fetchResult(String url) {
		return fetchResult(url, null);
	}

	public FetchResult fetchResult(String url,List<Param>parameters) {
		Document doc = fetch(url, parameters);
		return buildResult(doc);
	}

	public Document fetch(String url,List<Param> parameters ) {
		 Integer total = 0;
		 Integer offset = 0;

		 RestTemplate restTemplate = new RestTemplate();

		 FetchResult result = new FetchResult();
		 //TODO: iterate - pulling data in batches of 50 and adding to results object
		 String fullUrl =url + getParameters(parameters);

		 String reponseText = restTemplate.getForObject(fullUrl, String.class);

		Document doc = this.createXMLDocument(reponseText);
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
				log.error("Error parsing fetched data : " + e);
			}	
		}	
		
		return doc;
	}
	
	private FetchResult buildResult(Document doc) {
		FetchResult result = new FetchResult();
		if (doc != null) {
			NodeList activities = doc.getElementsByTagName("iati-activity");
			if (result.getVersions() == null) {
				result.setVersions(new HashSet<>());
			}			
			result.setActivities(activities);
			//here we are ite
			for (int i = 0; i < activities.getLength(); i++) {				
				Element activity = (Element) activities.item(i);
				String iatiVersion = activity.getAttribute("iati-extra:version");				
				result.getVersions().add(iatiVersion);			
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
}
