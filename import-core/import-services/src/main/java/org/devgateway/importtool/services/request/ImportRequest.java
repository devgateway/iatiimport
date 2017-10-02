package org.devgateway.importtool.services.request;

import org.devgateway.importtool.services.processor.helper.ImportOption;

public class ImportRequest {
	private ImportOption importOption;

	public ImportOption getImportOption() {
		return importOption;
	}

	public void setImportOption(ImportOption importOption) {
		this.importOption = importOption;
	}
}
