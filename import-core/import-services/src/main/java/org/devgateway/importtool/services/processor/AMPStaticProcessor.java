package org.devgateway.importtool.services.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.endpoint.EPMessages;
import org.devgateway.importtool.exceptions.CurrencyNotFoundException;
import org.devgateway.importtool.services.dto.JsonBean;
import org.devgateway.importtool.services.dto.MappedProject;
import org.devgateway.importtool.services.processor.destination.TokenCookieHeaderInterceptor;
import org.devgateway.importtool.services.processor.helper.ActionResult;
import org.devgateway.importtool.services.processor.helper.ActionStatus;
import org.devgateway.importtool.services.processor.helper.Constants;
import org.devgateway.importtool.services.processor.helper.DocumentMapping;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldMapping;
import org.devgateway.importtool.services.processor.helper.FieldType;
import org.devgateway.importtool.services.processor.helper.FieldValue;
import org.devgateway.importtool.services.processor.helper.FieldValueMapping;
import org.devgateway.importtool.services.processor.helper.IDestinationProcessor;
import org.devgateway.importtool.services.processor.helper.InternalDocument;
import org.devgateway.importtool.services.processor.helper.ValueMappingException;
import org.devgateway.importtool.services.processor.helper.ValueNotEnabledException;
import org.devgateway.importtool.services.request.ImportRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.devgateway.importtool.services.processor.destination.AmpStaticProcessorConstants.*;

// TODO: Sort methods, move classes to generic helpers for all processors if possible
// TODO: Clean up code, find opportunities to reuse methods (example update/insert)
// TODO: Add default values when mappings are missing, reading them from configuration db or files
// TODO: Better error handling to the end user. Friendlier user messages, specially when referencing a missing dependency
@Component("AMP_PROCESSOR")
@Scope("session")
public class AMPStaticProcessor implements IDestinationProcessor {
	private String descriptiveName = "AMP";


    private Log log = LogFactory.getLog(getClass());
	// AMP Configuration Details
	private String baseURL;
	private String ampIatiIdField;
	private Integer ampImplementationLevel;

	private List<Field> fieldList = new ArrayList<Field>();
	private List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();

	private List<String> destinationFieldsList = new ArrayList<String>(); // fields returned by current AMP installation
	private Map<String, List<FieldValue>> allFieldValuesForDestinationProcessor; // this holds the list of possible
	// values that wr retrieve from the endpoint at once, instead of one by one
	private HashMap<String, Map<String, String>> destinationFieldsListLabels = new HashMap<String, Map<String, String>>();
	// translations we ask amp to translate for us
	private Map<String, Map<String, String>> ampTranslations = new HashMap<>();

	public List<String> getDestinationFieldsList() {
		return destinationFieldsList;
	}
	private String processorVersion;


	public void setDestinationFieldsList(List<String> destinationFieldsList) {
		this.destinationFieldsList = destinationFieldsList;
	}

	//list of projects to be sent to the AMP
	private List<MappedProject> projectsReadyToBePosted;
	//List of projects to be updated into the amp. Will be loaded in batches
	List<JsonBean> projectsToBeUpdated;

	@Override
	public String getProcessorVersion() {
		return processorVersion;
	}
	@Override
	public void setProcessorVersion(String processorVersion) {
		this.processorVersion = processorVersion;
	}

	private RestTemplate restTemplate;

	private ActionStatus actionStatus;

	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	public Map<String, Map<String, String>> getAmpTranslations() {
		return ampTranslations;
	}

	public void setAmpTranslations(Map<String, Map<String, String>> ampTranslations) {
		this.ampTranslations = ampTranslations;
	}

	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}
	public AMPStaticProcessor(){

	}
	@Override
	public void initialize(String authenticationToken) {

		this.setAuthenticationToken(authenticationToken);
		this.restTemplate = getRestTemplate();

		baseURL = System.getProperty(BASEURL_PROPERTY);

		if (StringUtils.isEmpty(baseURL)) {
			this.baseURL = BASEURL_DEFAULT_VALUE;
		}

		ampIatiIdField = System.getProperty(AMP_IATI_ID_FIELD_PROPERTY);
		if (StringUtils.isEmpty(ampIatiIdField)) {
			ampIatiIdField = AMP_IATI_ID_FIELD_DEFAULT_VALUE;
		}
		String ampImplementationLevelProperty = System.getProperty(AMP_IMPLEMENTATION_LEVEL_ID_FIELD_PROPERTY);
		if (ampImplementationLevelProperty != null) {
			ampImplementationLevel = Integer.parseInt(ampImplementationLevelProperty);
		} else {
			ampImplementationLevel = AMP_IMPLEMENTATION_LEVEL_ID_DEFAULT_VALUE;
		}
		initializeProjectsLists();
		instantiateStaticFields();
	}

	@Override
	public void reset(){
		baseURL = null;
		ampIatiIdField = null;
		ampImplementationLevel = null;
		fieldList =  new ArrayList<>();
		interceptors.clear();
		destinationFieldsList = new ArrayList<>();
		allFieldValuesForDestinationProcessor = null;
		destinationFieldsListLabels = new HashMap<String, Map<String, String>>();
		ampTranslations = new HashMap<>();
		processorVersion = null;
		projectsReadyToBePosted = new ArrayList<>();
		projectsToBeUpdated = new ArrayList<>();
		restTemplate = null;
		actionStatus = null;
		jsonNode = null;
	}

	private void initializeProjectsLists() {
		this.projectsReadyToBePosted = new ArrayList<>();
		this.projectsToBeUpdated = new ArrayList<>();
	}

	@Override
	public List<Field> getFields() {
		return fieldList;
	}
	JsonNode jsonNode;
	@Override
	// Updated!
	public List<InternalDocument> getDocuments(Boolean onlyEditable) {
		actionStatus = new ActionStatus(EPMessages.FETCHING_DESTINATION_PROJECTS.getDescription(), 0L,
				EPMessages.FETCHING_DESTINATION_PROJECTS.getCode());
		List<InternalDocument> list = new ArrayList<InternalDocument>();
		String result = "";

		try {
			if (jsonNode == null) {
				// we go only once to fetch documents since it make no sense to fetch 3k project
				// twice. if its faster ill save the list of editable and non editable on init and will only return
				// the list filtered
				result = this.restTemplate.getForObject(baseURL + DOCUMENTS_END_POINT, String.class);
				ObjectMapper mapper = new ObjectMapper();

				jsonNode = mapper.readTree(result);
			}
			if (jsonNode.isArray()) {
				jsonNode.forEach((JsonNode node) -> {
					InternalDocument document = new InternalDocument();

					Boolean edit = node.get("edit").asBoolean();

					String id = node.get("internal_id").asText();
					String internalId = node.get("amp_id").asText();
					// Needs to be checked, since it's configurable it might not have a value
					JsonNode ampIatiId = node.get(ampIatiIdField);					
					if (ampIatiId != null && !Constants.NULL_STRING.equals(ampIatiId.asText())) {
					    document.setIdentifier(ampIatiId.asText());
						document.addStringField(ampIatiIdField, ampIatiId.asText());
					}
					Map<String, String> title = extractMultilanguageText(node.get("project_title"));
					String dateString = node.get("creation_date").asText();

					document.addStringField("id", id);
					document.addStringField("internalId", internalId);
					document.addMultilangStringField("title", title);
					document.addStringField("dateString", dateString);
					document.setAllowEdit(edit);

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
			languages.put(entry.getKey().toLowerCase(), entry.getValue().asText());
		}
		
		if (languages.isEmpty()) {
			languages.put(DEFAULT_LANGUAGE_CODE, jsonNode.asText());
		}
		return languages;
	}

	private RestTemplate getRestTemplate() {
		if (restTemplate == null) {
			restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			restTemplate.setInterceptors(this.interceptors);
		}
		return restTemplate;
	}

	@Override
	public String getIdField() {
		return ampIatiIdField;
	}

	@Override
	public String getTitleField() {
		return DEFAULT_TITLE_FIELD;
	}

	@Override
	public void setAuthenticationToken(String authToken) {
		this.interceptors.clear();
		this.interceptors.add(new TokenCookieHeaderInterceptor(authToken));
	}


	private void updateProject(JsonBean project, InternalDocument source, List<FieldMapping> fieldMappings,
			List<FieldValueMapping> valueMappings, boolean overrideTitle, ImportRequest importRequest)
			throws ValueMappingException, CurrencyNotFoundException {
		if (overrideTitle) {
			project.set("project_title", getMultilangString(source, "project_title", "title"));
		}
		project.set(ampIatiIdField, source.getIdentifier());

		Boolean hasTransactions = false;
		for (FieldMapping mapping : fieldMappings) {
			Field sourceField = mapping.getSourceField();
			Field destinationField = mapping.getDestinationField();

			switch (sourceField.getType()) {
			case LOCATION:
				Optional<FieldValueMapping> optValueMappingLocation = valueMappings.stream().filter(n -> {
					return n.getSourceField().getFieldName().equals(mapping.getSourceField().getFieldName());
				}).findFirst();
				
				List<JsonBean> locations = getCodesFromList(source, optValueMappingLocation.get(), false);
				if (locations != null) {
					project.set(destinationField.getFieldName(), locations);
				}
				//TODO THIS IS A WORKAROUND TO BE FIXED BEFORE THE RELEASE
				/*Properties props = getExtraInfo(source, optValueMappingLocation.get(), false);
				if (props != null) {
					@SuppressWarnings("unchecked")
					LinkedHashMap<String, Integer> hm = (LinkedHashMap<String, Integer>) props.get("extra_info");
					Integer implementationLocation = hm.get("implementation_level_id");
					project.set("implementation_location", implementationLocation);
					project.set("implementation_level", ampImplementationLevel);
				}*/
				break;
			case RECIPIENT_COUNTRY:
			case LIST:
				processListDestinationProjects(source, valueMappings, project, mapping, sourceField, destinationField);
				break;
				case ORGANIZATION:
					processOrganization(project, source, fieldMappings, valueMappings, mapping, sourceField, destinationField);
					break;
			case MULTILANG_STRING:
				Object fieldValue = getMapFromString(source, destinationField.getFieldName(), mapping);
				if(destinationField.getLength() > 0 && fieldValue instanceof Map) {
					//NOOP
				} else {
					String fieldValueString = (String)fieldValue;
					if (destinationField.getLength() != 0 && fieldValueString.length() > destinationField.getLength()) {
						fieldValue = fieldValueString.substring(0, destinationField.getLength());
					} else {
						fieldValue = fieldValueString;
					}	
				}

				project.set(destinationField.getFieldName(), fieldValue);
				break;
			case DATE:
				project.set(destinationField.getFieldName(), getFormattedDateFromString(source, mapping));
				break;
			case STRING:
				project.set(destinationField.getFieldName(), getString(source, mapping));
				break;
			case TRANSACTION:
				hasTransactions = true;
				break;
			default:
				break;
			}
		}
		// Process transactions
		if (hasTransactions) {
			List<JsonBean> fundings = null;
			switch (importRequest.getImportOption()) {
			case OVERWRITE_ALL_FUNDING:
				fundings = getSourceFundings(source, fieldMappings, valueMappings, importRequest);
				break;
			case ONLY_ADD_NEW_FUNDING:
				fundings = addNewFunding(source, fieldMappings, valueMappings, project, importRequest);
				break;
			case REPLACE_DONOR_FUNDING:
				fundings = replaceDonorTransactions(source, fieldMappings, valueMappings, project, importRequest);
				break;
			default:
				break;
			}

			if (fundings != null) {
				project.set("fundings", fundings);
				project.set("donor_organization", getDonorOrgs(fundings));
			}

		}
	}

	private void processOrganization(JsonBean project, InternalDocument source, List<FieldMapping> fieldMappings,
									 List<FieldValueMapping> valueMappings, FieldMapping mapping, Field sourceField,
									 Field destinationField)  throws ValueMappingException{
		if (!Constants.FUNDING_ORG_DISPLAY_NAME.equals(sourceField.getDisplayName())
				&& !Constants.PROVIDER_ORG_DISPLAY_NAME.equals(sourceField.getDisplayName())) {

			project.set(destinationField.getFieldName(), getOrgsByRole(source, sourceField.getSubType(),
					fieldMappings, valueMappings, sourceField.getDisplayName(), destinationField.isPercentage()));
		}
	}

	private List<JsonBean> getDonorOrgs(List<JsonBean> fundings) {
		List<JsonBean> listDonorOrganizations = new ArrayList<JsonBean>();
		for (JsonBean funding : fundings) {
			Optional<Field> org = fieldList.stream().filter(n -> {
				return n.getFieldName().equals("donor_organization");
			}).findFirst();
			
			Optional<JsonBean> foundDonor = listDonorOrganizations.stream().filter(o -> {
                return o.get("organization").equals(funding.get("donor_organization_id"));
            }).findFirst();
			
			if (org.isPresent() && !foundDonor.isPresent()) {
				JsonBean donorRole = new JsonBean();
				donorRole.set("organization", funding.get("donor_organization_id"));
				if(processorVersion.equals("2.x")) {
					donorRole.set("role", 1);
				}
				listDonorOrganizations.add(donorRole);
			}
		}

		return listDonorOrganizations;

	}

	   private List<JsonBean> getOrgsByRole(InternalDocument source, String role, List<FieldMapping> fieldMappings,
	            List<FieldValueMapping> valueMappings, String fieldDisplayName, Boolean percentage) throws ValueMappingException {
	        List<JsonBean> orgs = new ArrayList<JsonBean>();
	       
	        Map<String, Map<String, String>> organizations = source.getOrganizationFields().entrySet().stream()
	                .filter(p -> {
	                    return p.getValue().get("role").equals(role);
	                }).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
	        
	        for (Entry<String, Map<String, String>> entry : organizations.entrySet()) {	                        
	            Integer orgId = getOrgIdFromList(entry.getValue().get("value"), "participating-org",
                        fieldMappings, valueMappings, false, fieldDisplayName);
                
	            JsonBean org = new JsonBean();
	            org.set("organization", orgId);
	            orgs.add(org);                
	        }
			if(percentage){
				//percentage should be configurable
				processPercentage(orgs, "percentage");
			}
	        return orgs;
	    }

	private void processPercentage(List<JsonBean> percentageObject, String percentageField) {
		Integer objectCount = percentageObject.size();
		if (objectCount > 0) {
			Integer distribution = 100 / objectCount;
			percentageObject.forEach(po -> {
				po.set(percentageField, distribution);
			});
			Integer difference = 100 - distribution * objectCount;
			if (difference ==0) {
				return;
			}
			Integer increment = 1;
			while (difference > 0) {
				for(JsonBean po:percentageObject) {
					Integer previousDistribution = (Integer) po.get(percentageField);
					po.set(percentageField, previousDistribution + increment);
                    difference -= increment;
					if (difference == 0) {
						break;
					}
				}
			}
		}
	}

	@Override
	public void loadProjectsForUpdate(List<String> listOfAmpIds) {
		for (int startIndex = 0; startIndex < listOfAmpIds.size(); startIndex += Constants.AMP_PULL_BATCH_SIZE) {
			int endIndex = Math.min(listOfAmpIds.size(), startIndex + Constants.AMP_PULL_BATCH_SIZE);

			String result = this.restTemplate.postForObject(baseURL + "/rest/activity/projects",
					listOfAmpIds.subList(startIndex, endIndex), String.class);
			List<JsonBean> lJsb = JsonBean.getListOfJsoBeanFromString(result);
			projectsToBeUpdated.addAll(lJsb);

		}
	}

	private JsonBean getProject(String documentId) {
		JsonBean jb = projectsToBeUpdated.stream().filter(p-> p.getString("amp_id").equals(documentId)).
				findFirst().orElse(null);
		return jb;
	}

	@Override
	public String getDescriptiveName() {

		return this.descriptiveName;
	}
	private MappedProject getMappedProjectfromSource(InternalDocument source, JsonBean project){
		MappedProject mappedProject = new MappedProject();
		mappedProject.setGroupingCriteria(source.getGrouping());
		mappedProject.setProjectIdentifier(source.getIdentifier());
		mappedProject.setProject(project);
		return mappedProject;
	}
	@Override
	public void update(InternalDocument source, InternalDocument destination, List<FieldMapping> fieldMapping,
			List<FieldValueMapping> valueMapping, boolean overrideTitle, ImportRequest importRequest)
			throws  ValueMappingException, CurrencyNotFoundException {
		JsonBean project = getProject(destination.getStringFields().get("internalId"));
		updateProject(project, source, fieldMapping, valueMapping, overrideTitle, importRequest);
		projectsReadyToBePosted.add(getMappedProjectfromSource(source, project));
	}

	@Override
	public void insert(InternalDocument source, List<FieldMapping> fieldMapping,
			List<FieldValueMapping> valueMapping, ImportRequest importRequest) throws ValueMappingException,
			CurrencyNotFoundException {
		JsonBean project = transformProject(source, fieldMapping, valueMapping, importRequest);
		projectsReadyToBePosted.add(getMappedProjectfromSource(source, project));
	}

	@Override
	public List<ActionResult> processProjectsInBatch(ActionStatus importStatus) {

		List<ActionResult> finalResults = new ArrayList<>();

		for (int fromIndex = 0; fromIndex < projectsReadyToBePosted.size(); fromIndex += Constants.AMP_PUSH_BATCH_SIZE) {
			int end = Math.min(projectsReadyToBePosted.size(), fromIndex + Constants.AMP_PUSH_BATCH_SIZE);
			importStatus.setProcessed(end);
			List<MappedProject> currentActivities = projectsReadyToBePosted.subList(fromIndex, end);

			List<CompletableFuture<ActionResult>> projectsToBePosted =
					currentActivities.stream()
							.map(project -> postProjects(project))
							.collect(Collectors.toList());

			List<ActionResult> result =
					projectsToBePosted.stream()
							.map(CompletableFuture::join)
							.collect(Collectors.toList());
			finalResults.addAll(result);
		}
		//once processed we need to remove what is being processed since the user can click on process again
		initializeProjectsLists();
		return finalResults;
	}


	private CompletableFuture<ActionResult> postProjects(MappedProject mappedProject) {
		CompletableFuture<ActionResult> future = CompletableFuture.supplyAsync(new Supplier<ActionResult>() {
			@Override
			public ActionResult get() {
				ActionResult result;

				String operation = Constants.AMP_INSERT_OPERATION;
				String url = Constants.AMP_ACTIVITY_ENDPOINT;
				if (mappedProject.getProject().get(Constants.AMP_INTERNAL_ID) != null) {
					operation = Constants.AMP_UPDATE_OPERATION;
					url += "/" + mappedProject.getProject().getString(Constants.AMP_INTERNAL_ID);
				}
				try {
					JsonBean resultPost = restTemplate.postForObject(baseURL + url, mappedProject.getProject(), JsonBean.class);

					Object errorNode = resultPost.get("error");

					if (errorNode == null) {
						Integer id = (int) resultPost.get("internal_id");
						String message = "";
						if (resultPost.get("project_title") instanceof Map) {
							@SuppressWarnings("unchecked")
							Map<String, String> titleMultilang = (Map<String, String>) resultPost.get("project_title");
							message = (String) titleMultilang.entrySet().stream().map(i -> i.getValue())
									.collect(Collectors.joining(", "));
						} else {
							message = resultPost.getString("project_title");
						}
						result = new ActionResult(id.toString(), operation, "OK", message,
								null, null);
					} else {
						String error = errorNode.toString();
						result = new ActionResult("N/A", "REJECT", "ERROR", "Error: " + error);
					}
				} catch (RestClientException e) {
					log.error("Error importing activity ", e);
					if (e instanceof HttpStatusCodeException) {
						HttpStatusCodeException ex = (HttpStatusCodeException) e;
						JsonBean resultPost = JsonBean.getJsonBeanFromString(ex.getResponseBodyAsString());
						if (resultPost != null) {
							Object errorNode = resultPost.get("error");
							Map<?, ?> activity = (Map<?, ?>) resultPost.get("activity");
							Object projectTitle = (activity != null && activity.get("project_title") != null)
									? activity.get("project_title")
									: "";
							result = new ActionResult("N/A", "ERROR", "ERROR",
									"REST Exception:" + projectTitle + " " + errorNode);
						} else {
							result = new ActionResult("N/A", "ERROR", "ERROR", "REST Exception:" + e.getMessage());
						}
					} else {
						result = new ActionResult("N/A", "ERROR", "ERROR", "REST Exception:" + e.getMessage());
					}
				} catch (Exception e) {
					log.error("Error importing activity " + e);
					result = new ActionResult("N/A", "ERROR", "ERROR", "Import failed with an error");
				}
				//we set the iati_identifier
				//we set the grouping criteira
				result.setSourceProjectIdentifier(mappedProject.getProjectIdentifier());
				result.setSourceGroupingCriteria(mappedProject.getGroupingCriteria());
				return result;
			}
		});

		return future;
	}

	private JsonBean transformProject(InternalDocument source, List<FieldMapping> fieldMappings,
			List<FieldValueMapping> valueMappings, ImportRequest importRequest) throws ValueMappingException, CurrencyNotFoundException {
		Boolean hasTransactions = false;
		JsonBean project = new JsonBean();
		project.set(ampIatiIdField, source.getIdentifier());
		project.set("project_title", getMultilangString(source, "project_title", "title"));

		// Check for project title length and trim them
		if (project.get("project_title") instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, String> titleMultilang = (Map<String, String>) project.get("project_title");
			Map<Object, Object> titleMultilangShort = titleMultilang.entrySet().stream().map(e -> new AbstractMap.SimpleEntry<>(e.getKey(),e.getValue().length() > 255 ? e.getValue().substring(0, 255) : e.getValue()))
			         .collect(Collectors.toMap(
			             Map.Entry::getKey,
			             Map.Entry::getValue
			         ));
			project.set("project_title", titleMultilangShort);
		} else {
			String projectTitle = (String) project.get("project_title");
			String projectTitleValue = projectTitle.length() > 255 ? projectTitle.substring(0, 255) : projectTitle;
			project.set("project_title", projectTitleValue);
		}
		
		for (FieldMapping mapping : fieldMappings) {
			Field sourceField = mapping.getSourceField();
			Field destinationField = mapping.getDestinationField();

			switch (sourceField.getType()) {
			case LOCATION:
				Optional<FieldValueMapping> optValueMappingLocation = valueMappings.stream().filter(n -> {
					return n.getSourceField().getFieldName().equals(mapping.getSourceField().getFieldName());
				}).findFirst();
				
				List<JsonBean> locations = getCodesFromList(source, optValueMappingLocation.get(), false);
				if (locations != null) {
					project.set(destinationField.getFieldName(), locations);
				}
				
				Properties props = getExtraInfo(source, optValueMappingLocation.get(), false);
				if (props != null) {
					@SuppressWarnings("unchecked")
					LinkedHashMap<String, Integer> hm = (LinkedHashMap<String, Integer>) props.get("extra_info");
					Integer implementationLocation = hm.get("implementation_level_id");
					project.set("implementation_location", implementationLocation);
					project.set("implementation_level", ampImplementationLevel);
				}
				break;
			case RECIPIENT_COUNTRY:
			case LIST:
				processListDestinationProjects(source, valueMappings, project, mapping, sourceField, destinationField);
				break;
			case ORGANIZATION:
				processOrganization(project, source, fieldMappings, valueMappings, mapping, sourceField, destinationField);
			    break;
			case MULTILANG_STRING:
				Object fieldValue = getMapFromString(source, destinationField.getFieldName(), mapping);
				if(!(fieldValue instanceof Map)) {
					String fieldValueString = (String)fieldValue;
					if (destinationField.getLength() != 0 && fieldValueString.length() > destinationField.getLength()) {
						fieldValue = fieldValueString.substring(0, destinationField.getLength());
					} else {
						fieldValue = fieldValueString;
					}				
				}

				project.set(destinationField.getFieldName(), fieldValue);
				break;
			case DATE:
				project.set(destinationField.getFieldName(), getFormattedDateFromString(source, mapping));
				break;
			case STRING:
				project.set(destinationField.getFieldName(), getString(source, mapping));
				break;
			case TRANSACTION:
				hasTransactions = true;
			default:
				break;
			}
		}

		// Process transactions
		if (hasTransactions) {
			List<JsonBean> fundings = getSourceFundings(source, fieldMappings, valueMappings, importRequest);
			if (fundings != null) {
				project.set("fundings", fundings);
				project.set("donor_organization", getDonorOrgs(fundings));
			}
		}

		return project;
	}

	private void processListDestinationProjects(InternalDocument source, List<FieldValueMapping> valueMappings,
												JsonBean project, FieldMapping mapping, Field sourceField,
												Field destinationField) throws ValueMappingException {
		if (!destinationField.getFieldName().equals("type_of_assistance")
				&& !destinationField.getFieldName().equals("financing_instrument")) {
			// TODO this needs to come from Field destinationObject as a property
			// TODO after the release please refactor this method in order to avoid asking here
			// TODO if we should add id for the prefix
			// TODO also column names should be moved to constants
			Boolean prefix = true;
			if (destinationField.getFieldName().equals("primary_sectors")
					|| destinationField.getFieldName().equals("secondary_sectors")
					|| destinationField.getFieldName().equals("tertiary_sectors")) {
				prefix = false;
			}
			Optional<FieldValueMapping> optValueMapping = valueMappings.stream().filter(n -> {
				return n.getSourceField().getFieldName().equals(mapping.getSourceField().getFieldName());
			}).findFirst();
			if (optValueMapping.isPresent() && sourceField.isMultiple()) {
				List<JsonBean> values = getCodesFromList(source, optValueMapping.get(), prefix);
				if (values != null) {
					project.set(destinationField.getFieldName(), values);
				}
			} else {
				Integer value = getCodeFromList(source, optValueMapping.get());
				if (value != null) {
					project.set(destinationField.getFieldName(), value);
				}
			}
		}
	}

	private Object getMultilangString(InternalDocument source, String destinationFieldName, String sourceFieldName) {
		Map<String, String> fieldValues = source.getMultilangFields().get(sourceFieldName);
		Field field = fieldList.stream().filter(n -> {
			return n.getFieldName().equals(destinationFieldName);
		}).findFirst().get();

		if (field.getType() == FieldType.MULTILANG_STRING) {
			return source.getMultilangFields().get(sourceFieldName);
		} else {
			if (fieldValues.size() > 0) {
				return fieldValues.values().iterator().next();
			} else {
				return "";
			}
		}
	}

	private List<JsonBean> getSourceFundings(InternalDocument source, List<FieldMapping> fieldMappings,
			List<FieldValueMapping> valueMappings, ImportRequest importRequest)  throws CurrencyNotFoundException,
			ValueMappingException{
		List<JsonBean> fundings = new ArrayList<>();
		String currencyCode = source.getStringFields().get("default-currency");
		String currencyIdString = getCurrencyId(currencyCode);
		if (currencyIdString == null) {
			throw new CurrencyNotFoundException("Currency code " + currencyCode + " could not be found in AMP");
		}

		int currencyId = Integer.parseInt(currencyIdString);
		Optional<FieldValue> optionalRecipientCountry = source.getRecepientCountryFields().get("recipient-country")
				.stream().findFirst();

		Double percentage = 100.00;
		if (optionalRecipientCountry.isPresent()) {
			FieldValue recipientCountry = optionalRecipientCountry.get();
			percentage = (StringUtils.isEmpty(recipientCountry.getPercentage())) ? 100.00
					: Double.parseDouble(recipientCountry.getPercentage());
		}

		Map<String, Map<String, String>> organizations = source.getOrganizationFields().entrySet().stream()
				.filter(p -> {
					return p.getValue().get("role").equals("Funding");
				}).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

		if(organizations.size() == 0) {
			throw new RuntimeException("Provider organization missing. Project ID: " + source.getIdentifier());
		}
		Entry<String, Map<String, String>> organization = organizations.entrySet().stream().findFirst().get();

		Map<String, List<JsonBean>> providerFundingDetails = new HashMap<>();
		for (FieldMapping mapping : fieldMappings) {
			Field sourceField = mapping.getSourceField();
			Field destinationField = mapping.getDestinationField();

			if (sourceField.getType() == FieldType.TRANSACTION) {

				String sourceSubType = sourceField.getSubType();
				Map<String, Map<String, String>> transactions = source.getTransactionFields().entrySet().stream()
						.filter(p -> {
							return p.getValue().get("subtype").equals(sourceSubType);
						}).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

				// Now we need the mapping of the field
				String destinationSubType = destinationField.getSubType();
				// this maps transactions to donor organizations using the
				// providing-org
				for (Entry<String, Map<String, String>> entry : transactions.entrySet()) {
					JsonBean fundingDetail = new JsonBean();
					Map<String, String> value = entry.getValue();
					String provider = value.get("providing-org");
					// if no provider tag on transaction use first funding org
					// in participating-orgs
					if (StringUtils.isEmpty(provider.trim())) {
						provider = organization.getValue().get("value");
					}

					List<JsonBean> fundingDetails = providerFundingDetails.get(provider);
					if (fundingDetails == null) {
						fundingDetails = new ArrayList<JsonBean>();
					}

					String amount = value.get("value");
					String dateString = value.get("date");
					fundingDetail.set("transaction_type", getTransactionType(destinationSubType)); 
					fundingDetail.set("adjustment_type", getAdjustmentType(destinationSubType));
					fundingDetail.set("transaction_date", getTransactionDate(dateString));
					fundingDetail.set("currency", currencyId);
					fundingDetail.set("transaction_amount", getTransactionAmount(amount, percentage));
					
					
					
					if (destinationFieldsList.contains("fundings~funding_details~disaster_response") && (Constants.DISBURSEMENTS.equals(sourceField.getDisplayName()) || Constants.COMMITMENTS.equals(sourceField.getDisplayName()) )) {
					   fundingDetail.set("disaster_response", importRequest.getDisasterResponse());
	                }
					
					fundingDetails.add(fundingDetail);
					providerFundingDetails.put(provider, fundingDetails);
				}
			}
		}

		// create fundings and add transactions to the fundings
		for (Entry<String, Map<String, String>> entry : organizations.entrySet()) {
			List<JsonBean> fundingDetails = providerFundingDetails.get(entry.getValue().get("value"));
			if (fundingDetails != null) {
				JsonBean funding = new JsonBean();
				Integer donorId = getIdFromList(entry.getValue().get("value"), "participating-org", fieldMappings,
						valueMappings, false);
				
				if (donorId != null) {
				   funding.set("donor_organization_id", donorId);
				}				

				try {
					String typeOfAssistance = source.getStringFields().get("default-finance-type");
					if (typeOfAssistance != null) {
						Integer typeOfAssistanceValue =  getIdFromList(typeOfAssistance, "default-finance-type",
								fieldMappings, valueMappings, true);
						if (typeOfAssistanceValue != null) {
							funding.set("type_of_assistance", typeOfAssistanceValue);
						}
						
					}
				} catch (ValueMappingException e) {
					log.debug("Dependent field not loaded: default-finance-type");
				}

				try {
					if (source.getStringFields().get("default-aid-type") != null) {
						String financingInstrument = source.getStringFields().get("default-aid-type");
						Integer financingInstrumentValue = getIdFromList(financingInstrument, "default-aid-type",
								fieldMappings, valueMappings, true);
						if (financingInstrumentValue != null) {
							funding.set("financing_instrument", financingInstrumentValue);
						}						
					}
				} catch (ValueMappingException e) {
					log.debug("Dependent field not loaded: default-aid-type");
				}

				if (destinationFieldsList.contains("fundings~source_role")) {
					funding.set("source_role", 1);
				}

				funding.set("funding_details", fundingDetails);
				fundings.add(funding);
			}
		}

		return fundings;
	}

	/**
	 * This is used when the import option is "ONLY_ADD_NEW_FUNDING". Updates the
	 * funding from AMP by adding missing transactions. Since IATI transactions do
	 * not have a unique identifier, we compare the fields to check if the
	 * transaction exists.
	 * 
	 * @param source
	 * @param fieldMappings
	 * @param valueMappings
	 * @param project
	 * @return
	 * @throws ValueMappingException
	 * @throws CurrencyNotFoundException
	 */
	private List<JsonBean> addNewFunding(InternalDocument source, List<FieldMapping> fieldMappings,
			List<FieldValueMapping> valueMappings, JsonBean project, ImportRequest importRequest)
			throws  CurrencyNotFoundException, ValueMappingException{
		List<JsonBean> sourceFundings = getSourceFundings(source, fieldMappings, valueMappings, importRequest);
		List<LinkedHashMap<String, Object>> destinationFundings = null;
		if (project.get("fundings") != null) {
			destinationFundings = (List<LinkedHashMap<String, Object>>) project.get("fundings");
		}

		List<JsonBean> updatedFundings = new ArrayList<>();
		for (JsonBean sourceFunding : sourceFundings) {
			if (destinationFundings != null && destinationFundings.size() > 0) {
				for (LinkedHashMap<String, Object> destFunding : destinationFundings) {
					// if funding exists in project from amp, update it by adding
					// missing transactions, else rebuild funding and add to list
					if (sourceFunding.get("donor_organization_id").equals(destFunding.get("donor_organization_id"))) {
						updatedFundings.add(getUpdatedFunding(sourceFunding, destFunding));
					} else {
						updatedFundings.add(mapToJsonBean(destFunding));
					}
				}
			} else {
				updatedFundings.add(sourceFunding);
			}
		}
		return updatedFundings;
	}

	private JsonBean mapToJsonBean(LinkedHashMap<String, Object> map) {
		JsonBean result = new JsonBean();
		for (Entry<String, Object> entry : map.entrySet()) {
			result.set(entry.getKey(), entry.getValue());
		}

		return result;
	}

	/**
	 * Create a new jsonbean object by modifying the funding from AMP. If
	 * transaction is not found, adds it to funding details
	 * 
	 * @param source
	 * @param destFunding
	 * @return
	 */
	private JsonBean getUpdatedFunding(JsonBean source, LinkedHashMap<String, Object> destFunding) {
		JsonBean rebuiltDestFunding = new JsonBean();
		for (Entry<String, Object> entry : destFunding.entrySet()) {
			if ("funding_details".equals(entry.getKey())) {
				List<JsonBean> fundingDetailsSource = (List<JsonBean>) source.get("funding_details");
				List<Map<String, Object>> fundingDetailsDestination = new ArrayList<>();
				if (entry.getValue() != null) {
					fundingDetailsDestination = (List<Map<String, Object>>) entry.getValue();
				}

				for (JsonBean sourceTransaction : fundingDetailsSource) {
					// if transaction is not found, add it to the funding
					// details retrieved from amp
					boolean transactionExists = transactionExistsInProject(fundingDetailsDestination,
							sourceTransaction);
					if (Boolean.FALSE.equals(transactionExists)) {
						fundingDetailsDestination.add(sourceTransaction.any());
					}
				}
				rebuiltDestFunding.set("funding_details", fundingDetailsDestination);
			} else {
				rebuiltDestFunding.set(entry.getKey(), entry.getValue());
			}

		}

		return rebuiltDestFunding;
	}

	/**
	 * checks if a transaction from the IATI file exists in the funding details from
	 * AMP. Since IATI transactions do not have a unique identifier, we compare the
	 * fields to check if the transaction exists.
	 * 
	 * @param fundingDetailsDestination
	 *            - funding details from AMP
	 * @param sourceTransaction
	 *            - transaction from IATI file
	 * @return
	 */
	private boolean transactionExistsInProject(List<Map<String, Object>> fundingDetailsDestination,
			JsonBean sourceTransaction) {
		return fundingDetailsDestination.stream().anyMatch(n -> {
			return n.get("transaction_type").equals(sourceTransaction.get("transaction_type"))
					&& n.get("adjustment_type").equals(sourceTransaction.get("adjustment_type"))
					&& n.get("transaction_date").equals(sourceTransaction.get("transaction_date"))
					&& n.get("currency").equals(sourceTransaction.get("currency"))
					&& n.get("transaction_amount").equals(sourceTransaction.get("transaction_amount"));
		});
	}

	/**
	 * This is called when the import option is "REPLACE_DONOR_FUNDING". It modifies
	 * the fundings from amp by replacing/overwriting the fundings for donors that
	 * have data in the IATI file. Funding for donors not in the IATI file are not
	 * affected.
	 * 
	 * @param source
	 * @param fieldMappings
	 * @param valueMappings
	 * @param project
	 * @return
	 * @throws ValueMappingException
	 * @throws CurrencyNotFoundException
	 */
	private List<JsonBean> replaceDonorTransactions(InternalDocument source, List<FieldMapping> fieldMappings,
			List<FieldValueMapping> valueMappings, JsonBean project, ImportRequest importRequest)
			throws  CurrencyNotFoundException, ValueMappingException {
		List<JsonBean> sourceFundings = getSourceFundings(source, fieldMappings, valueMappings, importRequest);
		List<LinkedHashMap<String, Object>> destinationFundings = new ArrayList<>();
		if (project.get("fundings") != null) {
			destinationFundings = (List<LinkedHashMap<String, Object>>) project.get("fundings");
		}

		List<JsonBean> updatedFundings = new ArrayList<>();
		for (JsonBean sourceFunding : sourceFundings) {
			if (destinationFundings != null && destinationFundings.size() > 0) {
				for (LinkedHashMap<String, Object> destFunding : destinationFundings) {
					// if same donor_organization_id is found in both source data
					// and amp project, replace the fundings in the amp project with
					// fundings from the IATI file
					if (sourceFunding.get("donor_organization_id").equals(destFunding.get("donor_organization_id"))) {
						updatedFundings.add(sourceFunding);
					} else {
						updatedFundings.add(mapToJsonBean(destFunding));
					}
				}
			} else {
				updatedFundings.add(sourceFunding);
			}
		}

		return updatedFundings;
	}

	private String getCurrencyId(String currencyCode) {
		Field currency = fieldList.stream().filter(n -> {
			return n.getFieldName().equals("currency_code");
		}).findFirst().get();

		Optional<FieldValue> foundCurrency = currency.getPossibleValues().stream().filter(n -> {
			return n.getValue().equalsIgnoreCase(currencyCode);
		}).findFirst();

		FieldValue currencyValue;
		if (foundCurrency.isPresent()) {
			currencyValue = foundCurrency.get();
			return currencyValue.getCode();
		}
		return null;

	}

	private Object getTransactionAmount(String amount, Double percentage) {
		Double amountValue = Double.parseDouble(amount);
		return (amountValue * percentage) / 100;
	}

	private Integer getIdFromList(String fieldValue, String sourceField, List<FieldMapping> fieldMappings,
			List<FieldValueMapping> valueMappings, Boolean useCode) throws ValueMappingException {
	    	    
		Optional<FieldValueMapping> optVm = valueMappings.stream().filter(n -> {
			return n.getSourceField().getFieldName().equals(sourceField);
		}).findFirst();

		return getIdByValue(fieldValue, optVm, sourceField, useCode);
	}
	
	private Integer getOrgIdFromList(String fieldValue, String sourceField, List<FieldMapping> fieldMappings,
            List<FieldValueMapping> valueMappings, Boolean useCode, String fieldDisplayName) throws ValueMappingException {
                
        Optional<FieldValueMapping> optVm = valueMappings.stream().filter(n -> {
            return n.getSourceField().getFieldName().equals(sourceField) && n.getSourceField().getDisplayName().equals(fieldDisplayName);
        }).findFirst();

        return getIdByValue(fieldValue, optVm, sourceField, useCode);
    }

	private Integer getIdByValue(String fieldValue, Optional<FieldValueMapping> optVm, String sourceField, Boolean useCode) throws ValueMappingException {
	    if ((!optVm.isPresent()) || (optVm.get().getSourceField().getPossibleValues() == null)) {
            throw new ValueMappingException("The mapping for " + sourceField + " is invalid. No source values were found." );                       
        }        
        
        FieldValueMapping vm = optVm.get();
        FieldValue fvs = vm.getSourceField().getPossibleValues().stream().filter(n -> {
            if (useCode) {
                return n.getCode().equals(fieldValue);
            } else {
                return n.getValue().equals(fieldValue);
            }
        }).findFirst().get();
        Integer sourceValueIndex = fvs.getIndex();
        Integer destinationValueIndex = vm.getValueIndexMapping().get(sourceValueIndex);
        FieldValue fvd = vm.getDestinationField().getPossibleValues().stream().filter(n -> {
            return destinationValueIndex != null && n.getIndex() == destinationValueIndex;
        }).findFirst().get();
        return Integer.parseInt(fvd.getCode());
	}
	
	private String getTransactionDate(String dateString) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date inputDate;
		try {
			inputDate = dateFormat.parse(dateString);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			String isoDate = df.format(inputDate);
			return isoDate;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	private boolean isTransactionTypeEnabled(String transactionTypeValue) {
		return getTransactionTypePossibleValue(transactionTypeValue) != null;
	}
	private FieldValue getTransactionTypePossibleValue(String transactionTypeValue) {
		Field transactionType = this.getFields().stream().filter(n ->
				n.getFieldName().equals("transaction_type")).findFirst().get();

		Optional<FieldValue> transactionFieldValue =
				transactionType.getPossibleValues().stream().filter(n ->
						n.getValue().equals(tTNameSourceMap.get(transactionTypeValue))).findFirst();

		if (transactionFieldValue.isPresent()) {
			return transactionFieldValue.get();
		} else {
			return null;
		}
	}
	private int getTransactionType(String transactionTypeValue) {

		FieldValue transactionTypeFieldValue =getTransactionTypePossibleValue(transactionTypeValue);
		if(transactionTypeFieldValue == null){
			throw new ValueNotEnabledException("The mapped value is not present in the destination system");
		}

		return Integer.parseInt(transactionTypeFieldValue.getCode());

	}

	private int getAdjustmentType(String value) {
		Field adjustmentType = this.getFields().stream().filter(n -> {
			return n.getFieldName().equals("adjustment_type");
		}).findFirst().get();

		String adjustmentTypeValue = adjustmentType.getPossibleValues().stream().filter(n -> {
			return n.getValue().equals(aTNameDestinationMap.get(value));
		}).findFirst().get().getCode();
		return Integer.parseInt(adjustmentTypeValue);
	}

	private String getString(InternalDocument source, FieldMapping mapping) {
		return source.getStringFields().get(mapping.getSourceField().getFieldName());
	}

	private String getFormattedDateFromString(InternalDocument source, FieldMapping mapping) {
		String uniqueFieldName = mapping.getSourceField().getUniqueFieldName();
		Date date = source.getDateFields().get(uniqueFieldName);
		if (date == null)
			return null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String nowAsISO = df.format(date);
		return nowAsISO;
	}

	private Object getMapFromString(InternalDocument source, String destinationFieldName, FieldMapping mapping) {
		Object langString = getMultilangString(source, destinationFieldName, mapping.getSourceField().getFieldName());
		return langString;
	}

	private List<JsonBean> getCodesFromList(InternalDocument source, FieldValueMapping mapping) throws ValueMappingException {
		return getCodesFromList(source, mapping, true);
	}

	private Properties getExtraInfo(InternalDocument source, FieldValueMapping mapping, Boolean suffix) {
		Object value = source.getStringMultiFields().get(mapping.getSourceField().getFieldName());
		Map<Integer, Integer> valueMapIndex = mapping.getValueIndexMapping();
		List<FieldValue> sourcePossibleValues = mapping.getSourceField().getPossibleValues();
		String[] stringValues = (String[]) value;

		if (stringValues != null) {
		    for (int i = 0; i < stringValues.length; i++) {
	            final int stringValueIndex = i;
	            Optional<FieldValue> optSourceValueIndex = sourcePossibleValues.stream().filter(n -> {
	                return n.getCode().equals(stringValues[stringValueIndex]);
	            }).findAny();
	            Integer sourceValueIndex = optSourceValueIndex.get().getIndex();
	            Integer destinationValueIndex = valueMapIndex.get(sourceValueIndex);
	            List<FieldValue> destinationPossibleValues = mapping.getDestinationField().getPossibleValues();
	            FieldValue destinationValue = destinationPossibleValues.get(destinationValueIndex);
	            return destinationValue.getProperties();
	        } 
		}
		
		return null;
	}

	private List<JsonBean> getCodesFromList(InternalDocument source, FieldValueMapping mapping, Boolean suffix) throws ValueMappingException {
		Object value = source.getStringMultiFields().get(mapping.getSourceField().getFieldName());
		Map<Integer, Integer> valueMapIndex = mapping.getValueIndexMapping();
		List<FieldValue> sourcePossibleValues = mapping.getSourceField().getPossibleValues();
		String[] stringValues = (String[]) value;
		HashMap<Integer, Integer> uniqueValues = new HashMap<Integer, Integer>();

		if (stringValues == null) {
			if (mapping.getDestinationField().isRequired()) {
				throw new ValueMappingException("The mapping for " + mapping.getSourceField().getDisplayName() + " is invalid. No source values were found." );
			} else {
				return null;
			}			
		}
		
		for (int i = 0; i < stringValues.length; i++) {
			final int stringValueIndex = i;
			Optional<FieldValue> optSourceValueIndex = sourcePossibleValues.stream().filter(n -> {
				return n.getCode().equals(stringValues[stringValueIndex]);
			}).findAny();
			Integer sourceValueIndex = optSourceValueIndex.get().getIndex();
			Integer destinationValueIndex = valueMapIndex.get(sourceValueIndex);
			List<FieldValue> destinationPossibleValues = mapping.getDestinationField().getPossibleValues();
			FieldValue destinationValue = destinationPossibleValues.get(destinationValueIndex);
			Integer intValue = Integer.parseInt(destinationValue.getCode());
			Integer count = uniqueValues.get(intValue);
			if (count == null) {
				uniqueValues.put(intValue, 1);
			} else {
				uniqueValues.put(intValue, count + 1);
			}
		}

		List<JsonBean> beanList = new ArrayList<JsonBean>();
		Integer divider = uniqueValues.size();
		for (Entry<Integer, Integer> entry : uniqueValues.entrySet()) {
			JsonBean bean = new JsonBean();
			if (suffix) {
				bean.set(mapping.getSourceField().getFieldName() + "_id", entry.getKey());
			} else {
				bean.set(mapping.getSourceField().getFieldName(), entry.getKey());
			}
			if (mapping.getSourceField().isPercentage() && entry.getValue() > 0) {
				bean.set(mapping.getSourceField().getFieldName() + "_percentage", (double) 100 / (double) divider);
			}
			beanList.add(bean);
		}
		return beanList;
	}

	private Integer getCodeFromList(InternalDocument source, FieldValueMapping mapping) throws ValueMappingException {
		Object value = source.getStringFields().get(mapping.getSourceField().getFieldName());
		Map<Integer, Integer> valueMapIndex = mapping.getValueIndexMapping();
		List<FieldValue> sourcePossibleValues = mapping.getSourceField().getPossibleValues();
		String stringValue = (String) value;
		Optional<FieldValue> optSourceValueIndex = sourcePossibleValues.stream().filter(n -> {
			return n.getCode().equals(stringValue);
		}).findAny();
		
		if (!optSourceValueIndex.isPresent()) {
			if (mapping.getDestinationField().isRequired()) {
				throw new ValueMappingException("The mapping for " + mapping.getSourceField().getDisplayName() + " is invalid. No source values were found." );
			} else {
				return null;
			}			
		}
		
		Integer sourceValueIndex = optSourceValueIndex.get().getIndex();
		Integer destinationValueIndex = valueMapIndex.get(sourceValueIndex);
		List<FieldValue> destinationPossibleValues = mapping.getDestinationField().getPossibleValues();
		FieldValue destinationValue = destinationPossibleValues.get(destinationValueIndex);
		Integer intValue = Integer.parseInt(destinationValue.getCode());
		return intValue;
	}

	private void instantiateStaticFields() {

		Map<String, Properties> fieldProps = getFieldProps();
		// Transaction Fields
		List<Field> trnDependencies = new ArrayList<Field>();
		loadCodeListValues();
		loadAmpTranslations();
		// Fixed fields
		fieldList.add(new Field("IATI Identifier", "iati-identifier", FieldType.STRING, false));
		if (destinationFieldsList.contains("project_title")) {
			Field projectTitle = new Field("Project Title", "project_title",
					getFieldType(fieldProps.get("project_title")), false);
			projectTitle.setAttributes(destinationFieldsListLabels.get("project_title"));
			projectTitle.setMultiLangDisplayName(destinationFieldsListLabels.get("project_title"));
			fieldList.add(projectTitle);
		}

		// Code Lists
		if (destinationFieldsList.contains("activity_status")) {
			Field activityStatus = new Field("Activity Status", "activity_status", FieldType.LIST, true);
			activityStatus.setPossibleValues(getCodeListValues("activity_status"));
			activityStatus.setRequired(true);
			activityStatus.setMultiLangDisplayName(destinationFieldsListLabels.get("activity_status"));
			fieldList.add(activityStatus);
		}

		if (destinationFieldsList.contains("A C Chapter")) {
			Field acChapter = new Field("AC Chapter", "A C Chapter", FieldType.LIST, true);
			acChapter.setPossibleValues(getCodeListValues("A C Chapter"));
			acChapter.setRequired(false);
			acChapter.setMultiLangDisplayName(destinationFieldsListLabels.get("A C Chapter"));
			fieldList.add(acChapter);
		}

		if (destinationFieldsList.contains("fundings~type_of_assistance")) {
			Field typeOfAssistence = new Field("Type of Assistance", "type_of_assistance", FieldType.LIST, true);
			typeOfAssistence.setPossibleValues(getCodeListValues("fundings~type_of_assistance"));
			typeOfAssistence.setMultiLangDisplayName(destinationFieldsListLabels.get("fundings~type_of_assistance"));
			fieldList.add(typeOfAssistence);
			trnDependencies.add(typeOfAssistence);
		}

		if (destinationFieldsList.contains("fundings~financing_instrument")) {
			Field financialInstrument = new Field("Aid Modality", "financing_instrument", FieldType.LIST, true);
			financialInstrument.setPossibleValues(getCodeListValues("fundings~financing_instrument"));
			financialInstrument
					.setMultiLangDisplayName(destinationFieldsListLabels.get("fundings~financing_instrument"));
			fieldList.add(financialInstrument);
			trnDependencies.add(financialInstrument);
		}

		if (destinationFieldsList.contains("fundings~funding_details~adjustment_type")) {
			Field adjustmentType = new Field("Adjustment Type", "adjustment_type", FieldType.LIST, true);
			adjustmentType.setPossibleValues(getCodeListValues("fundings~funding_details~adjustment_type"));
			adjustmentType.setMultiLangDisplayName(
					destinationFieldsListLabels.get("fundings~funding_details~adjustment_type"));
			fieldList.add(adjustmentType);
		}

		if (destinationFieldsList.contains("fundings~funding_details~transaction_type")) {
			Field transactionType = new Field("Transaction Type", "transaction_type", FieldType.LIST, false);
		transactionType.setPossibleValues(getCodeListValues("fundings~funding_details~transaction_type"));
			transactionType.setMultiLangDisplayName(
					destinationFieldsListLabels.get("fundings~funding_details~transaction_type"));
			fieldList.add(transactionType);
		}

		if (destinationFieldsList.contains("primary_sectors~sector")) {
			Field primarySector = new Field("Primary Sector", "primary_sectors", FieldType.LIST, true);
			primarySector.setPossibleValues(getCodeListValues("primary_sectors~sector"));
			primarySector.setMultiple(true);
			primarySector.setMultiLangDisplayName(destinationFieldsListLabels.get("primary_sectors"));
			fieldList.add(primarySector);
		}

		if (destinationFieldsList.contains("secondary_sectors~sector")) {
			Field secondarySector = new Field("Secondary Sector", "secondary_sectors", FieldType.LIST, true);
			secondarySector.setPossibleValues(getCodeListValues("secondary_sectors~sector"));
			secondarySector.setMultiLangDisplayName(destinationFieldsListLabels.get("secondary_sectors"));
			fieldList.add(secondarySector);
		}

		if (destinationFieldsList.contains("tertiary_sectors~sector")) {
			Field tertiarySector = new Field("Tertiary Sector", "tertiary_sectors", FieldType.LIST, true);
			tertiarySector.setPossibleValues(getCodeListValues("tertiary_sectors~sector"));
			tertiarySector.setMultiLangDisplayName(destinationFieldsListLabels.get("tertiary_sectors"));
			fieldList.add(tertiarySector);
		}

		// locations, locations~location, locations~location_percentage
		if (destinationFieldsList.contains("locations~location")) {
			Field location = new Field("Location", "locations", FieldType.LOCATION, true);
			location.setPossibleValues(getCodeListValues("locations~location"));
			location.setMultiLangDisplayName(destinationFieldsListLabels.get("locations~location"));
			location.setMultiple(true);
			fieldList.add(location);
		}

		// Multi-language strings
		Map<String, String> multilangfields = new HashMap<String, String>();
		multilangfields.put("project_comments", "Project Comments");
		multilangfields.put("objective", "Objective");
		multilangfields.put("document_space", "Document Space");
		multilangfields.put("status_reason", "Status Reason");
		multilangfields.put("project_impact", "Project Impact");
		multilangfields.put("activity_summary", "Activity Summary");
		multilangfields.put("conditionalities", "Conditionalities");
		multilangfields.put("project_management", "Project Management");
		multilangfields.put("project_comments", "Project Comments");
		multilangfields.put("lessons_learned", "Lessons Learned");
		multilangfields.put("results", "Results");
		multilangfields.put("description", "Description");

		multilangfields.forEach((name, label) -> {
			if (destinationFieldsList.contains(name)) {
				FieldType ftDescription = getFieldType(fieldProps.get(name));
				if (ftDescription != null) {
					Field newField = new Field(label, name, ftDescription, true);
					int fieldLength = getFieldLength(fieldProps.get(name));
					newField.setLength(fieldLength);
					newField.setMultiLangDisplayName(destinationFieldsListLabels.get(name));
					fieldList.add(newField);
				}
			}
		});

		// Dates
		Map<String, String> dateFields = new HashMap<String, String>();
		dateFields.put("actual_start_date", "Actual Start Date");
		dateFields.put("actual_completion_date", "Actual Completion Date");
		dateFields.put("actual_approval_date", "Actual Approval Date");
		dateFields.put("proposed_start_date", "Proposed Start Date");
		dateFields.put("proposed_approval_date", "Proposed Approval Date");
		dateFields.put("proposed_completion_date", "Proposed Completion Date");
		dateFields.put("planned_start_date", "Planned Start Date");
		dateFields.put("original_completion_date", "Original Completion Date");

		Field actualStartDate = new Field("", "actual_start_date", FieldType.DATE, true);
		dateFields.forEach((name, label) -> {
			if (fieldProps.get(name) != null) {
				FieldType ftDescription = getFieldType(fieldProps.get(name));
				if (ftDescription != null) {
					Field newField = new Field(label, name, FieldType.DATE, true);
					newField.setMultiLangDisplayName(destinationFieldsListLabels.get(name));
					fieldList.add(newField);
				}
			}
		});

		// Organizations

		Map<String, String> organizationsRoles = new HashMap<String, String>();

		organizationsRoles.put("responsible_organization", "Responsible Organization");
		organizationsRoles.put("executing_agency", "Executing Agency");
		organizationsRoles.put("implementing_agency", "Implementing Agency");
		organizationsRoles.put("beneficiary_agency", "Beneficiary Agency");
		organizationsRoles.put("contracting_agency", "Contracting Agency");
		organizationsRoles.put("regional_group", "Regional Group");
		organizationsRoles.put("sector_group", "Sector Group");

		organizationsRoles.forEach((name, label) -> {
			if (destinationFieldsList.contains(name)) {
				Field org = new Field(label, name, FieldType.ORGANIZATION, true);
				org.setMultiLangDisplayName(destinationFieldsListLabels.get(name));
				org.setPossibleValues(getCodeListValues("fundings~donor_organization_id"));
				if (fieldProps.get(name) != null && fieldProps.get(name).getProperty("percentage_constraint") != null) {
					org.setPercentage(true);
				}
				fieldList.add(org);
			}
		});

		if (destinationFieldsList.contains("fundings~donor_organization_id")) {
			Field fundingOrganization = new Field("Funding Organization", "donor_organization", FieldType.ORGANIZATION,
					true);
			fundingOrganization.setPossibleValues(getCodeListValues("fundings~donor_organization_id"));
			fundingOrganization
					.setMultiLangDisplayName(destinationFieldsListLabels.get("fundings~donor_organization_id"));
			if (fieldProps.get("donor_organization") != null
					&& fieldProps.get("donor_organization").getProperty("percentage_constraint") != null) {
				fundingOrganization.setPercentage(true);
			}
			fieldList.add(fundingOrganization);
			trnDependencies.add(fundingOrganization);
		}

		// Transactions
		//we need to find a more generic way to only enable what is enabled in amp
		//we should refactor this clases to be able to check all fields
		if(this.isTransactionTypeEnabled(Constants.TRANSACTION_TYPE_ACTUAL_COMMITMENTS)) {
			Field actualCommitments = new Field("Actual Commitments", "transaction", FieldType.TRANSACTION, true);
			actualCommitments.setSubType(Constants.TRANSACTION_TYPE_ACTUAL_COMMITMENTS);
			actualCommitments.setDependencies(trnDependencies);
			actualCommitments.setMultiLangDisplayName(getAmpTranslations().get(Constants.ACTUAL + " "+ Constants.COMMITMENTS ));
			fieldList.add(actualCommitments);
		}
		if(this.isTransactionTypeEnabled(Constants.TRANSACTION_TYPE_PLANNED_COMMITMENTS)) {
			Field plannedCommitments = new Field("Planned Commitments", "transaction", FieldType.TRANSACTION, true);
			plannedCommitments.setSubType(Constants.TRANSACTION_TYPE_PLANNED_COMMITMENTS);
			plannedCommitments.setDependencies(trnDependencies);
			plannedCommitments.setMultiLangDisplayName(getAmpTranslations().get(Constants.PLANNED + " "+ Constants.COMMITMENTS ));
			fieldList.add(plannedCommitments);
		}
		if(this.isTransactionTypeEnabled(Constants.TRANSACTION_TYPE_ACTUAL_DISBURSEMENTS)) {
			Field actualDisbursements = new Field("Actual Disbursements", "transaction", FieldType.TRANSACTION, true);
			actualDisbursements.setSubType(Constants.TRANSACTION_TYPE_ACTUAL_DISBURSEMENTS);
			actualDisbursements.setDependencies(trnDependencies);
			actualDisbursements.setMultiLangDisplayName(getAmpTranslations().get(Constants.ACTUAL + " "+ Constants.DISBURSEMENTS ));
			fieldList.add(actualDisbursements);
		}
		if(this.isTransactionTypeEnabled(Constants.TRANSACTION_TYPE_PLANNED_DISBURSEMENTS)) {
			Field plannedDisbursements = new Field("Planned Disbursements", "transaction", FieldType.TRANSACTION, true);
			plannedDisbursements.setSubType(Constants.TRANSACTION_TYPE_PLANNED_DISBURSEMENTS);
			plannedDisbursements.setDependencies(trnDependencies);
			plannedDisbursements.setMultiLangDisplayName(getAmpTranslations().get(Constants.PLANNED + " " + Constants.DISBURSEMENTS ));

			fieldList.add(plannedDisbursements);
		}
		if(this.isTransactionTypeEnabled(Constants.TRANSACTION_TYPE_ACTUAL_EXPENDITURES)) {
			Field actualExpenditure = new Field(Constants.ACTUAL_EXPENDITURES, "transaction", FieldType.TRANSACTION,
					true);
			actualExpenditure.setSubType(Constants.TRANSACTION_TYPE_ACTUAL_EXPENDITURES);
			actualExpenditure.setDependencies(trnDependencies);
			actualExpenditure.setMultiLangDisplayName(getAmpTranslations().get(Constants.ACTUAL + " " + Constants.EXPENDITURES ));
			fieldList.add(actualExpenditure);
		}
		if(this.isTransactionTypeEnabled(Constants.TRANSACTION_TYPE_PLANNED_EXPENDITURES)) {
			Field plannedExpenditures = new Field(Constants.PLANNED_EXPENDITURES, "transaction", FieldType.TRANSACTION, true);
			plannedExpenditures.setSubType(Constants.TRANSACTION_TYPE_PLANNED_EXPENDITURES);
			plannedExpenditures.setDependencies(trnDependencies);
			plannedExpenditures.setMultiLangDisplayName(getAmpTranslations().get(Constants.PLANNED + " " + Constants.EXPENDITURES ));
			fieldList.add(plannedExpenditures);
		}
		// Currency
		Field currency = new Field("Currency Code", "currency_code", FieldType.LIST, true);
		currency.setMultiLangDisplayName(destinationFieldsListLabels.get("fundings~funding_details~currency"));
		currency.setPossibleValues(getCodeListValues("fundings~funding_details~currency"));
		fieldList.add(currency);
		
		 if (destinationFieldsList.contains("fundings~funding_details~disaster_response")) {
		     Field disasterResponse = new Field("Disaster Response", "disaster_response", FieldType.BOOLEAN, false);
		     fieldList.add(disasterResponse);
          }

	}

	private boolean existFieldInAmp(String fieldName) {
		return destinationFieldsList.stream().anyMatch(field-> field.equals(fieldName));
	}

	private int getFieldLength(Properties properties) {
		if (properties == null || properties.get("length") == null) {
			return 0;
		}
		String value = (String) properties.get("length");
		int fieldLength = Integer.parseInt(value);
		return fieldLength;
	}

	private FieldType getFieldType(Properties properties) {
		if (properties == null || properties.get("field_type") == null) {
			return null;
		}
		FieldType ft = FieldType.STRING;
		String fieldType = (String) properties.get("field_type");
		switch (fieldType) {
		case "list":
			ft = FieldType.LIST;
			break;
		case "string":
			if (Boolean.parseBoolean((String) properties.get("translatable"))) {
				ft = FieldType.MULTILANG_STRING;
			} else {
				ft = FieldType.STRING;
			}
			break;
		case "date":
			ft = FieldType.DATE;
			break;
		}
		return ft;
	}

	private Map<String, Properties> getFieldProps() {
		// Map<String, FieldType> map = new HashMap<String, FieldType>();
		Map<String, Properties> fieldProperties = new HashMap<String, Properties>();

		String result = "";
		RestTemplate restTemplate = getRestTemplate();
		result = restTemplate.getForObject(baseURL + FIELDS_ENDPOINT, String.class);
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(result);

			if (jsonNode.isArray()) {
				Iterator<JsonNode> mainNode = jsonNode.elements();
				while (mainNode.hasNext()) {
					JsonNode node = mainNode.next();
					String fieldName = node.get("field_name").asText();
					String fieldType = node.get("field_type").asText();
					
					Map<String, String> fieldLabel = extractMultilanguageText(node.get("field_label"));
					destinationFieldsList.add(fieldName);
					destinationFieldsListLabels.put(fieldName, fieldLabel);
					addChildrenToDestinationFieldList(node, fieldName);

					JsonNode translatable = node.get("translatable");
					JsonNode percentageConstraint = node.get("percentage_constraint");
					JsonNode length = node.get("field_length");

					Properties prop = new Properties();
					prop.setProperty("field_type", fieldType);
					if(length != null) {
						prop.setProperty("length", length.asText());
					}
					if (translatable != null) {
						prop.setProperty("translatable", translatable.asText());
					}
					if (percentageConstraint != null) {
						prop.setProperty("percentage_constraint", percentageConstraint.asText());
					}
					fieldProperties.put(fieldName, prop);

				}
			}

		} catch (Exception e) {
			log.error("Couldn't retrieve field properties from endpoint. Exception: " + e.getMessage());
		}
		return fieldProperties;
	}

	private void addChildrenToDestinationFieldList(JsonNode node, String fieldName) {
		JsonNode children = node.get("children");
		if (children != null && children.isArray()) {
			Iterator<JsonNode> childFields = children.elements();
			while (childFields.hasNext()) {
				JsonNode child = childFields.next();
				String childFieldName = child.get("field_name").asText();
				Map<String, String> childFieldLabel = extractMultilanguageText(child.get("field_label"));
				String name = fieldName + "~" + childFieldName;
				destinationFieldsList.add(name);
				destinationFieldsListLabels.put(name, childFieldLabel);
				if (node.get("children") != null) {
					addChildrenToDestinationFieldList(child, name);
				}
			}
		}

	}
	private void loadAmpTranslations() {

		String result = restTemplate.postForObject(baseURL + TRANSLATIONS_END_POINT
						+ extractSupportedLocales(), Constants.TRANSACTION_FIELDS, String.class);
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(result);
			Iterator<String> keys = jsonNode.fieldNames();
			while(keys.hasNext()){
				String keyName = keys.next();
				JsonNode jn = jsonNode.get(keyName);
				Map<String,String> translationperField = new HashMap<>();
				Iterator <Entry<String,JsonNode>> iter = jn.fields();
				while(iter.hasNext()){
					Entry<String,JsonNode>lang = iter.next();
					translationperField.put(lang.getKey(),lang.getValue().asText());
				}
				ampTranslations.put(keyName, translationperField);
			}

		}catch(IOException ex){
			// Its safe to Ignore, if translations can not be loaded we use default values(english)
			log.error("cannot load transaltions from AMP", ex);
		}
	}

	private String extractSupportedLocales(){
		return String.join("|",Constants.SUPPORTED_LOCALES);
	}

	private void loadCodeListValues() {
		// TODO this needs to be extracted to a static variable and abscract field configuration
		// TODO but proper analysis is needed and its out of the scope of this ticket
		// TODO also we need to move amp column names to constant and try to abstract configuration
		List<String> allDestinationFieldValues = new ArrayList<>();
		allDestinationFieldValues.add("activity_status");
		allDestinationFieldValues.add("A C Chapter");
		allDestinationFieldValues.add("fundings~type_of_assistance");
		allDestinationFieldValues.add("fundings~financing_instrument");
		allDestinationFieldValues.add("fundings~funding_details~adjustment_type");
		allDestinationFieldValues.add("fundings~funding_details~transaction_type");
		allDestinationFieldValues.add("primary_sectors~sector");
		allDestinationFieldValues.add("secondary_sectors~sector");
		allDestinationFieldValues.add("tertiary_sectors~sector");
		allDestinationFieldValues.add("locations~location");
		allDestinationFieldValues.add("fundings~donor_organization_id");
		allDestinationFieldValues.add("fundings~donor_organization_id");
		allDestinationFieldValues.add("fundings~funding_details~currency");
		allDestinationFieldValues.retainAll(destinationFieldsList);

		allFieldValuesForDestinationProcessor = new HashMap<>();

		String result = restTemplate.postForObject(baseURL + ALL_FIELDS_ENDPOINT, allDestinationFieldValues,
				String.class);

		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode jsonNode = mapper.readTree(result);
			Iterator<String> keys = jsonNode.fieldNames();
			while (keys.hasNext()) {
				String keyName = keys.next();
				JsonNode jn = jsonNode.get(keyName);
				allFieldValuesForDestinationProcessor.put(keyName, getPossibleValuesFromNode(jn.iterator()));
			}
		} catch (IOException e) {
			//
			log.error("cannot retrieve possible values");
		}
	}

	private List<FieldValue> getCodeListValues(String codeListName) {
		// TODO this if is defensive since we are close to release, if its not found in the map, then we go and fetch
		// TODO individually. After the release is safe to remove the else and only call from the map
		List<FieldValue> possibleValues = allFieldValuesForDestinationProcessor.get(codeListName);
			if(possibleValues == null) {
			String result;
			possibleValues = new ArrayList<>();

			result = restTemplate.getForObject(baseURL + FIELDS_ENDPOINT + "/" + codeListName, String.class);

			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode jsonNode = mapper.readTree(result);
				if (jsonNode.isArray()) {
					Iterator<JsonNode> mainNode = jsonNode.elements();
					possibleValues.addAll(getPossibleValuesFromNode(mainNode));
				}

			} catch (Exception e) {
				log.error("Couldn't retrieve values from Endpoint. Exception: " + e.getMessage() + ", URL:" + codeListName);
			}
		}
		return possibleValues;
	}

	private List<FieldValue> getPossibleValuesFromNode(Iterator<JsonNode> mainNode) {
		List<FieldValue> possibleValues = new ArrayList<FieldValue>();
		int index = 0;
		while (mainNode.hasNext()) {
			JsonNode node = mainNode.next();
			String code = node.get("id").asText();
			String value = node.get("value").asText();
			FieldValue fv = new FieldValue();
			fv.setIndex(index++);
			fv.setCode(code);
			fv.setValue(value);
			//TODO THIS IS A WORKAROUND TO BE FIXED BEFORE THE RELEASE
			/*if (node.get("extra_info") != null) {
				fv.getProperties().put("extra_info", node.get("extra_info"));			
			}*/
			
			if (node.get("translated-value") != null) {			        
			    Iterator <Entry<String,JsonNode>> iter = node.get("translated-value").fields();			    
                while(iter.hasNext()){
                    Entry<String,JsonNode>lang = iter.next();                   
                    fv.getTranslatedValue().put(lang.getKey(),lang.getValue().asText());
                }
            }
			
			
			possibleValues.add(fv);
		}
		return possibleValues;
	}

	@Override
	public List<DocumentMapping> preImportProcessing(List<DocumentMapping> documentMappings) {
		Map<String, Integer> titleCount = new HashMap<>();
		for (DocumentMapping doc : documentMappings) {
			if (doc.getSelected()) {
				InternalDocument source = doc.getSourceDocument();
				modifyDuplicateProjectTitles(source, titleCount);
			}
		}
		return documentMappings;
	}

	@SuppressWarnings("unchecked")
	private void modifyDuplicateProjectTitles(InternalDocument source, Map<String, Integer> titleCount) {
		Map<String, String> titles = source.getMultilangFields().get("title");
		Iterator<Entry<String, String>> it = titles.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry item = (Map.Entry) it.next();
			String title = (String) item.getValue();
			Integer count = (titleCount.get(title) != null) ? titleCount.get(title) : 0;
			titleCount.put(title, ++count);
			if (!title.startsWith(source.getIdentifier()) && count > 1) {
				item.setValue(source.getIdentifier() + " " + title);
			}
		}
	}

}
