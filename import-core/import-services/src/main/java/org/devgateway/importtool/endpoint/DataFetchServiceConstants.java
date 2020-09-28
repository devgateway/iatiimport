package org.devgateway.importtool.endpoint;

import java.util.ArrayList;
import java.util.List;

public class DataFetchServiceConstants {
    public static final String IATI_DATASTORE_DEFAULT_URL = "https://iatidatastore.iatistandard.org/search/activity";
    public static final String IATI_ORGANIZATIONS_DEFAULT_URL = "https://iatiregistry.org/api/action/organization_list?" +
            "all_fields=true&include_extras=true";
    public static final String PARAMETER_REPLACE_VALUE = "$";
    public static final String REPORTING_ORGANISATION_PARAMETER = "reporting_org_ref:(" + PARAMETER_REPLACE_VALUE + ")";
    public static final String RECIPIENT_COUNTRY_PARAMETER = "recipient_country_code:(" + PARAMETER_REPLACE_VALUE + ")";
    public static final String IATI_IDENTIFIER_PARAMETER = "iati_identifier:(" + PARAMETER_REPLACE_VALUE + ")";
    public static final String PARAM_AND_VALUE = " AND ";
    public static final String IATI_EXTRA_VERSION = "iati-extra:version";
    public static final String ACTIVITIES_FILES_STORE = "ActivityFetcher.fileStore";

    public static List<Param> getCommonParams(String reportingOrg, String defaultCountry) {
        List<Param> params = new ArrayList<>();
        params.add(new Param(DataFetchServiceConstants.RECIPIENT_COUNTRY_PARAMETER, defaultCountry));
        params.add(new Param(DataFetchServiceConstants.REPORTING_ORGANISATION_PARAMETER, reportingOrg));
        return params;
    }
}
