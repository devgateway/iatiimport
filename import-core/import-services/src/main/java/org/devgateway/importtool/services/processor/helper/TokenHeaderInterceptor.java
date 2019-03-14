package org.devgateway.importtool.services.processor.helper;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

public class TokenHeaderInterceptor implements ClientHttpRequestInterceptor {

	private final String token;
	
	public TokenHeaderInterceptor(String token) {
		this.token = token;
	}
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body,
			ClientHttpRequestExecution execution) throws IOException {

	    HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
	    //since we are pooling the request can already contain the header
		requestWrapper.getHeaders().remove("X-Auth-Token");
	    requestWrapper.getHeaders().add("X-Auth-Token", token);
		return execution.execute(requestWrapper, body);
	}

}
