package org.devgateway.importtool.services.processor;

import java.io.IOException;
import java.io.InputStream;
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
	private String baseURL = "http://localhost:8081";

	private String fieldsEndpoint = "/rest/activity/fields";
	private String documentsEndpoint = "/rest/activity/projects";
	private String documentsTestEndpoint = "activity_list.json";
	private String authenticationToken;
	private String descriptiveName = "AMP 2.11";

	private Boolean testMode = false;

	private List<Field> fieldList = new ArrayList<Field>();

	private List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
	private RestTemplate template;

	public AMPStaticProcessor(String authenticationToken) {
		this.setAuthenticationToken(authenticationToken);
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
	public ActionResult update(InternalDocument source, InternalDocument destination, List<FieldMapping> fieldMapping, List<FieldValueMapping> valueMapping) {
		ActionResult result = new ActionResult("1", "UPDATE", "OK", "Project has been updated");
		log.debug("Update new document in destination system");
		return result;
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
			String resultPost = restTemplate.postForObject(baseURL + "/rest/activityNO", project, String.class);
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

	private JsonBean transformProject(InternalDocument source, List<FieldMapping> fieldMapping, List<FieldValueMapping> valueMapping) {
		JsonBean project = new JsonBean();

		project.set("project_code", source.getIdentifier() + System.currentTimeMillis());

		Map<String, String> title = new HashMap<String, String>();
		title.put("en", System.currentTimeMillis() + "");
		project.set("project_title", title);

		for (FieldMapping mapping : fieldMapping) {
			Field sourceField = mapping.getSourceField();
			Field destinationField = mapping.getDestinationField();

			switch (sourceField.getType()) {
			case LIST:
				Optional<FieldValueMapping> optValueMapping = valueMapping.stream().filter(n -> {
					return n.getSourceField().getFieldName().equals(mapping.getSourceField().getFieldName());
				}).findFirst();
				if (optValueMapping.isPresent() && sourceField.isMultiple()) {
					project.set(destinationField.getFieldName(), getCodesFromList(source, optValueMapping.get()));
				} else {
					project.set(destinationField.getFieldName(), getCodeFromList(source, optValueMapping.get()));
				}
				break;
			case MULTILANG_STRING:
				project.set(destinationField.getFieldName(), getMapFromString(source, mapping));
				break;
			case DATE:
				project.set(destinationField.getFieldName(), getFormattedDateFromString(source, mapping));
				break;
			case STRING:
				project.set(destinationField.getFieldName(), getString(source, mapping));
				break;
			case TRANSACTION:
				@SuppressWarnings("unchecked")
				List<JsonBean> fundings = (List<JsonBean>) project.get("fundings");
				if (fundings == null) {
					fundings = new ArrayList<JsonBean>();
				}
				JsonBean funding = getTransaction(source, mapping, fieldMapping, valueMapping);
				fundings.add(funding);
				break;
			case ORGANIZATION:
				// TODO: Implement Organization. For now only Funding
				// Organization is used, and that happens inside the TRANSACTION
				break;
			default:
				break;
			}
		}

		return project;
	}

	private JsonBean getTransaction(InternalDocument source, FieldMapping fieldMapping, List<FieldMapping> fieldMappings, List<FieldValueMapping> valueMappings) {
		List<JsonBean> fundingDetails = new ArrayList<JsonBean>();
		JsonBean fundingDetail = new JsonBean();
		System.out.println(fieldMapping);
		// Get transaction from source of the correct mapped type
		// Global values that go to every transaction
		int currencyId = 10; // http://localhost:9010/rest/activity/fields/fundings~funding_details~currency

		for (Entry<String, Map<String, String>> entry : source.getTransactionFields().entrySet()) {
//			String key = entry.getKey();
			Map<String, String> value = entry.getValue();

			String transactionType = value.get("subtype");
			String amount = value.get("value");
			String dateString = value.get("date");

			// Unused fields for now
//			String receivingOrg = value.get("receiving-org");
//			String reference = value.get("reference");
			// Transaction Type -> (Commitment/Disbursement)
			fundingDetail.set("transaction_type", getTransactionType(transactionType));
			// Adjustment Type -> (Actual/Planned)
			fundingDetail.set("adjustment_type", getAdjustmentType("Actual"));
			// Transaction Date
			fundingDetail.set("transaction_date", getTransactionDate(dateString));
			fundingDetail.set("currency", currencyId);
			fundingDetail.set("transaction_amount", getTransactionAmount(amount));

			fundingDetails.add(fundingDetail);
		}

		JsonBean funding = new JsonBean();

		String receivingOrg = source.getStringFields().get("receiving-org");
		funding.set("donor_organization_id", getDonorOrganizationId(receivingOrg, fieldMappings, valueMappings));

		String typeOfAssistance = source.getStringFields().get("default-finance-type");
		funding.set("type_of_assistance", getTypeOfAssistance(typeOfAssistance, fieldMappings, valueMappings));

		String financingInstrument = source.getStringFields().get("default-aid-type");
		funding.set("financing_instrument", getFinancingInstrument(financingInstrument, fieldMappings, valueMappings));

		funding.set("source_role", 1);
		funding.set("funding_details", fundingDetails);

		return funding;
	}

	private Object getTransactionAmount(String amount) {
		Double amountValue = Double.parseDouble(amount);
		return amountValue;
	}

	private Object getFinancingInstrument(String financingInstrument, List<FieldMapping> fieldMappings, List<FieldValueMapping> valueMappings) {
		// TODO Auto-generated method stub
		return null;
	}

	private Object getTypeOfAssistance(String typeOfAssistance, List<FieldMapping> fieldMappings, List<FieldValueMapping> valueMappings) {
		// TODO Auto-generated method stub
		return null;
	}

	private Object getDonorOrganizationId(String receivingOrg, List<FieldMapping> fieldMappings, List<FieldValueMapping> valueMappings) {
		return null;
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
			return n.getValue().equals(getTTName(transactionTypeValue));
		}).findFirst().get().getCode();
		return Integer.parseInt(transactionTypeId);
		// return null;
	}

	private String getTTName(String transactionTypeValue) {
		switch (transactionTypeValue) {
		case "C":
			return "Commitments";
		case "D":
			return "Disbursements";
		}
		return "";
	}

	private String getAdjustmentType(String value) {
		Field adjustmentType = this.getFields().stream().filter(n -> {
			return n.getFieldName().equals("adjustment_type");
		}).findFirst().get();

		String adjustmentTypeValue = adjustmentType.getPossibleValues().stream().filter(n -> {
			return n.getValue().equals(value);
		}).findFirst().get().getCode();
		return adjustmentTypeValue;
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

	private Map<String, String> getMapFromString(InternalDocument source, FieldMapping mapping) {
		Map<String, String> langString = source.getMultilangFields().get(mapping.getSourceField().getFieldName());
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

	private String getDocumentsTestEndpoint() {
		return documentsTestEndpoint;
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
		fieldList.add(new Field("Activity Description", "activity_description", FieldType.MULTILANG_STRING, true));

		// Dates
		fieldList.add(new Field("Planned Start Date", "planned_start_date", FieldType.DATE, true));
		fieldList.add(new Field("Actual Start Date", "actual_start_date", FieldType.DATE, true));
		fieldList.add(new Field("Original Completion Date", "original_completion_date", FieldType.DATE, true));
		fieldList.add(new Field("Actual Completion Date", "actual_completion_date", FieldType.DATE, true));

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
