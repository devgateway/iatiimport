package org.devgateway.importtool.services.processor.helper.interceptors;

import java.io.IOException;

import org.devgateway.importtool.services.processor.destination.AmpStaticProcessorConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class CookieHeaderInterceptor extends AbstractHttpInterceptor implements ClientHttpRequestInterceptor {

    private String ampJSessionId;
    
    public CookieHeaderInterceptor(String ampJSessionId) {
        this.ampJSessionId = ampJSessionId;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
    
        String jSessionCookie = AmpStaticProcessorConstants.SESSION_COOKIE_NAME + "=" + ampJSessionId;
        request.getHeaders().add(HttpHeaders.COOKIE, jSessionCookie);
        
        ClientHttpResponse response = execution.execute(request, body);

        return response;
    }

}
