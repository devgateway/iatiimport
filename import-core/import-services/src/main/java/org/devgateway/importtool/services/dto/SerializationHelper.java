package org.devgateway.importtool.services.dto;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializationHelper {

    public static ObjectMapper getDefaultMapper() {
        ObjectMapper mapper11 = new ObjectMapper();
        mapper11.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
        mapper11.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper11;
    }
}
