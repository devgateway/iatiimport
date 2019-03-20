package org.devgateway.importtool.services.processor.helper;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.devgateway.importtool.exceptions.CurrencyNotFoundException;
import org.devgateway.importtool.services.request.ImportRequest;
import org.springframework.web.client.RestTemplate;

public interface IDestinationProcessor {
	public String getDescriptiveName();

	public void setAuthenticationToken(String authToken);

	public List<Field> getFields();

	public List<InternalDocument> getDocuments(Boolean summary);

	public String getIdField();

	void insert(InternalDocument source, List<FieldMapping> fieldMapping, List<FieldValueMapping> valueMapping,
				ImportRequest importRequest) throws ValueMappingException, CurrencyNotFoundException, ParseException;

	void update(InternalDocument source, InternalDocument destination, List<FieldMapping> fieldMapping,
				List<FieldValueMapping> valueMapping, boolean overrideTitle, ImportRequest importRequest)
			throws ValueMappingException, CurrencyNotFoundException, ParseException;

	public String getTitleField();

	String getProcessorVersion();

	void setProcessorVersion(String processorVersion);

    public void setActionStatus(ActionStatus documentMappingStatus);
	public List<DocumentMapping> preImportProcessing(List<DocumentMapping> documentMappings);
	//void setRestTemplate (RestTemplate restTemplate);
	
    void loadProjectsForUpdate(List<String> listOfAmpIds);

	List<ActionResult> processProjectsInBatch(ActionStatus importStatus);
}
