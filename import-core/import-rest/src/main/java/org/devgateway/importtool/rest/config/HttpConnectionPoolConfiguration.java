package org.devgateway.importtool.rest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "httppool")
public class HttpConnectionPoolConfiguration {
    private Integer maxPerRoute;
    private Integer maxTotal;
    private Integer connectionRequestTimeout;
    private Integer connectTimeout;
    private Integer socketTimeout;

    public Integer getMaxPerRoute() {
        return maxPerRoute;
    }

    public void setMaxPerRoute(Integer maxPerRoute) {
        this.maxPerRoute = maxPerRoute;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Integer getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(Integer connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }
}
