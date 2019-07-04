package org.devgateway.importtool.services.request;

import org.devgateway.importtool.services.processor.helper.ImportOption;

public class ImportRequest {
	private ImportOption importOption;
	private Boolean disasterResponse;

	public ImportOption getImportOption() {
		return importOption;
	}

	public void setImportOption(ImportOption importOption) {
		this.importOption = importOption;
	}

    public Boolean getDisasterResponse() {
        return disasterResponse;
    }

    public void setDisasterResponse(Boolean disasterResponse) {
        this.disasterResponse = disasterResponse;
    }
}
