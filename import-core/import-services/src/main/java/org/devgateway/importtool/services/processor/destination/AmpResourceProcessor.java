package org.devgateway.importtool.services.processor.destination;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.services.dto.JsonBean;
import org.devgateway.importtool.services.dto.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Processor used to create resources in AMP
 * @author Viorel Chihai
 */
public class AmpResourceProcessor {

    private static final Log log = LogFactory.getLog(AmpResourceProcessor.class);

    final static private String RESOURCE_ENDPOINT = "rest/resource";
    final static private String SECURITY_LAYOUT_ENDPOINT = "rest/security/layout";

    final static private Long RESOURCE_WEB_LINK_TYPE = 0L;

    final private String baseURL;
    final private RestTemplate restTemplate;

    private String userEmail;

    private Long teamId;

    public AmpResourceProcessor(String baseURL, RestTemplate restTemplate) {
        this.baseURL = baseURL;
        this.restTemplate = restTemplate;
        init();
    }

    private void init() {
        JsonBean layout = restTemplate.getForObject(baseURL + SECURITY_LAYOUT_ENDPOINT, JsonBean.class);
        String workspaceId = layout.getString("workspaceId");
        if (StringUtils.isBlank(workspaceId)) {
            throw new RuntimeException("There is no workspace attached to the user session");
        }
        teamId =  Long.parseLong(layout.getString("workspaceId"));
        userEmail = layout.getString("email");
    }

    public Resource createResource(Map<String, String> value, Integer documentCategoryId) {
        JsonBean documentEntity = new JsonBean();

        documentEntity.set("title", value.get("title"));
        documentEntity.set("web_link", value.get("url"));
        documentEntity.set("creator_email", userEmail);
        documentEntity.set("team", teamId);
        documentEntity.set("resource_type", RESOURCE_WEB_LINK_TYPE);
        documentEntity.set("private", true);

        if (documentCategoryId != null) {
            documentEntity.set("type", documentCategoryId);
        }

        HttpEntity httpEntity = new HttpEntity(documentEntity);

        try {
            ResponseEntity<JsonBean> response =
                    restTemplate.exchange(baseURL + RESOURCE_ENDPOINT, HttpMethod.PUT, httpEntity, JsonBean.class);

            if (response.getBody().getString("error") == null) {
                String uuid = response.getBody().getString("uuid");
                log.info("A resource was created in amp: " + uuid);

                return new Resource(uuid);
            } else {
                log.error("Failed to create resource in AMP: " + response.getBody().getString("error"));
            }
        } catch (RestClientException ex) {
            log.error("Failed to create resource in AMP", ex);
        }

        return null;
    }
}
