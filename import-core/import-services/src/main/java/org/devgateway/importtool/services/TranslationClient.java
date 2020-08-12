package org.devgateway.importtool.services;

import static org.devgateway.importtool.services.processor.destination.AmpStaticProcessorConstants.BASEURL;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.devgateway.importtool.services.dto.JsonBean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @author Octavian Ciubotaru
 */
public class TranslationClient {

    private static final long MAX_WAIT = TimeUnit.MINUTES.toMillis(10);
    private static final long POLL_WAIT = TimeUnit.SECONDS.toMillis(1);

    private final RestTemplate restTemplate;

    private final String username;
    private final String password;

    public TranslationClient(String username, String password) {
        this.username = username;
        this.password = password;

        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public static class TranslationException extends RuntimeException {

        public TranslationException(String s) {
            super(s);
        }

        public TranslationException(String s, Throwable throwable) {
            super(s, throwable);
        }
    }

    public static class TranslationRequest {

        private final String sourceLanguageCode;

        private final String targetLanguageCode;

        private final Collection<String> contents;

        public TranslationRequest(String sourceLanguageCode, String targetLanguageCode,
                Collection<String> contents) {
            this.sourceLanguageCode = sourceLanguageCode;
            this.targetLanguageCode = targetLanguageCode;
            this.contents = contents;
        }

        public String getSourceLanguageCode() {
            return sourceLanguageCode;
        }

        public String getTargetLanguageCode() {
            return targetLanguageCode;
        }

        public Collection<String> getContents() {
            return contents;
        }
    }

    public static class TranslationOperation {

        private boolean done;

        private String error;

        private Map<String, String> result;

        public boolean isDone() {
            return done;
        }

        public void setDone(boolean done) {
            this.done = done;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public Map<String, String> getResult() {
            return result;
        }

        public void setResult(Map<String, String> result) {
            this.result = result;
        }
    }

    public static class TranslationConfig {

        private Boolean enabled;

        private Integer maxChars;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Integer getMaxChars() {
            return maxChars;
        }

        public void setMaxChars(Integer maxChars) {
            this.maxChars = maxChars;
        }
    }

    /**
     *
     * @param texts
     * @throws TranslationException
     * @return
     */
    public Map<String, String> translate(Collection<String> texts, String sourceLanguage, String targetLanguage) {
        try {
            signInIfNecessary();

            TranslationRequest request = new TranslationRequest(sourceLanguage, targetLanguage, texts);

            String translateUrl = BASEURL + "/rest/machine-translation/translate";
            String uuid = restTemplate.postForObject(translateUrl, request, String.class);

            String opUrl = BASEURL + "/rest/machine-translation/operation/" + uuid;

            for (int t = 0; t < MAX_WAIT; t += POLL_WAIT) {
                TranslationOperation op = restTemplate.getForObject(opUrl, TranslationOperation.class);

                if (op.isDone()) {
                    if (op.getError() != null) {
                        throw new TranslationException("Translation failed. API returned error: " + op.getError());
                    } else {
                        return op.getResult();
                    }
                }

                try {
                    Thread.sleep(POLL_WAIT);
                } catch (InterruptedException e) {
                    throw new TranslationException("Translation failed. Thread was interrupted.", e);
                }
            }

            throw new TranslationException("Couldn't obtain a response after " + Duration.ofMillis(MAX_WAIT));
        } catch (RestClientException e) {
            throw new TranslationException("Translation failed.", e);
        }
    }

    private void signInIfNecessary() {
        try {
            restTemplate.getForObject(BASEURL + "/rest/security/user", JsonBean.class);
        } catch (HttpClientErrorException.Unauthorized e) {
            Map<String, String> loginReq = new HashMap<>();
            loginReq.put("username", username);
            loginReq.put("password", password);
            restTemplate.postForObject(BASEURL + "/rest/security/user", loginReq, JsonBean.class);
        }
    }

    public TranslationConfig getConfig() {
        try {
            signInIfNecessary();

            return restTemplate.getForObject(BASEURL + "/rest/machine-translation/config", TranslationConfig.class);
        } catch (RestClientException e) {
            throw new TranslationException("Failed to retrieve the config.", e);
        }
    }
}
