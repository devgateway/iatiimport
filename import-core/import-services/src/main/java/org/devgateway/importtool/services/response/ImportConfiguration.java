package org.devgateway.importtool.services.response;

import org.devgateway.importtool.security.ImportSessionToken;
import org.devgateway.importtool.services.processor.helper.Field;

import java.util.List;

public class ImportConfiguration {
    private org.devgateway.importtool.security.ImportSessionToken importSessionToken;
    private List<Field> sourceProcessorFields;

    public ImportConfiguration(ImportSessionToken pImportSessionToken, List<Field> pSourceProcessorFields)  {
    this.importSessionToken = pImportSessionToken;
        this.sourceProcessorFields = pSourceProcessorFields;
    }

    public ImportSessionToken getImportSessionToken() {
        return importSessionToken;
    }

    public void setImportSessionToken(ImportSessionToken pImportSessionToken) {
        this.importSessionToken = pImportSessionToken;
    }

    public List<Field> getSourceProcessorFields() {
        return sourceProcessorFields;
    }

    public void setSourceProcessorFields(List<Field> sourceProcessorFields) {
        this.sourceProcessorFields = sourceProcessorFields;
    }
}
