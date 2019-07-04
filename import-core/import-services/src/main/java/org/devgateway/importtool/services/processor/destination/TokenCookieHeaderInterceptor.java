package org.devgateway.importtool.services.processor.destination;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

public class TokenCookieHeaderInterceptor implements ClientHttpRequestInterceptor {

    private String token;
    private String cookie;

    public TokenCookieHeaderInterceptor(String token) {
        this.token = token;
    }


    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
        requestWrapper.getHeaders().add(AmpStaticProcessorConstants.X_AUTH_HEADER, token);
        if (cookie != null) {
            request.getHeaders().add(HttpHeaders.COOKIE, cookie);
        }
        ClientHttpResponse response = execution.execute(requestWrapper, body);

        List<String> cookies =
                response.getHeaders().get(HttpHeaders.SET_COOKIE);

        if(cookies != null) {
            cookies.stream().filter(
                    pCookie -> pCookie.startsWith(AmpStaticProcessorConstants.SESSION_COOKIE_NAME)).
                    forEach(this::setCookie);
        }
        return response;
    }

}
