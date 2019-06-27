package org.devgateway.importtool.services.processor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("AMP")
public class AmpStaticProcessorConfig implements DestinationProcessorConfiguration{

    @Value("${AMPStaticProcessor.canUpgradeToDraft:true}")
    //Can upgrade to draft default value is true in case its not specified in application.properties
    private Boolean canUpgradeToDraft;

    //Is draft default value false in case its not specified in application.properties
    @Value("${AMPStaticProcessor.isDraft:false}")
    private Boolean isDraft;

    @Value("${app.version}")
    private String appversion;
    
    @Value("${app.name}")
    private String appName;

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

    public String getAppversion() {
        return appversion;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
