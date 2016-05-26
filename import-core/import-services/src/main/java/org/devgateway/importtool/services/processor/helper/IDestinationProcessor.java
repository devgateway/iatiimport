package org.devgateway.importtool.services.processor.helper;

import java.util.List;

public interface IDestinationProcessor {
	public String getDescriptiveName();

	public void setAuthenticationToken(String authToken);

	public List<Field> getFields();

	public List<InternalDocument> getDocuments(Boolean summary);

	public String getIdField();

	public ActionResult insert(InternalDocument source, List<FieldMapping> fieldMapping, List<FieldValueMapping> valueMapping);

	public ActionResult update(InternalDocument source, InternalDocument destination, List<FieldMapping> fieldMapping, List<FieldValueMapping> valueMapping, boolean overrideTitle);

	public String getTitleField();

	public void setActionStatus(ActionStatus documentMappingStatus);
	public List<DocumentMapping> preImportProcessing(List<DocumentMapping> documentMappings);
	

}
