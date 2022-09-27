package org.devgateway.importtool.services;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Validated
@ConfigurationProperties("iati.datastore")
public class IATIDatastoreProperties {

    private String url = "https://api.iatistandard.org/datastore";

    @NotEmpty(message = "API Key is required. See more at https://developer.iatistandard.org/.")
    private String apiKey;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
