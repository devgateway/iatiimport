package org.devgateway.importtool.services.response;

import java.util.ArrayList;
import java.util.List;

import org.devgateway.importtool.services.processor.helper.ActionResult;
import org.devgateway.importtool.services.processor.helper.ActionStatus;

public class ImportExecuteResponse {
	
	private ActionStatus importStatus;
	
	public ActionStatus getImportStatus() {
		return importStatus;
	}
	public void setImportStatus(ActionStatus importStatus) {
		this.importStatus = importStatus;
	}
	public List<ActionResult> getResults() {
		return results;
	}
	public void setResults(List<ActionResult> results) {
		this.results = results;
	}
	List<ActionResult> results = new ArrayList<ActionResult>();

}
