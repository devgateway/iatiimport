package org.devgateway.importtool.services.dto;

/**
 * Class used to insert resource objects in AMP
 */
public class Resource {

    private final static String DEFAULT_DOCUMENT_TYPE = "Related Documents";

    private final String uuid;

    public Resource(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getDocumentType() {
        return DEFAULT_DOCUMENT_TYPE;
    }
}
