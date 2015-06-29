package org.devgateway.importtool.services.processor.helper;

import java.util.List;

public interface IDestinationProcessor {
	public String getDescriptiveName();

	public void setAuthenticationToken(String authToken);

	public List<Field> getFields();

	public List<InternalDocument> getDocuments();

	public String getIdField();

	public Boolean getTestMode();

	public void setTestMode(Boolean testMode);

	public ActionResult insert(InternalDocument source);

	public ActionResult update(InternalDocument source,
			InternalDocument destination);

	public String getTitleField();

}
