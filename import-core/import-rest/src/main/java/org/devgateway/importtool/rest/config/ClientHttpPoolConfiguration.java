package org.devgateway.importtool.rest.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientHttpPoolConfiguration {

    @Autowired
    private HttpConnectionPoolConfiguration poolConfiguration;
    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager result = new PoolingHttpClientConnectionManager();
        result.setDefaultMaxPerRoute(poolConfiguration.getMaxPerRoute());
        result.setMaxTotal(poolConfiguration.getMaxTotal());
        

        return result;
    }
    @Bean
    public RequestConfig requestConfig() {
        RequestConfig result = RequestConfig.custom()
                /*.setConnectionRequestTimeout(poolConfiguration.getConnectionRequestTimeout())
                .setConnectTimeout(poolConfiguration.getConnectTimeout())
                .setSocketTimeout(poolConfiguration.getSocketTimeout())*/
                .build();
        return result;
    }
    @Bean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager,
                                          RequestConfig requestConfig) {
        CloseableHttpClient result = HttpClientBuilder
                .create()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
        return result;
    }
}
