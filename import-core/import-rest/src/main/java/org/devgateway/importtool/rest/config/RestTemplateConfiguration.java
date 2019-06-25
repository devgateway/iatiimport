package org.devgateway.importtool.rest.config;

import org.apache.http.client.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {
    @Bean
    public RestTemplate restTemplate(HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new
                HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        //restTemplate.setErrorHandler(new CustomErrorResponseHandler());
        return restTemplate;
    }
}
