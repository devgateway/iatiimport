package org.devgateway.importtool.endpoint;

import java.util.ArrayList;
import java.util.List;

public class DataFetchServiceConstants {
    //this default url should come from the database
    public static final String IATI_DATASTORE_DEFAULT_URL ="http://datastore.iatistandard.org/api/1/access/activity.xml";
    public static final String IATI_ORGANIZATIONS_DEFAULT_URL = "https://iatiregistry.org/api/action/organization_list?" +
            "all_fields=true&include_extras=true";
    public static final String RECIPIENT_COUNTRY_PARAMETER="recipient-country";
    public static final String REPORTING_ORGANISATION_PARAMETER="reporting-org";
    public static final String IATI_IDENTIFIER_PARAMETER="iati-identifier";
    public static final String IATI_EXTRA_VERSION ="iati-extra:version";
    public static final String ORGANIZATIONS_ENDPOINT="";
    public static List<Param> getCommonParams(String reportingOrg) {
        List<Param> params = new ArrayList<>();
        //SN should be taken from AMP
        params.add(new Param(DataFetchServiceConstants.RECIPIENT_COUNTRY_PARAMETER, "SN"));
        params.add(new Param(DataFetchServiceConstants.REPORTING_ORGANISATION_PARAMETER, reportingOrg));
        return params;
    }
}
