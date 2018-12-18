package org.devgateway.importtool.services.processor.helper;

import java.util.Map;

public class ProviderOganizationField extends Field {

    public ProviderOganizationField(String displayName, String fieldName, FieldType type, boolean isMappable,
                                    Map<String, String> description, Map<String, String> labels) {
        super(displayName, fieldName, type, isMappable, description,  labels);
    }

    @Override
    public String getXpathFilterCondition(Boolean isFormDataStore) {
        String condition =  " (@ref) ";
        if(isFormDataStore) {
            return condition +"and ";
        }else{
            return "[" + condition + "]";
        }
    }

}
