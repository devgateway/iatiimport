package org.devgateway.importtool.services.processor.destination;

import org.springframework.web.client.RestTemplate;

public class AmpResourceFieldProvider extends AmpFieldProvider {

    public static final String RESOURCE_FIELDS_ENDPOINT = "/rest/resource/fields";

    public AmpResourceFieldProvider(String baseURL, RestTemplate restTemplate) {
        super(baseURL, restTemplate);
    }

    public String getFieldProviderEndpoint() {
        return RESOURCE_FIELDS_ENDPOINT;
    }

}
