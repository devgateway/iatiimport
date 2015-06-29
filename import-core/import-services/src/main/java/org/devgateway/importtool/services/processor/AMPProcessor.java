package org.devgateway.importtool.services.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldType;
import org.devgateway.importtool.services.processor.helper.FieldValue;
import org.devgateway.importtool.services.processor.helper.IDestinationProcessor;
import org.devgateway.importtool.services.processor.helper.InternalDocument;
import org.devgateway.importtool.services.processor.helper.ActionResult;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("AMP210")
public class AMPProcessor implements IDestinationProcessor {

	private Log log = LogFactory.getLog(getClass());

	private String DEFAULT_ID_FIELD = "amp-identifier";
	private String DEFAULT_TITLE_FIELD = "title";
	private String baseURL;
	private String fieldsEndpoint;
	private String possibleValuesEndpoint;
	private String documentsEndpoint;
	@SuppressWarnings("unused")
	private String authenticationToken;
	private Boolean testMode = false;
	private String descriptiveName = "AMP 2.11";

	public AMPProcessor() {
		this.baseURL = "http://localhost:8080/mockup";
		this.setFieldsEndpoint("destination_fields.json");
		this.setPossibleValuesEndpoint("_possiblevalues.json");
		this.setDocumentsEndpoint("activity_list.json");
	}

	@Override
	public List<Field> getFields() {
		List<Field> list = new ArrayList<Field>();
		String result = "";

		if (testMode) {
			InputStream input = this.getClass().getResourceAsStream(
					"AMP/sample_files/" + this.getFieldsEndpoint());

			try {
				result = IOUtils.toString(input, "UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			RestTemplate restTemplate = new RestTemplate();
			result = restTemplate.getForObject(
					baseURL + "/" + this.getFieldsEndpoint(), String.class);
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(result);

			if (jsonNode.isArray()) {
				jsonNode.forEach((JsonNode node) -> {
					Field field = new Field();
					String fieldName = node.get("field_name").asText();
					field.setFieldName(fieldName);
					String displayName = node.get("display_name").asText();
					field.setDisplayName(displayName);
					List<FieldValue> possibleValues = getPossibleValues(fieldName);
					field.setPossibleValues(possibleValues);
					FieldType fieldType = FieldType.STRING;

					switch (node.get("field_type").asText()) {
					case "string":
						fieldType = FieldType.STRING;
						break;
					case "list":
						fieldType = FieldType.LIST;
						break;
					case "integer":
						fieldType = FieldType.INTEGER;
						break;
					case "multilang_string":
						fieldType = FieldType.MULTILANG_STRING;
						break;
						
					}
					field.setType(fieldType);
					list.add(field);
				});
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

		return list;
	}

	private List<FieldValue> getPossibleValues(String fieldName) {
		List<FieldValue> list = new ArrayList<FieldValue>();
		String result = null;
		try {
			if (testMode) {
				InputStream input = this.getClass().getResourceAsStream(
						"AMP/sample_files/" + fieldName
								+ this.getPossibleValuesEndpoint());

				try {
					result = IOUtils.toString(input, "UTF-8");
				} catch (Exception e) {
					log.error("Couldn't retrieve values for: " + fieldName);
					// e.printStackTrace();
				}

			} else {
				RestTemplate restTemplate = new RestTemplate();
				result = restTemplate.getForObject(baseURL + "/" + fieldName
						+ this.getPossibleValuesEndpoint(), String.class);
			}

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode;
			if (result != null) {
				jsonNode = mapper.readTree(result);
				if (jsonNode.isArray()) {
					jsonNode.forEach((JsonNode node) -> {
						String id = node.get("id").asText();
						String value = node.get("value").asText();
						FieldValue fv = new FieldValue();
						fv.setCode(id);
						fv.setValue(value);
						list.add(fv);
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<InternalDocument> getDocuments() {
		List<InternalDocument> list = new ArrayList<InternalDocument>();
		String result = "";

		try {
			if (testMode) {
				InputStream input = this.getClass().getResourceAsStream(
						"AMP/sample_files/" + this.getDocumentsEndpoint());
				result = IOUtils.toString(input, "UTF-8");
			} else {
				RestTemplate restTemplate = new RestTemplate();
				result = restTemplate.getForObject(
						baseURL + "/" + this.getDocumentsEndpoint(),
						String.class);
			}
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode;
			jsonNode = mapper.readTree(result);
			if (jsonNode.isArray()) {
				jsonNode.forEach((JsonNode node) -> {
					String id = node.get("amp_activity_id").asText();
					String internalId = node.get("amp_id").asText();
					String identifier = node.get("amp_identifier").asText();
					String title = node.get("title").asText();
					String dateString = node.get("created_date").asText();
					String status = node.get("activity_status").asText();

					InternalDocument document = new InternalDocument();
					document.setIdentifier(identifier);
					document.setTitle(title);
					document.addStringField("id", id);
					document.addStringField("internalId", internalId);
					document.addStringField("amp-identifier", identifier);
					document.addStringField("title", title);
					document.addStringField("dateString", dateString);
					document.addStringField("activity_status", status);
					list.add(document);
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
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

	private String getPossibleValuesEndpoint() {
		return possibleValuesEndpoint;
	}

	private void setPossibleValuesEndpoint(String possibleValuesEndpoint) {
		this.possibleValuesEndpoint = possibleValuesEndpoint;
	}

	@Override
	public void setAuthenticationToken(String authToken) {
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
	public ActionResult insert(InternalDocument source) {
		ActionResult result = new ActionResult("INSERT",
				"Project has been inserted");
		log.debug("Update new document in destination system");
		return result;
	}

	@Override
	public ActionResult update(InternalDocument source,
			InternalDocument destination) {
		ActionResult result = new ActionResult("UPDATE",
				"Project has been updated");
		log.debug("Update new document in destination system");
		return result;
	}

	@Override
	public String getDescriptiveName() {

		return this.descriptiveName;
	}

}
