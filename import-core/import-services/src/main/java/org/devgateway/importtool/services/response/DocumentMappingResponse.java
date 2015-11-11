package org.devgateway.importtool.services.response;

import java.util.ArrayList;
import java.util.List;

import org.devgateway.importtool.services.processor.helper.DocumentMapping;
import org.devgateway.importtool.services.processor.helper.ProcessStatus;

public class DocumentMappingResponse {
	
	private ProcessStatus documentMappingStatus;
	
	private List<DocumentMapping> documentMappings = new ArrayList<DocumentMapping>();

	public ProcessStatus getDocumentMappingStatus() {
		return documentMappingStatus;
	}

	public void setDocumentMappingStatus(ProcessStatus documentMappingStatus) {
		this.documentMappingStatus = documentMappingStatus;
	}

	public List<DocumentMapping> getDocumentMappings() {
		return documentMappings;
	}

	public void setDocumentMappings(List<DocumentMapping> documentMappings) {
		this.documentMappings = documentMappings;
	}
	
	

}
