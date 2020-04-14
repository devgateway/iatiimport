package org.devgateway.importtool.services.processor.destination;

import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.devgateway.importtool.services.processor.destination.AmpStaticProcessorConstants.RESOURCE_TYPE;

public class AmpResourceFieldValueProvider extends AmpFieldValueProvider {

    public static final List<String> RESOURCE_FIELD_VALUES_TO_FETCH = new ArrayList<>(Arrays.asList(RESOURCE_TYPE));

    public static final String RESOURCE_FIELD_VALUES_ENDPOINT = "/rest/resource/field/values";

    public AmpResourceFieldValueProvider(final String baseURL, final RestTemplate restTemplate) {
        super(baseURL, restTemplate);
    }

    @Override
    protected String getFieldValuesEndpoint() {
        return RESOURCE_FIELD_VALUES_ENDPOINT;
    }

    @Override
    protected List<String> getFieldsToFetch() {
        return RESOURCE_FIELD_VALUES_TO_FETCH;
    }
}
