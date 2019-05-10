package org.devgateway.importtool.services.processor.helper.interceptors;

import org.apache.commons.lang.SystemUtils;
import org.apache.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

import java.io.IOException;

public class UserAgentInterceptor extends AbstractHttpInterceptor implements ClientHttpRequestInterceptor {

    String appVersion;
    String appName;

    public UserAgentInterceptor(String appName, String appVersion){
        this.appVersion = appVersion;
        this.appName = appName;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        HttpRequestWrapper requestWrapper = addHeader(request, HttpHeaders.USER_AGENT, buildUserAgent());
        return execution.execute(requestWrapper, body);
    }

    private String buildUserAgent() {

        return appName + "/" + appVersion + " ("+ SystemUtils.OS_NAME+ "; "+SystemUtils.OS_ARCH +"; "
                + SystemUtils.OS_VERSION + ")";
    }
}
