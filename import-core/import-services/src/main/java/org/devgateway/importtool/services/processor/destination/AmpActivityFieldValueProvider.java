package org.devgateway.importtool.services.processor.destination;

import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.devgateway.importtool.services.processor.destination.AmpStaticProcessorConstants.*;

public class AmpActivityFieldValueProvider extends AmpFieldValueProvider {

    public static final List<String> ACTIVITY_FIELD_VALUES_TO_FETCH = new ArrayList<>(Arrays.asList(AMP_ACTIVITY_STATUS,
            AMP_A_C_CHAPTER, AMP_FUNDINGS_TYPE_OF_ASSISTANCE, AMP_FUNDINGS_FINANCING_INSTRUMENT,
            AMP_PRIMARY_SECTORS_SECTOR, AMP_SECONDARY_SECTORS_SECTOR, AMP_TERTIARY_SECTORS_SECTOR,LOCATIONS_LOCATION ,
            AMP_FUNDINGS_DONOR_ORGANIZATION_ID, AMP_FUNDINGS_COMMITMENTS_ADJUSTMENT_TYPE,
            AMP_FUNDINGS_DISBURSEMENTS_ADJUSTMENT_TYPE, AMP_FUNDINGS_EXPENDITURES_ADJUSTMENT_TYPE,
            AMP_FUNDINGS_COMMITMENTS_CURRENCY, AMP_FUNDINGS_DISBURSEMENTS_CURRENCY, AMP_FUNDINGS_CEXPENDITURES_CURRENCY,
            AMP_IMPLEMENTATION_LEVEL));

    public static final String ACTIVITY_FIELD_VALUES_ENDPOINT = "/rest/activity/field/values";

    public AmpActivityFieldValueProvider(final String baseURL, final RestTemplate restTemplate) {
        super(baseURL, restTemplate);
    }

    @Override
    protected String getFieldValuesEndpoint() {
        return ACTIVITY_FIELD_VALUES_ENDPOINT;
    }

    @Override
    protected List<String> getFieldsToFetch() {
        return ACTIVITY_FIELD_VALUES_TO_FETCH;
    }
}
