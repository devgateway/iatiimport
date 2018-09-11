package org.devgateway.importtool.services.processor.helper;

import java.util.Map;

public class ProviderOganizationField extends Field {

    public ProviderOganizationField(String displayName, String fieldName, FieldType type, boolean isMappable,
                                    Map<String, String> description) {
        super(displayName, fieldName, type, isMappable, description);
    }

    @Override
    public String getXpathFilterCondition(Boolean isFormDataStore) {
        return " not(@provider-activity-id) " + (isFormDataStore ? "and " : "");
    }

}
