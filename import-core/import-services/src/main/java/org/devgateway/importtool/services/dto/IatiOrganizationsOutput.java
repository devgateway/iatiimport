package org.devgateway.importtool.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class IatiOrganizationsOutput {

    public List<ResultEntry> getResult() {
        return result;
    }

    public void setResult(List<ResultEntry> result) {
        this.result = result;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @JsonProperty("result") private List<ResultEntry> result;
    @JsonProperty("help") private String help;
    @JsonProperty("success") private Boolean success;
}
