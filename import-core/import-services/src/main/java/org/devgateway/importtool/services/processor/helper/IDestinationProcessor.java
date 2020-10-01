package org.devgateway.importtool.services.processor.helper;

import java.text.ParseException;
import java.util.List;

import org.devgateway.importtool.exceptions.CurrencyNotFoundException;
import org.devgateway.importtool.services.dto.JsonBean;
import org.devgateway.importtool.services.dto.MappedProject;
import org.devgateway.importtool.services.request.ImportRequest;

public interface IDestinationProcessor {
	public String getDescriptiveName();

	void reset();

	public List<Field> getFields();

	public List<InternalDocument> getDocuments(Boolean summary);

	void insert(InternalDocument source, List<FieldMapping> fieldMapping, List<FieldValueMapping> valueMapping,
				ImportRequest importRequest) throws ValueMappingException, CurrencyNotFoundException, ParseException, UnsupportedFieldTypeException, AmpResourceNotCreatedException;

	void update(InternalDocument source, InternalDocument destination, List<FieldMapping> fieldMapping,
				List<FieldValueMapping> valueMapping, boolean overrideTitle, ImportRequest importRequest)
			throws ValueMappingException, CurrencyNotFoundException, ParseException, UnsupportedFieldTypeException,
			AmpResourceNotCreatedException;

	public String getTitleField();
	 void setProcessorVersion(String processorVersion);

	String getProcessorVersion();

    public void setActionStatus(ActionStatus documentMappingStatus);
	public List<DocumentMapping> preImportProcessing(List<DocumentMapping> documentMappings);

    void loadProjectsForUpdate(List<String> listOfAmpIds);

	List<ActionResult> processProjectsInBatch(ActionStatus importStatus);
	
	void initialize(String ampJSessionId);

	default MappedProject getMappedProjectFromSource(InternalDocument source, JsonBean project) {
		MappedProject mappedProject = new MappedProject();
		mappedProject.setGroupingCriteria(source.getGrouping());
		mappedProject.setProjectIdentifier(source.getIdentifier());
		mappedProject.setProject(project);

		return mappedProject;
	}

}
