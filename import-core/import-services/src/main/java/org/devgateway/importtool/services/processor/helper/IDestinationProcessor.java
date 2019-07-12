package org.devgateway.importtool.services.processor.helper;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.devgateway.importtool.exceptions.CurrencyNotFoundException;
import org.devgateway.importtool.exceptions.MissingPrerequisitesException;
import org.devgateway.importtool.services.request.ImportRequest;
import org.springframework.web.client.RestTemplate;

public interface IDestinationProcessor {
	public String getDescriptiveName();

	public void setAuthenticationToken(String authToken);

	void reset();

	public List<Field> getFields();

	public List<InternalDocument> getDocuments(Boolean summary);

	void insert(InternalDocument source, List<FieldMapping> fieldMapping, List<FieldValueMapping> valueMapping,
				ImportRequest importRequest) throws ValueMappingException, CurrencyNotFoundException, ParseException, UnsupportedFieldTypeException;

	void update(InternalDocument source, InternalDocument destination, List<FieldMapping> fieldMapping,
				List<FieldValueMapping> valueMapping, boolean overrideTitle, ImportRequest importRequest)
			throws ValueMappingException, CurrencyNotFoundException, ParseException, UnsupportedFieldTypeException;

	public String getTitleField();
	 void setProcessorVersion(String processorVersion);

	String getProcessorVersion();

    public void setActionStatus(ActionStatus documentMappingStatus);
	public List<DocumentMapping> preImportProcessing(List<DocumentMapping> documentMappings);

    void loadProjectsForUpdate(List<String> listOfAmpIds);

	List<ActionResult> processProjectsInBatch(ActionStatus importStatus);

	void initialize (String token);
}
