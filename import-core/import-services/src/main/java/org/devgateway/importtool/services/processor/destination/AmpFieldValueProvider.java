package org.devgateway.importtool.services.processor.destination;

import org.devgateway.importtool.services.processor.dto.PossibleValue;
import org.devgateway.importtool.services.processor.helper.FieldValue;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AmpFieldValueProvider {

    protected Map<String, List<FieldValue>> fieldValuesMap = new HashMap<>();

    private final String baseURL;
    private final RestTemplate restTemplate;

    public AmpFieldValueProvider(String baseURL, RestTemplate restTemplate) {
        this.baseURL = baseURL;
        this.restTemplate = restTemplate;
    }

    public void reset() {
        fieldValuesMap = new HashMap<>();
    }

    public void loadFieldValues(List<String> enabledFields) {
        // TODO only get codelist that we need
        // TODO Refactor to use an object instead of iterating JsonNodes\
        ParameterizedTypeReference<Map<String, List<PossibleValue>>> responseType =
                new ParameterizedTypeReference<Map<String, List<PossibleValue>>>() {
                };
        List<String> fieldsToFetch = (new ArrayList(getFieldsToFetch()));
        fieldsToFetch.retainAll(enabledFields);
        HttpEntity httpEntity = new HttpEntity(fieldsToFetch);
        ResponseEntity<Map<String, List<PossibleValue>>> response =
                restTemplate.exchange(baseURL + getFieldValuesEndpoint(), HttpMethod.POST, httpEntity, responseType);

        response.getBody().forEach((keyName, lPossibleValues) -> {
            fieldValuesMap.put(keyName, getPossibleValuesFromNode(lPossibleValues));
        });
    }

    private List<FieldValue> getPossibleValuesFromNode(List<PossibleValue> lPossibleValues) {
        List<FieldValue> possibleValues = new ArrayList<>();
        int index = 0;
        for(PossibleValue p:lPossibleValues){

            FieldValue fv = new FieldValue();
            fv.setIndex(index++);
            fv.setCode(p.getId().toString());
            fv.setValue(p.getValue());
            //TODO THIS IS A WORKAROUND TO BE FIXED BEFORE THE RELEASE
            fv.setProperties(p.getExtraInfo());
            fv.setTranslatedValue(p.getTranslatedValues());
            possibleValues.add(fv);
        }
        return possibleValues;
    }

    public List<FieldValue> getPossibleValues(String codeListName) {
        return fieldValuesMap.get(codeListName);
    }

    protected abstract String getFieldValuesEndpoint();

    protected abstract List<String> getFieldsToFetch();

}
