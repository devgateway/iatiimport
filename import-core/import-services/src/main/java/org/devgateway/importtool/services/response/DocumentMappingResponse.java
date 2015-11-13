package org.devgateway.importtool.services.response;

import java.util.ArrayList;
import java.util.List;

import org.devgateway.importtool.services.processor.helper.ActionStatus;
import org.devgateway.importtool.services.processor.helper.DocumentMapping;
import org.devgateway.importtool.services.processor.helper.Status;

public class DocumentMappingResponse {
	
	private ActionStatus documentMappingStatus;
	
	private List<DocumentMapping> documentMappings = new ArrayList<DocumentMapping>();

	public ActionStatus getDocumentMappingStatus() {
		return documentMappingStatus;
	}

	public void setDocumentMappingStatus(ActionStatus documentMappingStatus) {
		this.documentMappingStatus = documentMappingStatus;
	}

	public List<DocumentMapping> getDocumentMappings() {
		return documentMappings;
	}

	public void setDocumentMappings(List<DocumentMapping> documentMappings) {
		this.documentMappings = documentMappings;
	}
	
	

}
