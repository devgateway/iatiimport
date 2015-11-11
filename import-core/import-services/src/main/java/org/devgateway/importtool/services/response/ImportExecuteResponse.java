package org.devgateway.importtool.services.response;

import java.util.ArrayList;
import java.util.List;

import org.devgateway.importtool.services.processor.helper.ActionResult;
import org.devgateway.importtool.services.processor.helper.ProcessStatus;

public class ImportExecuteResponse {
	
	ProcessStatus executeStatus = ProcessStatus.NOT_STARTED;
	public ProcessStatus getExecuteStatus() {
		return executeStatus;
	}
	public void setExecuteStatus(ProcessStatus executeStatus) {
		this.executeStatus = executeStatus;
	}
	public List<ActionResult> getResults() {
		return results;
	}
	public void setResults(List<ActionResult> results) {
		this.results = results;
	}
	List<ActionResult> results = new ArrayList<ActionResult>();

}
