package org.devgateway.importtool.services.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.services.dto.JsonBean;
import org.devgateway.importtool.services.dto.MappedProject;
import org.devgateway.importtool.services.processor.helper.*;
import org.devgateway.importtool.services.processor.helper.interceptors.CookieHeaderInterceptor;
import org.devgateway.importtool.services.request.ImportRequest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

@Component("AMP34")
public class AMP34TestDestinationProcessor implements IDestinationProcessor {

	private Log log = LogFactory.getLog(getClass());

	private String DEFAULT_TITLE_FIELD = "title";
	private String baseURL;

	private String fieldsEndpoint;
	private String fieldsTestEndpoint;

	private String possibleValuesEndpoint;
	private String documentsEndpoint;
	private String documentsTestEndpoint;
	private Boolean testMode = false;
	private String descriptiveName = "AMP 3.4";

	private List<MappedProject> projectsReadyToBePosted = new ArrayList<>();

	private List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
	private RestTemplate template;

	public AMP34TestDestinationProcessor() {
		this.baseURL = "http://localhost:8081";
		this.setFieldsEndpoint("/rest/activity/fields");
		this.setFieldsTestEndpoint("destination_fields.json");
		this.setDocumentsEndpoint("/rest/activity/projects");
		this.setDocumentsTestEndpoint("activity_list.json");
		this.setPossibleValuesEndpoint("_possiblevalues.json");
	}
	
	@Override
	public void initialize(String ampJSessionId) {
		this.interceptors.add(new CookieHeaderInterceptor(ampJSessionId));
	}
	
	@Override
	public List<Field> getFields() {
		List<Field> list = new ArrayList<>();
		String result = "";

		if (testMode) {
			InputStream input = this.getClass().getResourceAsStream("AMP/sample_files/" + this.getFieldsTestEndpoint());

			try {
				result = IOUtils.toString(input, "UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			RestTemplate restTemplate = getRestTemplate();
			result = restTemplate.getForObject(baseURL + this.getFieldsEndpoint(), String.class);
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(result);

			if (jsonNode.isArray()) {
				jsonNode.forEach((JsonNode node) -> {
					Field field = new Field();
					String fieldName = node.get("field_name").asText();
					field.setFieldName(fieldName);
					String fieldLabel = node.get("field_label").asText();
					field.setDisplayName(fieldLabel);
					// List<FieldValue> possibleValues =
					// getPossibleValues(fieldName);
					// field.setPossibleValues(possibleValues);
					FieldType fieldType = FieldType.STRING;

					switch (node.get("field_type").asText()) {
					case "boolean":
						fieldType = FieldType.BOOLEAN;
						break;
					case "string":
						if (node.get("translatable").asBoolean())
							fieldType = FieldType.MULTILANG_STRING;
						else
							fieldType = FieldType.STRING;
						break;
					case "list":
						fieldType = FieldType.LIST;
						break;
					case "integer":
						fieldType = FieldType.INTEGER;
						break;
					}
					field.setType(fieldType);
					list.add(field);
				});
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	@SuppressWarnings("unused")
	private List<FieldValue> getPossibleValues(String fieldName) {
		List<FieldValue> list = new ArrayList<FieldValue>();
		String result = null;
		try {
			if (testMode) {
				InputStream input = this.getClass().getResourceAsStream("AMP/sample_files/" + fieldName + this.getPossibleValuesEndpoint());

				try {
					result = IOUtils.toString(input, "UTF-8");
				} catch (Exception e) {
					log.error("Couldn't retrieve values for: " + fieldName);
					// e.printStackTrace();
				}

			} else {
				RestTemplate restTemplate = getRestTemplate();
				result = restTemplate.getForObject(baseURL + "/" + fieldName + this.getPossibleValuesEndpoint(), String.class);
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
	public String getTitleField() {
		return DEFAULT_TITLE_FIELD;
	}

	@Override
	public String getProcessorVersion() {
		return null;
	}


	public void setProcessorVersion(String processorVersion) {

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
	public void reset() {

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
	public void insert(InternalDocument source, List<FieldMapping> fieldMapping, List<FieldValueMapping> valueMapping,
                       ImportRequest importRequest) {
		JsonBean project = transformProject(source, fieldMapping, valueMapping, importRequest);
		projectsReadyToBePosted.add(getMappedProjectFromSource(source, project));
	}

	private JsonBean transformProject(InternalDocument source, List<FieldMapping> fieldMapping,
									  List<FieldValueMapping> valueMapping, ImportRequest importRequest) {
		return new JsonBean();
	}

	@Override
	public void update(InternalDocument source, InternalDocument destination, List<FieldMapping> fieldMapping, List<FieldValueMapping> valueMapping, boolean overrideTitle, ImportRequest importRequest) {
		ActionResult result = new ActionResult("1", "UPDATE", "OK", "Project has been updated");
		log.debug("Update new document in destination system");
		//return result;
	}

	@Override
	public String getDescriptiveName() {

		return this.descriptiveName;
	}

	private String getFieldsTestEndpoint() {
		return fieldsTestEndpoint;
	}

	private void setFieldsTestEndpoint(String fieldsTestEndpoint) {
		this.fieldsTestEndpoint = fieldsTestEndpoint;
	}

	private String getDocumentsTestEndpoint() {
		return documentsTestEndpoint;
	}

	private void setDocumentsTestEndpoint(String documentsTestEndpoint) {
		this.documentsTestEndpoint = documentsTestEndpoint;
	}

	@Override
	public void setActionStatus(ActionStatus documentMappingStatus) {
		// TODO Auto-generated method stub
		
	}
	public List<DocumentMapping>  preImportProcessing(List<DocumentMapping> documentMappings){
		return documentMappings;
	}

    @Override
    public void loadProjectsForUpdate(List<String> listOfAmpIds) {

    }

    @Override
    public List<ActionResult> processProjectsInBatch(ActionStatus importStatus) {
		List<ActionResult> finalResults = new ArrayList<>();

		for (MappedProject project : projectsReadyToBePosted) {
			ActionResult result = new ActionResult("1", "INSERT", "OK", "Project has been inserted: " + project.getProjectIdentifier());
			finalResults.add(result);
		}

		projectsReadyToBePosted = new ArrayList<>();
		return finalResults;
    }
}
