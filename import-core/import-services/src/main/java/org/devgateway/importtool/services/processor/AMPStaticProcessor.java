package org.devgateway.importtool.services.processor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.devgateway.importtool.services.processor.helper.ValueMappingException;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// TODO: Sort methods, move classes to generic helpers for all processors if possible
// TODO: Clean up code, find opportunities to reuse methods (example update/insert)
// TODO: Add default values when mappings are missing, reading them from configuration db or files
// TODO: Better error handling to the end user. Friendlier user messages, specially when referencing a missing dependency

public class AMPStaticProcessor implements IDestinationProcessor {
	private String descriptiveName = "AMP 2.11";

	private final String DEFAULT_BASEURL = "http://localhost:8081";
	private Log log = LogFactory.getLog(getClass());

	// AMP Configuration Details
	private String DEFAULT_ID_FIELD = "amp-identifier";
	private String DEFAULT_TITLE_FIELD = "project_title";
	private String baseURL;
	private String fieldsEndpoint = "/rest/activity/fields";
	private String documentsEndpoint = "/rest/activity/projects";
	// private Properties properties = null;

	private List<Field> fieldList = new ArrayList<Field>();

	private List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
	private RestTemplate template;

	public AMPStaticProcessor(String authenticationToken) {
		// this.properties = initProps();
		this.setAuthenticationToken(authenticationToken);
		this.baseURL = System.getProperty("AMPStaticProcessor.baseURL");
		if (this.baseURL == null) {
			this.baseURL = DEFAULT_BASEURL;
		}
		instantiateStaticFields();
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
			RestTemplate restTemplate = getRestTemplate();
			result = restTemplate.getForObject(baseURL + this.getDocumentsEndpoint(), String.class);
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

	@Override
	public void setAuthenticationToken(String authToken) {
		this.interceptors.add(new TokenHeaderInterceptor(authToken));
	}

	public String getDocumentsEndpoint() {
		return documentsEndpoint;
	}

	public void setDocumentsEndpoint(String documentsEndpoint) {
		this.documentsEndpoint = documentsEndpoint;
	}

	@Override
	public ActionResult update(InternalDocument source, InternalDocument destination, List<FieldMapping> fieldMapping, List<FieldValueMapping> valueMapping) {
		ActionResult result;

		RestTemplate restTemplate = getRestTemplate();
		try {
			JsonBean project = getProject(destination.getStringFields().get("id"));
			updateProject(project, source, fieldMapping, valueMapping);
			log.info(project);
			JsonBean resultPost = restTemplate.postForObject(baseURL + "/rest/activity/" + destination.getStringFields().get("id"), project, JsonBean.class);
			Object errorNode = resultPost.get("error");

			if (errorNode == null) {
				Integer id = (int) resultPost.get("internal_id");
				result = new ActionResult(id.toString(), "UPDATE", "OK", "Project has been updated");
			} else {
				String error = errorNode.toString();
				result = new ActionResult("N/A", "REJECT", "ERROR", "Error: " + error);
			}

		} catch (RestClientException e) {
			if (e.getClass().equals(HttpServerErrorException.class)) {
				HttpServerErrorException ex = (HttpServerErrorException) e;
				JsonBean resultPost = JsonBean.getJsonBeanFromString(ex.getResponseBodyAsString());
				Object errorNode = resultPost.get("error");
				result = new ActionResult("N/A", "ERROR", "ERROR", "REST Exception:" + errorNode);
			} else {
				result = new ActionResult("N/A", "ERROR", "ERROR", "REST Exception:" + e.getMessage());
			}
		} catch (ValueMappingException e) {
			result = new ActionResult("N/A", "ERROR", "ERROR", "Value Mapping Exception: " + e.getMessage());
		}
		return result;
	}

	private void updateProject(JsonBean project, InternalDocument source, List<FieldMapping> fieldMappings, List<FieldValueMapping> valueMappings) throws ValueMappingException {
		Boolean hasTransactions = false;
		for (FieldMapping mapping : fieldMappings) {
			Field sourceField = mapping.getSourceField();
			Field destinationField = mapping.getDestinationField();

			switch (sourceField.getType()) {
			case LIST:
				if (!destinationField.getFieldName().equals("type_of_assistance") && !destinationField.getFieldName().equals("financing_instrument")) {
					Optional<FieldValueMapping> optValueMapping = valueMappings.stream().filter(n -> {
						return n.getSourceField().getFieldName().equals(mapping.getSourceField().getFieldName());
					}).findFirst();
					if (optValueMapping.isPresent() && sourceField.isMultiple()) {
						project.set(destinationField.getFieldName(), getCodesFromList(source, optValueMapping.get()));
					} else {
						project.set(destinationField.getFieldName(), getCodeFromList(source, optValueMapping.get()));
					}
				}
				break;
			case MULTILANG_STRING:
				project.set(destinationField.getFieldName(), getMapFromString(source, destinationField.getFieldName(), mapping));
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
			JsonBean fundings = getTransactions(source, fieldMappings, valueMappings);
			if (fundings != null) {
				project.set("fundings", fundings);
				List<JsonBean> listDonorOrganizations = new ArrayList<JsonBean>();
				JsonBean donorRole = new JsonBean();
				donorRole.set("organization", fundings.get("donor_organization_id"));
				donorRole.set("role", 1);
				donorRole.set("percentage", 100);
				listDonorOrganizations.add(donorRole);
				project.set("donor_organization", listDonorOrganizations);
			}
		}
	}

	private JsonBean getProject(String documentId) {
		JsonBean projectObject = null;
		RestTemplate restTemplate = getRestTemplate();

		String result = restTemplate.getForObject(baseURL + "/rest/activity/projects/" + documentId, String.class);
		projectObject = JsonBean.getJsonBeanFromString(result);

		return projectObject;
	}

	@Override
	public String getDescriptiveName() {

		return this.descriptiveName;
	}

	@Override
	public ActionResult insert(InternalDocument source, List<FieldMapping> fieldMapping, List<FieldValueMapping> valueMapping) {

		ActionResult result;

		RestTemplate restTemplate = getRestTemplate();
		try {
			JsonBean project = transformProject(source, fieldMapping, valueMapping);
			log.info(project);
			// JsonBean resultPost = JsonBean.getJsonBeanFromString("{}");
			JsonBean resultPost = restTemplate.postForObject(baseURL + "/rest/activity", project, JsonBean.class);
			Object errorNode = resultPost.get("error");

			if (errorNode == null) {
				Integer id = (int) resultPost.get("internal_id");
				result = new ActionResult(id.toString(), "INSERT", "OK", "Project has been inserted");
			} else {
				String error = errorNode.toString();
				result = new ActionResult("N/A", "REJECT", "ERROR", "Error: " + error);
			}

		} catch (RestClientException e) {
			if (e.getClass().equals(HttpServerErrorException.class)) {
				HttpServerErrorException ex = (HttpServerErrorException) e;
				JsonBean resultPost = JsonBean.getJsonBeanFromString(ex.getResponseBodyAsString());
				Object errorNode = resultPost.get("error");
				result = new ActionResult("N/A", "ERROR", "ERROR", "REST Exception:" + errorNode);
			} else {
				result = new ActionResult("N/A", "ERROR", "ERROR", "REST Exception:" + e.getMessage());
			}
		} catch (ValueMappingException e) {
			result = new ActionResult("N/A", "ERROR", "ERROR", "Value Mapping Exception: " + e.getMessage());
		}
		return result;
	}

	private JsonBean transformProject(InternalDocument source, List<FieldMapping> fieldMappings, List<FieldValueMapping> valueMappings) throws ValueMappingException {
		Boolean hasTransactions = false;
		JsonBean project = new JsonBean();

		project.set("project_code", source.getIdentifier());
		project.set("project_title", getMultilangString(source, "project_title", "title"));

		for (FieldMapping mapping : fieldMappings) {
			Field sourceField = mapping.getSourceField();
			Field destinationField = mapping.getDestinationField();

			switch (sourceField.getType()) {
			case LIST:
				if (!destinationField.getFieldName().equals("type_of_assistance") && !destinationField.getFieldName().equals("financing_instrument")) {
					Optional<FieldValueMapping> optValueMapping = valueMappings.stream().filter(n -> {
						return n.getSourceField().getFieldName().equals(mapping.getSourceField().getFieldName());
					}).findFirst();
					if (optValueMapping.isPresent() && sourceField.isMultiple()) {
						project.set(destinationField.getFieldName(), getCodesFromList(source, optValueMapping.get()));
					} else {
						project.set(destinationField.getFieldName(), getCodeFromList(source, optValueMapping.get()));
					}
				}
				break;
			case MULTILANG_STRING:
				project.set(destinationField.getFieldName(), getMapFromString(source, destinationField.getFieldName(), mapping));
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
			JsonBean fundings = getTransactions(source, fieldMappings, valueMappings);
			if (fundings != null) {
				project.set("fundings", fundings);
				List<JsonBean> listDonorOrganizations = new ArrayList<JsonBean>();
				JsonBean donorRole = new JsonBean();
				donorRole.set("organization", fundings.get("donor_organization_id"));
				donorRole.set("role", 1);
				donorRole.set("percentage", 100);
				listDonorOrganizations.add(donorRole);
				project.set("donor_organization", listDonorOrganizations);
			}
		}

		return project;
	}

	private Object getMultilangString(InternalDocument source, String destinationFieldName, String sourceFieldName) {
		Map<String, String> fieldValues = source.getMultilangFields().get(sourceFieldName);
		Field field = fieldList.stream().filter(n -> {
			return n.getFieldName().equals(destinationFieldName);
		}).findFirst().get();

		if (field.getType() == FieldType.MULTILANG_STRING) {
			return source.getMultilangFields().get(sourceFieldName);
		} else {
			return fieldValues.values().iterator().next();
		}
	}

	private JsonBean getTransactions(InternalDocument source, List<FieldMapping> fieldMappings, List<FieldValueMapping> valueMappings) throws ValueMappingException {
		List<JsonBean> fundingDetails = new ArrayList<JsonBean>();
		String currencyCode = source.getStringFields().get("default-currency");
		String currencyIdString = getCurrencyId(currencyCode);
		int currencyId = Integer.parseInt(currencyIdString);

		for (FieldMapping mapping : fieldMappings) {
			Field sourceField = mapping.getSourceField();
			Field destinationField = mapping.getDestinationField();
			if (sourceField.getType() == FieldType.TRANSACTION) {
				// What kind of transaction is this???
				String sourceSubType = sourceField.getSubType();

				// Get the transactions from the source that are of that subtype
				// then!!!
				Map<String, Map<String, String>> transactions = source.getTransactionFields().entrySet().stream().filter(p -> {
					return p.getValue().get("subtype").equals(sourceSubType);
				}).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

				// Now we need the mapping of the field
				String destinationSubType = destinationField.getSubType();

				// ---------------------TRANSACTION
				// DETAILS-----------------------------------
				for (Entry<String, Map<String, String>> entry : transactions.entrySet()) {
					JsonBean fundingDetail = new JsonBean();
					Map<String, String> value = entry.getValue();
					String amount = value.get("value");
					String dateString = value.get("date");

					fundingDetail.set("transaction_type", getTransactionType(sourceSubType));
					fundingDetail.set("adjustment_type", getAdjustmentType(destinationSubType));
					fundingDetail.set("transaction_date", getTransactionDate(dateString));
					fundingDetail.set("currency", currencyId);
					fundingDetail.set("transaction_amount", getTransactionAmount(amount));
					fundingDetails.add(fundingDetail);
				}
				// ---------------------------------------------------------
			}
		}

		JsonBean funding = new JsonBean();
		Map<String, Map<String, String>> organizations = source.getOrganizationFields().entrySet().stream().filter(p -> {
			return p.getValue().get("role").equals("Funding");
		}).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

		if (organizations.size() > 0) {
			Entry<String, Map<String, String>> organization = organizations.entrySet().stream().findFirst().get();
			int donorId = getIdFromList(organization.getValue().get("value"), "participating-org", fieldMappings, valueMappings);
			funding.set("donor_organization_id", donorId);

		}

		try {
			String typeOfAssistance = source.getStringFields().get("default-finance-type");
			funding.set("type_of_assistance", getIdFromList(typeOfAssistance, "default-finance-type", fieldMappings, valueMappings));
		} catch (ValueMappingException e) {
			log.debug("Dependent field not loaded: default-finance-type");
		}

		try {
			String financingInstrument = source.getStringFields().get("default-aid-type");
			funding.set("financing_instrument", getIdFromList(financingInstrument, "default-aid-type", fieldMappings, valueMappings));
		} catch (ValueMappingException e) {
			log.debug("Dependent field not loaded: default-aid-type");
		}

		// funding.set("financing_instrument",
		// getFinancingInstrument(financingInstrument, fieldMappings,
		// valueMappings));

		funding.set("source_role", 1);
		funding.set("funding_details", fundingDetails);

		return funding;
	}

	private String getCurrencyId(String currencyCode) {
		Field currency = fieldList.stream().filter(n -> {
			return n.getFieldName().equals("currency_code");
		}).findFirst().get();

		FieldValue currencyValue = currency.getPossibleValues().stream().filter(n -> {
			return n.getValue().equals(currencyCode);
		}).findFirst().get();

		return currencyValue.getCode();
	}

	private Object getTransactionAmount(String amount) {
		Double amountValue = Double.parseDouble(amount);
		return amountValue;
	}

	// private int getFinancingInstrument(String financingInstrument,
	// List<FieldMapping> fieldMappings, List<FieldValueMapping> valueMappings)
	// {
	// FieldValueMapping vm = valueMappings.stream().filter(n -> {
	// return n.getSourceField().getFieldName().equals("default-aid-type");
	// }).findFirst().get();
	// FieldValue fvs =
	// vm.getSourceField().getPossibleValues().stream().filter(n -> {
	// return n.getCode().equals(financingInstrument);
	// }).findFirst().get();
	// Integer sourceValueIndex = fvs.getIndex();
	// Integer destinationValueIndex =
	// vm.getValueIndexMapping().get(sourceValueIndex);
	// FieldValue fvd =
	// vm.getDestinationField().getPossibleValues().stream().filter(n -> {
	// return n.getIndex() == destinationValueIndex;
	// }).findFirst().get();
	// return Integer.parseInt(fvd.getCode());
	// }
	//
	// private int getTypeOfAssistance(String typeOfAssistance,
	// List<FieldMapping> fieldMappings, List<FieldValueMapping> valueMappings)
	// throws ValueMappingException {
	// Optional<FieldValueMapping> optVm = valueMappings.stream().filter(n -> {
	// return n.getSourceField().getFieldName().equals("default-finance-type");
	// }).findFirst();
	// if (!optVm.isPresent()) {
	// throw new ValueMappingException("default-finance-type not found.");
	// }
	// FieldValueMapping vm = optVm.get();
	//
	// FieldValue fvs =
	// vm.getSourceField().getPossibleValues().stream().filter(n -> {
	// return n.getCode().equals(typeOfAssistance);
	// }).findFirst().get();
	// Integer sourceValueIndex = fvs.getIndex();
	// Integer destinationValueIndex =
	// vm.getValueIndexMapping().get(sourceValueIndex);
	// FieldValue fvd =
	// vm.getDestinationField().getPossibleValues().stream().filter(n -> {
	// return n.getIndex() == destinationValueIndex;
	// }).findFirst().get();
	// return Integer.parseInt(fvd.getCode());
	// }
	//
	// private int getDonorOrganization(String orgName, List<FieldMapping>
	// fieldMappings, List<FieldValueMapping> valueMappings) throws
	// ValueMappingException {
	// Optional<FieldValueMapping> optVm = valueMappings.stream().filter(n -> {
	// return n.getSourceField().getFieldName().equals("participating-org");
	// }).findFirst();
	//
	// if (!optVm.isPresent()) {
	// throw new ValueMappingException("participating-org not found.");
	// }
	// FieldValueMapping vm = optVm.get();
	//
	// FieldValue fvs =
	// vm.getSourceField().getPossibleValues().stream().filter(n -> {
	// return n.getValue().equals(orgName);
	// }).findFirst().get();
	// Integer sourceValueIndex = fvs.getIndex();
	// Integer destinationValueIndex =
	// vm.getValueIndexMapping().get(sourceValueIndex);
	// FieldValue fvd =
	// vm.getDestinationField().getPossibleValues().stream().filter(n -> {
	// return n.getIndex() == destinationValueIndex;
	// }).findFirst().get();
	// return Integer.parseInt(fvd.getCode());
	// }
	//
	private int getIdFromList(String fieldValue, String sourceField, List<FieldMapping> fieldMappings, List<FieldValueMapping> valueMappings) throws ValueMappingException {
		Optional<FieldValueMapping> optVm = valueMappings.stream().filter(n -> {
			return n.getSourceField().getFieldName().equals(sourceField);
		}).findFirst();

		if (!optVm.isPresent()) {
			throw new ValueMappingException(sourceField + " not found.");
		}
		FieldValueMapping vm = optVm.get();

		FieldValue fvs = vm.getSourceField().getPossibleValues().stream().filter(n -> {
			return n.getValue().equals(fieldValue);
		}).findFirst().get();
		Integer sourceValueIndex = fvs.getIndex();
		Integer destinationValueIndex = vm.getValueIndexMapping().get(sourceValueIndex);
		FieldValue fvd = vm.getDestinationField().getPossibleValues().stream().filter(n -> {
			return n.getIndex() == destinationValueIndex;
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

	private int getTransactionType(String transactionTypeValue) {
		Field transactionType = this.getFields().stream().filter(n -> {
			return n.getFieldName().equals("transaction_type");
		}).findFirst().get();

		String transactionTypeId = transactionType.getPossibleValues().stream().filter(n -> {
			return n.getValue().equals(getTTNameSource(transactionTypeValue));
		}).findFirst().get().getCode();
		return Integer.parseInt(transactionTypeId);
		// return null;
	}

	private String getTTNameSource(String transactionTypeValue) {
		switch (transactionTypeValue) {
		case "C":
			return "Commitments";
		case "D":
			return "Disbursements";
		}
		return "";
	}

	private String getATNameDestination(String transactionTypeValue) {
		switch (transactionTypeValue) {
		case "AC":
		case "AD":
			return "Actual";
		case "PC":
		case "PD":
			return "Planned";
		}
		return "";
	}

	private int getAdjustmentType(String value) {
		Field adjustmentType = this.getFields().stream().filter(n -> {
			return n.getFieldName().equals("adjustment_type");
		}).findFirst().get();

		String adjustmentTypeValue = adjustmentType.getPossibleValues().stream().filter(n -> {
			return n.getValue().equals(getATNameDestination(value));
		}).findFirst().get().getCode();
		return Integer.parseInt(adjustmentTypeValue);
	}

	private String getString(InternalDocument source, FieldMapping mapping) {
		return source.getStringFields().get(mapping.getSourceField().getFieldName());
	}

	private String getFormattedDateFromString(InternalDocument source, FieldMapping mapping) {
		String uniqueFieldName = mapping.getSourceField().getUniqueFieldName();
		Date date = source.getDateFields().get(uniqueFieldName);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String nowAsISO = df.format(date);
		return nowAsISO;
	}

	private Object getMapFromString(InternalDocument source, String destinationFieldName, FieldMapping mapping) {
		Object langString = getMultilangString(source, destinationFieldName, mapping.getSourceField().getFieldName());
		return langString;
	}

	private List<JsonBean> getCodesFromList(InternalDocument source, FieldValueMapping mapping) {
		List<JsonBean> beanList = new ArrayList<JsonBean>();
		Object value = source.getStringMultiFields().get(mapping.getSourceField().getFieldName());
		Map<Integer, Integer> valueMapIndex = mapping.getValueIndexMapping();
		List<FieldValue> sourcePossibleValues = mapping.getSourceField().getPossibleValues();
		String[] stringValues = (String[]) value;
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

			JsonBean bean = new JsonBean();
			bean.set(mapping.getSourceField().getFieldName() + "_id", intValue);
			if (mapping.getSourceField().isPercentage())
				bean.set(mapping.getSourceField().getFieldName() + "_percentage", 100 / stringValues.length);
			beanList.add(bean);
		}
		return beanList;
	}

	private Integer getCodeFromList(InternalDocument source, FieldValueMapping mapping) {
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
		Integer intValue = Integer.parseInt(destinationValue.getCode());
		return intValue;
	}

	private void instantiateStaticFields() {

		Map<String, FieldType> fieldTypes = getFieldTypes();
		// Fixed fields
		fieldList.add(new Field("IATI Identifier", "iati-identifier", FieldType.STRING, false));
		fieldList.add(new Field("Project Title", "project_title", fieldTypes.get("project_title"), false));

		// Code Lists
		Field activityStatus = new Field("Activity Status", "activity_status", FieldType.LIST, true);
		activityStatus.setPossibleValues(getCodeListValues("activity_status"));
		fieldList.add(activityStatus);

		Field typeOfAssistence = new Field("Type of Assistance", "type_of_assistance", FieldType.LIST, true);
		typeOfAssistence.setPossibleValues(getCodeListValues("fundings~type_of_assistance"));
		// typeOfAssistence.setPossibleValues(getToA());
		fieldList.add(typeOfAssistence);

		Field financialInstrument = new Field("Aid Modality", "financing_instrument", FieldType.LIST, true);
		financialInstrument.setPossibleValues(getCodeListValues("fundings~financing_instrument"));
		fieldList.add(financialInstrument);

		Field adjustmentType = new Field("Adjustment Type", "adjustment_type", FieldType.LIST, true);
		adjustmentType.setPossibleValues(getCodeListValues("fundings~funding_details~adjustment_type"));
		fieldList.add(adjustmentType);

		Field transactionType = new Field("Transaction Type", "transaction_type", FieldType.LIST, false);
		transactionType.setPossibleValues(getCodeListValues("fundings~funding_details~transaction_type"));
		fieldList.add(transactionType);

		Field primarySector = new Field("Primary Sector", "primary_sectors", FieldType.LIST, true);
		primarySector.setPossibleValues(getCodeListValues("primary_sectors~sector_id"));
		primarySector.setMultiple(true);
		fieldList.add(primarySector);

		Field secondarySector = new Field("Secondary Sector", "secondary_sectors", FieldType.LIST, true);
		secondarySector.setPossibleValues(getCodeListValues("secondary_sectors~sector_id"));
		fieldList.add(secondarySector);

		Field tertiarySector = new Field("Tertiary Sector", "tertiary_sectors", FieldType.LIST, true);
		tertiarySector.setPossibleValues(getCodeListValues("tertiary_sectors~sector_id"));
		fieldList.add(tertiarySector);

		// Multi-language strings
		fieldList.add(new Field("Activity Description", "description", fieldTypes.get("description"), true));

		// Dates
		if (fieldTypes.get("planned_start_date") != null) {
			fieldList.add(new Field("Planned Start Date", "planned_start_date", FieldType.DATE, true));
		}
		if (fieldTypes.get("actual_start_date") != null) {
			fieldList.add(new Field("Actual Start Date", "actual_start_date", FieldType.DATE, true));
		}
		if (fieldTypes.get("original_completion_date") != null) {
			fieldList.add(new Field("Original Completion Date", "original_completion_date", FieldType.DATE, true));
		}
		if (fieldTypes.get("actual_completion_date") != null) {
			fieldList.add(new Field("Actual Completion Date", "actual_completion_date", FieldType.DATE, true));
		}

		// Transactions
		// Transaction Fields
		Field actualCommitments = new Field("Actual Commitments", "transaction", FieldType.TRANSACTION, true);
		actualCommitments.setSubType("AC");
		fieldList.add(actualCommitments);

		Field actualDisbursements = new Field("Actual Disbursements", "transaction", FieldType.TRANSACTION, true);
		actualDisbursements.setSubType("AD");
		fieldList.add(actualDisbursements);

		Field plannedCommitments = new Field("Planned Commitments", "transaction", FieldType.TRANSACTION, true);
		plannedCommitments.setSubType("PC");
		fieldList.add(plannedCommitments);

		Field plannedDisbursements = new Field("Planned Disbursements", "transaction", FieldType.TRANSACTION, true);
		plannedDisbursements.setSubType("PD");
		fieldList.add(plannedDisbursements);

		// Organizations
		Field fundingOrganization = new Field("Funding Organization", "donor_organization", FieldType.ORGANIZATION, true);
		fundingOrganization.setPossibleValues(getCodeListValues("fundings~donor_organization_id"));
		fieldList.add(fundingOrganization);

		// Currency
		Field currency = new Field("Currency Code", "currency_code", FieldType.LIST, true);
		currency.setPossibleValues(getCodeListValues("fundings~funding_details~currency"));
		fieldList.add(currency);

	}

	private Map<String, FieldType> getFieldTypes() {
		Map<String, FieldType> map = new HashMap<String, FieldType>();
		String result = "";
		RestTemplate restTemplate = getRestTemplate();
		result = restTemplate.getForObject(baseURL + this.getFieldsEndpoint(), String.class);
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(result);

			if (jsonNode.isArray()) {
				Iterator<JsonNode> mainNode = jsonNode.elements();
				while (mainNode.hasNext()) {
					JsonNode node = mainNode.next();
					String fieldName = node.get("field_name").asText();
					String fieldType = node.get("field_type").asText();
					FieldType ft = FieldType.STRING;
					switch (fieldType) {
					case "list":
						ft = FieldType.LIST;
						break;
					case "string":
						if (node.get("translatable").asBoolean()) {
							ft = FieldType.MULTILANG_STRING;
						} else {
							ft = FieldType.STRING;
						}
						break;
					case "date":
						ft = FieldType.DATE;
						break;
					}
					map.put(fieldName, ft);
				}
			}

		} catch (Exception e) {
			log.error("Couldn't retrieve field properties from endpoint. Exception: " + e.getMessage());
		}
		return map;
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
			log.error("Couldn't retrieve values from Endpoint. Exception: " + e.getMessage() + ", URL:" + codeListName);
		}
		return possibleValues;
	}

}
