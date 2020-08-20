package org.devgateway.importtool.services;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Octavian Ciubotaru
 */
@ConfigurationProperties("project.translation")
public class ProjectTranslationSettings {

    private boolean enabled = false;

    /**
     * Credentials for AMP user used to access translations API.
     */
    private String username;
    private String password;

    private String sourceLanguage;
    private String destinationLanguage;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getDestinationLanguage() {
        return destinationLanguage;
    }

    public void setDestinationLanguage(String destinationLanguage) {
        this.destinationLanguage = destinationLanguage;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
