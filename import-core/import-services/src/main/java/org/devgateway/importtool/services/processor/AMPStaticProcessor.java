package org.devgateway.importtool.services.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.services.processor.helper.ActionResult;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldMapping;
import org.devgateway.importtool.services.processor.helper.FieldType;
import org.devgateway.importtool.services.processor.helper.FieldValue;
import org.devgateway.importtool.services.processor.helper.FieldValueMapping;
import org.devgateway.importtool.services.processor.helper.IDestinationProcessor;
import org.devgateway.importtool.services.processor.helper.InternalDocument;
import org.devgateway.importtool.services.processor.helper.JsonBean;
import org.devgateway.importtool.services.processor.helper.TokenHeaderInterceptor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AMPStaticProcessor implements IDestinationProcessor {

	private Log log = LogFactory.getLog(getClass());

	private String DEFAULT_ID_FIELD = "amp-identifier";
	private String DEFAULT_TITLE_FIELD = "title";
	private String baseURL;

	private String fieldsEndpoint;
	private String documentsEndpoint;
	private String documentsTestEndpoint;
	private String authenticationToken;
	private Boolean testMode = false;
	private String descriptiveName = "AMP 2.11";

	private List<Field> fieldList = new ArrayList<Field>();

	private List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
	private RestTemplate template;

	public AMPStaticProcessor(String authenticationToken) {
		this.setAuthenticationToken(authenticationToken);
		this.baseURL = "http://localhost:8081";
		this.setFieldsEndpoint("/rest/activity/fields");
		this.setDocumentsEndpoint("/rest/activity/projects");
		this.setDocumentsTestEndpoint("activity_list.json");
		instantiateStaticFields();
	}

	private void instantiateStaticFields() {
		// Fixed fields
		fieldList.add(new Field("IATI Identifier", "iati-identifier", FieldType.STRING, false));
		fieldList.add(new Field("Title", "title", FieldType.MULTILANG_STRING, false));

		// Code Lists
		Field activityStatus = new Field("Activity Status", "activity_status", FieldType.LIST, true);
		activityStatus.setPossibleValues(getCodeListValues("activity_status"));
		fieldList.add(activityStatus);

		Field typeOfAssistence = new Field("Type of Assistance", "type_of_assistance", FieldType.LIST, true);
		typeOfAssistence.setPossibleValues(getToA());
		fieldList.add(typeOfAssistence);

		Field primarySector = new Field("Primary Sector", "primary_sectors", FieldType.LIST, true);
		primarySector.setPossibleValues(getCodeListValues("primary_sectors~sector_id"));
		fieldList.add(primarySector);

		// Multi-language strings
		fieldList.add(new Field("Activity Description", "activity_description", FieldType.MULTILANG_STRING, true));

		// Dates
		fieldList.add(new Field("Planned Start Date", "planned_start_date", FieldType.DATE, true));
		fieldList.add(new Field("Actual Start Date", "actual_start_date", FieldType.DATE, true));
		fieldList.add(new Field("Original Completion Date", "original_completion_date", FieldType.DATE, true));
		fieldList.add(new Field("Actual Completion Date", "actual_completion_date", FieldType.DATE, true));
	}

	private List<FieldValue> getToA() {
		// TODO: Replace with information from endpoints once it's available
		List<FieldValue> list = new ArrayList<FieldValue>();
		FieldValue fv1 = new FieldValue();
		fv1.setIndex(0);
		fv1.setCode("10");
		fv1.setValue("Grant");
		list.add(fv1);
		FieldValue fv2 = new FieldValue();
		fv2.setIndex(1);
		fv2.setCode("11");
		fv2.setValue("Loan");
		list.add(fv2);
		FieldValue fv3 = new FieldValue();
		fv3.setIndex(2);
		fv3.setCode("12");
		fv3.setValue("Debt Relief");
		list.add(fv3);
		FieldValue fv4 = new FieldValue();
		fv4.setIndex(3);
		fv4.setCode("13");
		fv4.setValue("Government Funds");
		list.add(fv4);
		return list;
	}

	private List<FieldValue> getCodeListValues(String codeListName) {
		String result = "";
		List<FieldValue> possibleValues = new ArrayList<FieldValue>();
		RestTemplate restTemplate = getRestTemplate();
		result = restTemplate.getForObject(baseURL + this.getFieldsEndpoint() + "/" + codeListName, String.class);

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(result);

			if (jsonNode.isArray()) {
				int index = 0;
				Iterator<JsonNode> mainNode = jsonNode.elements();
				while (mainNode.hasNext()) {
					JsonNode node = mainNode.next();
					String code = node.get("id").asText();
					String value = node.get("value").asText();
					FieldValue fv = new FieldValue();
					fv.setIndex(index++);
					fv.setCode(code);
					fv.setValue(value);
					possibleValues.add(fv);
				}
			}

		} catch (Exception e) {
			log.error("Couldn't retrieve values from Endpoint. Exception: " + e.getMessage());
		}

		return possibleValues;
	}

	@Override
	public List<Field> getFields() {
		return fieldList;
	}

	@Override
	// Updated!
	public List<InternalDocument> getDocuments(Boolean onlyEditable) {
		List<InternalDocument> list = new ArrayList<InternalDocument>();
		String result = "";

		try {
			if (testMode) {
				InputStream input = this.getClass().getResourceAsStream("AMP/sample_files/" + this.getDocumentsTestEndpoint());
				result = IOUtils.toString(input, "UTF-8");
			} else {
				RestTemplate restTemplate = getRestTemplate();
				log.debug(this.authenticationToken);
				result = restTemplate.getForObject(baseURL + this.getDocumentsEndpoint(), String.class);
			}
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode;
			jsonNode = mapper.readTree(result);
			if (jsonNode.isArray()) {
				jsonNode.forEach((JsonNode node) -> {
					Boolean edit = node.get("edit").asBoolean();

					String id = node.get("internal_id").asText();
					String internalId = node.get("amp_id").asText();
					String identifier = node.get("project_code").asText();
					Map<String, String> title = extractMultilanguageText(node.get("project_title"));
					String dateString = node.get("creation_date").asText();

					InternalDocument document = new InternalDocument();
					document.setIdentifier(identifier);
					document.addStringField("id", id);
					document.addStringField("internalId", internalId);
					document.addStringField("amp-identifier", identifier);
					document.addMultilangStringField("title", title);
					document.addStringField("dateString", dateString);

					if (onlyEditable) {
						if (edit)
							list.add(document);
					} else {
						list.add(document);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	private Map<String, String> extractMultilanguageText(JsonNode jsonNode) {
		Iterator<Entry<String, JsonNode>> it = jsonNode.fields();
		Map<String, String> languages = new HashMap<String, String>();
		while (it.hasNext()) {
			Entry<String, JsonNode> entry = it.next();
			languages.put(entry.getKey(), entry.getValue().asText());
		}
		return languages;
	}

	private RestTemplate getRestTemplate() {
		if (template == null)
			template = new RestTemplate();
		template.setInterceptors(this.interceptors);
		return template;
	}

	@Override
	public String getIdField() {
		return DEFAULT_ID_FIELD;
	}

	@Override
	public String getTitleField() {
		return DEFAULT_TITLE_FIELD;
	}

	private String getFieldsEndpoint() {
		return fieldsEndpoint;
	}

	private void setFieldsEndpoint(String fieldsEndpoint) {
		this.fieldsEndpoint = fieldsEndpoint;
	}

	@Override
	public void setAuthenticationToken(String authToken) {
		this.interceptors.add(new TokenHeaderInterceptor(authToken));
		this.authenticationToken = authToken;
	}

	public String getDocumentsEndpoint() {
		return documentsEndpoint;
	}

	public void setDocumentsEndpoint(String documentsEndpoint) {
		this.documentsEndpoint = documentsEndpoint;
	}

	public Boolean getTestMode() {
		return testMode;
	}

	public void setTestMode(Boolean testMode) {
		this.testMode = testMode;
	}

	@Override
	public ActionResult insert(InternalDocument source, List<FieldMapping> fieldMapping, List<FieldValueMapping> valueMapping) {

		JsonBean project = new JsonBean();
		// Mandatory Fields
		project.set("project_code", source.getIdentifier());

		// List of selected fields
		for (FieldValueMapping mapping : valueMapping) {
			Field sourceField = mapping.getSourceField();
			Field destinationField = mapping.getDestinationField();

			switch (sourceField.getType()) {
			case LIST:
				project.set(destinationField.getFieldName(), getCodeFromList(source, mapping));
				break;
			case MULTILANG_STRING:
				project.set(destinationField.getFieldName(), getMapFromString(source, mapping));
				break;
			case DATE:
				project.set(destinationField.getFieldName(), getDateFromString(source, mapping));
				break;
			case STRING:
				project.set(destinationField.getFieldName(), getString(source, mapping));
				break;
			case TRANSACTION:
				// TODO: Implement Transaction
				break;
			case ORGANIZATION:
				// TODO: Implement Organization
				break;
			default:
				break;
			}
		}

		ActionResult result;

		RestTemplate restTemplate = getRestTemplate();
		try {
			String resultPost = restTemplate.postForObject(baseURL + "/rest/activity", project, String.class);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode;
			jsonNode = mapper.readTree(resultPost);
			JsonNode errorNode = jsonNode.get("error");

			if (errorNode == null || errorNode.isNull()) {
				Integer id = jsonNode.get("internal_id").asInt();
				result = new ActionResult(id.toString(), "INSERT", "OK", "Project has been inserted");
			} else {
				String error = errorNode.toString();
				result = new ActionResult("N/A", "REJECT", "ERROR", "Error: " + error);
			}

		} catch (RestClientException e) {
			result = new ActionResult("N/A", "ERROR", "ERROR", "REST Exception:" + e.getMessage());
		} catch (JsonProcessingException e) {
			result = new ActionResult("N/A", "ERROR", "ERROR", "Processing Exception:" + e.getMessage());
		} catch (IOException e) {
			result = new ActionResult("N/A", "ERROR", "ERROR", "IO Exception :" + e.getMessage());
		}
		return result;
	}

	private String getString(InternalDocument source, FieldValueMapping mapping) {
		return source.getStringFields().get(mapping.getSourceField().getFieldName());
	}

	private Date getDateFromString(InternalDocument source, FieldValueMapping mapping) {
		String uniqueFieldName = mapping.getSourceField().getUniqueFieldName();
		Date date = source.getDateFields().get(uniqueFieldName);
		return date != null ? date : null;
	}

	private Map<String, String> getMapFromString(InternalDocument source, FieldValueMapping mapping) {
		Map<String, String> langString = source.getMultilangFields().get(mapping.getSourceField().getFieldName());
		return langString;
	}

	private String getCodeFromList(InternalDocument source, FieldValueMapping mapping) {
		Object value = source.getStringFields().get(mapping.getSourceField().getFieldName());
		Map<Integer, Integer> valueMapIndex = mapping.getValueIndexMapping();
		List<FieldValue> sourcePossibleValues = mapping.getSourceField().getPossibleValues();
		String stringValue = (String) value;
		Optional<FieldValue> optSourceValueIndex = sourcePossibleValues.stream().filter(n -> {
			return n.getCode().equals(stringValue);
		}).findAny();
		Integer sourceValueIndex = optSourceValueIndex.get().getIndex();
		Integer destinationValueIndex = valueMapIndex.get(sourceValueIndex);
		List<FieldValue> destinationPossibleValues = mapping.getDestinationField().getPossibleValues();
		FieldValue destinationValue = destinationPossibleValues.get(destinationValueIndex);
		return destinationValue.getCode();
	}

	@Override
	public ActionResult update(InternalDocument source, InternalDocument destination, List<FieldMapping> fieldMapping, List<FieldValueMapping> valueMapping) {
		ActionResult result = new ActionResult("1", "UPDATE", "OK", "Project has been updated");
		log.debug("Update new document in destination system");
		return result;
	}

	@Override
	public String getDescriptiveName() {

		return this.descriptiveName;
	}

	private String getDocumentsTestEndpoint() {
		return documentsTestEndpoint;
	}

	private void setDocumentsTestEndpoint(String documentsTestEndpoint) {
		this.documentsTestEndpoint = documentsTestEndpoint;
	}

}
