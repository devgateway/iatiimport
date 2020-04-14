package org.devgateway.importtool.services.processor.destination;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.services.dto.SerializationHelper;
import org.devgateway.importtool.services.processor.dto.APIField;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AmpFieldProvider {

    private Log log = LogFactory.getLog(AmpFieldProvider.class);

    protected final String baseURL;
    protected final RestTemplate restTemplate;

    public AmpFieldProvider(String baseURL, RestTemplate restTemplate) {
        this.baseURL = baseURL;
        this.restTemplate = restTemplate;
    }

    protected List<APIField> fieldDefinitions;

    List<String> enabledFieldsPlain;

    public List<APIField> getFieldDefinitions() {
        return fieldDefinitions;
    }

    public void reset() {
        fieldDefinitions = new ArrayList<>();
    }

    public void loadFields() {
        String result = restTemplate.getForObject(baseURL + getFieldProviderEndpoint(), String.class);
        try {
            this.fieldDefinitions = SerializationHelper.getDefaultMapper().
                    readValue(result, new TypeReference<List<APIField>>() {
                    });
        } catch (IOException ex) {
            log.error("cannot deserialize fields definition", ex);
            //TODO Again its not good to Swallow the exception but a general error handling should be provided
            //TODO and improved
        }
    }

    public List<String> getEnabledFieldsPlain() {
        if (enabledFieldsPlain == null) {
            enabledFieldsPlain = new ArrayList<>();
            populateEnabledFieldsPlain();
        }
        return enabledFieldsPlain;
    }

    private void populateEnabledFieldsPlain() {
        populateEnabledFieldsPlain(enabledFieldsPlain, "", fieldDefinitions);
    }

    private void populateEnabledFieldsPlain(List<String> enabledFields, String parent, List<APIField> children) {
        for (APIField af : children) {
            String fieldName = parent + af.getFieldName();
            enabledFields.add(fieldName);
            if (af.getChildren() != null && af.getChildren().size() > 0) {
                populateEnabledFieldsPlain(enabledFields, fieldName + "~", af.getChildren());
            }
        }
    }

    /**
     * this method with ~
     *
     * @param path
     * @return
     */
    public APIField getFieldProps(String path) {
        if (path == null || path.trim().length() == 0) {
            return null;
        } else {
            return getFieldsDefinition(StringUtils.split(path, "~"));
        }
    }

    private APIField getFieldsDefinition(String... path) {
        if (path == null || path.length == 0) {
            return null;
        } else {
            return getFieldsDefinition(fieldDefinitions, path);
        }
    }

    public boolean existsField(String fieldName) {
        return getFieldProps(fieldName) != null;
    }

    private APIField getFieldsDefinition(List<APIField> apFieldsDefinitions, String... path) {
        APIField apiField = apFieldsDefinitions.stream().filter(f -> f.getFieldName().equals(path[0])).findAny().orElse(null);
        if (path.length == 1 || apiField == null) {
            return apiField;
        } else {
            if (apiField.getChildren() != null) {
                return getFieldsDefinition(apiField.getChildren(), Arrays.copyOfRange(path, 1, path.length));
            } else {
                return null;
            }
        }
    }

    public abstract String getFieldProviderEndpoint();

}
