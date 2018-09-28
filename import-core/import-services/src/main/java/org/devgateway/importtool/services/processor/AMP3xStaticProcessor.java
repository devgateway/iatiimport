package org.devgateway.importtool.services.processor;

import org.devgateway.importtool.services.processor.helper.JsonBean;

public class AMP3xStaticProcessor extends AMPStaticProcessor {

    public AMP3xStaticProcessor(String authenticationToken) {
        super(authenticationToken);
    }
    //so far Amp3x does not requires aditional donor information
    protected void addAditionalDonorInformation(JsonBean donorRole) {
    }
}
