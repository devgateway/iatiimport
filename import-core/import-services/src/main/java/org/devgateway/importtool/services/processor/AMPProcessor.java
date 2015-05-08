package org.devgateway.importtool.services.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldType;
import org.devgateway.importtool.services.processor.helper.IDestinationProcessor;
import org.devgateway.importtool.services.processor.helper.InternalDocument;
import org.devgateway.importtool.services.processor.helper.MappingResult;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("AMP210")
public class AMPProcessor implements IDestinationProcessor {

	private Log log = LogFactory.getLog(getClass());

	private String DEFAULT_ID_FIELD = "amp-identifier";
	private String baseURL;
	private String fieldsEndpoint;
	private String possibleValuesEndpoint;
	private String documentsEndpoint;
	private String authenticationToken;

	public AMPProcessor() {
		this.baseURL = "http://localhost:8080/mockup";
		this.setFieldsEndpoint("destination_fields.json");
		this.setPossibleValuesEndpoint("_possiblevalues.json");
		this.setDocumentsEndpoint("activity_list.json");
	}

	@Override
	public List<Field> getFields() {
		List<Field> list = new ArrayList<Field>();
		RestTemplate restTemplate = new RestTemplate();
		// List<Field> fields = restTemplate.getForObject(, ArrayList.class);
		String result = restTemplate.getForObject(
				baseURL + "/" + this.getFieldsEndpoint(), String.class);
		ObjectMapper mapper = new ObjectMapper();

		try {
			JsonNode jsonNode = mapper.readTree(result);

			if (jsonNode.isArray()) {
				jsonNode.forEach((JsonNode node) -> {
					Field field = new Field();
					String fieldName = node.get("field_name").asText();
					field.setFieldName(fieldName);
					Map<String, String> possibleValues = getPossibleValues(fieldName);
					field.setPossibleValues(possibleValues);
					FieldType fieldType = FieldType.STRING;

					switch (node.get("field_name").asText()) {
					case "string":
						fieldType = FieldType.STRING;
						break;
					case "array":
						fieldType = FieldType.ARRAY;
						break;
					case "integer":
						fieldType = FieldType.INTEGER;
						break;
					}
					field.setType(fieldType);
					list.add(field);
				});
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	private Map<String, String> getPossibleValues(String fieldName) {
		Map<String, String> map = new HashMap<String, String>();
		RestTemplate restTemplate = new RestTemplate();

		try {
			String result = restTemplate.getForObject(baseURL + "/" + fieldName
					+ this.getPossibleValuesEndpoint(), String.class);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode;
			jsonNode = mapper.readTree(result);
			if (jsonNode.isArray()) {
				jsonNode.forEach((JsonNode node) -> {
					String id = node.get("id").asText();
					String value = node.get("value").asText();

					map.put(id, value);
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public List<InternalDocument> getDocuments() {
		List<InternalDocument> list = new ArrayList<InternalDocument>();
		RestTemplate restTemplate = new RestTemplate();

		try {
			String result = restTemplate.getForObject(
					baseURL + "/" + this.getDocumentsEndpoint(), String.class);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode;
			jsonNode = mapper.readTree(result);
			if (jsonNode.isArray()) {
				jsonNode.forEach((JsonNode node) -> {
					String id = node.get("amp_activity_id").asText();
					String internalId = node.get("amp_id").asText();
					String title = node.get("title").asText();
					String dateString = node.get("created_date").asText();

					InternalDocument document = new InternalDocument();
					document.addStringField("id", id);
					document.addStringField("internalId", internalId);
					document.addStringField("title", title);
					document.addStringField("dateString", dateString);
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
	public MappingResult insertOrUpdate(InternalDocument doc) {
		// PErform Calls.
		return null;
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

}
