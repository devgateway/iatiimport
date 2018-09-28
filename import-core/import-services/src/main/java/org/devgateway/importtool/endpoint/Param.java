package org.devgateway.importtool.endpoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Param {
    private String paramName;
    private List<String> paramValue;
    private String parameterOperator;

    public Param() {

    }

    public Param(String paramName, String paramValue) {
        this(paramName, paramValue, "=");
    }

    public Param(String paramName, String paramValue, String parameterOperator) {
        this(paramName, new ArrayList<>(Arrays.asList(paramValue)), parameterOperator);
    }

    public Param(String paramName, List<String> paramValue) {
        this(paramName, paramValue, "=");
    }

    public Param(String paramName, List<String> paramValue, String parameterOperator) {
        this.paramName = paramName;
        this.paramValue = paramValue;
        this.parameterOperator = parameterOperator;
    }


    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }
    public String getParamValueEncoded() {
        StringBuffer p = new StringBuffer();
        Optional.ofNullable(paramValue).orElse(Collections.emptyList()).stream().forEach(param -> {
            p.append(param);
            p.append("|" );
        });
        if(p.length()>0){
            p.setLength(p.length() - 1);
        }
        return p.toString();
    }

    public List<String> getParamValue() {
        return paramValue;
    }

    public void setParamValue(List<String> paramValue) {
        this.paramValue = paramValue;
    }

    public String getParameterOperator() {
        return parameterOperator;
    }

    public void setParameterOperator(String parameterOperator) {
        this.parameterOperator = parameterOperator;
    }
}
