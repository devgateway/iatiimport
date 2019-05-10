package org.devgateway.importtool.services.processor.helper.interceptors;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.support.HttpRequestWrapper;

public class AbstractHttpInterceptor {

    protected HttpRequestWrapper addHeader(HttpRequest request, String headerName, String headerValue) {
        HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
        //since we are pooling the request can already contain the header
        requestWrapper.getHeaders().remove(headerName);
        requestWrapper.getHeaders().add(headerName, headerValue);
        return requestWrapper;
    }
}
