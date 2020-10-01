package org.devgateway.importtool.services.processor.destination;

import org.devgateway.importtool.services.processor.dto.APIField;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.devgateway.importtool.services.processor.destination.AmpStaticProcessorConstants.AMP_ADJUSTMENT_TYPE;
import static org.devgateway.importtool.services.processor.destination.AmpStaticProcessorConstants.AMP_FUNDINGS;

public class AmpActivityFieldProvider extends AmpFieldProvider {

    public static final String ACTIVITY_FIELDS_ENDPOINT = "/rest/activity/fields";

    private Map<String, Boolean> areTransactionDatesTimeStamps = new HashMap<>();

    public AmpActivityFieldProvider(final String baseURL, final RestTemplate restTemplate) {
        super(baseURL, restTemplate);
    }

    public void reset() {
        super.reset();
        areTransactionDatesTimeStamps = new HashMap<>();
    }

    public String getFieldProviderEndpoint() {
        return ACTIVITY_FIELDS_ENDPOINT;
    }

    public String getCurrencyPath() {
        //TODO find a way to get the APIField iterating field props checking ancestors
        return getEnabledFieldsPlain().stream().
                filter(fieldName -> fieldName.startsWith("fundings~") && fieldName.endsWith("~currency"))
                .findFirst().get();
    }

    public String getAdjustmentTypePath() {
        //TODO find a way to get the APIField iterating field props checking ancestors
        return getEnabledFieldsPlain().stream().
                filter(fieldName -> fieldName.startsWith(AMP_FUNDINGS + "~") && fieldName.endsWith("~" + AMP_ADJUSTMENT_TYPE))
                .findFirst().orElseGet(null);
    }

    public boolean isTransactionDateTimeStamp(String path) {
        if (areTransactionDatesTimeStamps.get(path) == null) {
            Boolean isTimeStamp = Boolean.FALSE;
            APIField af = getFieldProps(path);
            if (af == null && af.getApiType().getFieldType().equals("timestamp")) {
                isTimeStamp = Boolean.TRUE;
            }
            areTransactionDatesTimeStamps.put(path, isTimeStamp);
        }
        return areTransactionDatesTimeStamps.get(path);
    }
}
