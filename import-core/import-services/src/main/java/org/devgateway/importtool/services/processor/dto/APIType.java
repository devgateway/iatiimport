package org.devgateway.importtool.services.processor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class coming from AMP authored by Octavian Ciubotaru
 * We can proabbly make it a separate jar if needed to avoid copy & pasting
 */

public class APIType {
    @JsonProperty(JsonConstants.FIELD_TYPE)
    private final String fieldType;


    @JsonProperty(JsonConstants.ITEM_TYPE)
    private final String itemType;

    public APIType() {
        this.fieldType = null;
        this.itemType = null;
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getItemType() {
        return itemType;
    }
}
