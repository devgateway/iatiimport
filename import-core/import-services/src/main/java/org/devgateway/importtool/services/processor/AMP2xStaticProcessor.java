package org.devgateway.importtool.services.processor;

import org.devgateway.importtool.services.processor.helper.JsonBean;

public class AMP2xStaticProcessor extends AMPStaticProcessor {
    public AMP2xStaticProcessor(String authenticationToken) {
        super(authenticationToken);
    }

    protected void addAditionalDonorInformation(JsonBean donorRole) {
        donorRole.set("role", 1);
    }
}