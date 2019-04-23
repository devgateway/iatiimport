package org.devgateway.importtool.services.processor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("AMP")
public class AmpStaticProcessorConfig implements DestinationProcessorConfiguration{

    @Value("${AMPStaticProcessor.canUpgradeToDraft:true}")
    //Can upgarde to draft default value is true in case its not specified in application.properties
    private Boolean canUpgradeToDraft;
    //Is draft default value false in case its not specified in application.properties
    @Value("${AMPStaticProcessor.isDraft:false}")
    private Boolean isDraft;

    private String authenticationToken;

    private String processorVersion;

    public AmpStaticProcessorConfig(){

    }

    public Boolean getCanUpgradeToDraft() {
        return canUpgradeToDraft;
    }

    public void setCanUpgradeToDraft(Boolean canUpgradeToDraft) {
        this.canUpgradeToDraft = canUpgradeToDraft;
    }

    public Boolean getDraft() {
        return isDraft;
    }

    public void setDraft(Boolean draft) {
        isDraft = draft;
    }

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public String getProcessorVersion() {
        return processorVersion;
    }

    @Override
    public void setProcessorVersion(String processorVersion) {
        this.processorVersion = processorVersion;
    }
}
