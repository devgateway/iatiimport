package org.devgateway.importtool.services.processor.destination;

import org.devgateway.importtool.services.processor.helper.Constants;

import java.util.HashMap;
import java.util.Map;

public class AmpStaticProcessorConstants {
    public static final String BASEURL_PROPERTY = "AMPStaticProcessor.baseURL";
    public static final String BASEURL_DEFAULT_VALUE = "http://localhost:8081";
    public static final String AMP_IATI_ID_FIELD_PROPERTY = "AMPStaticProcessor.ampIatiIdField";
    public static final String AMP_IATI_ID_FIELD_DEFAULT_VALUE = "project_code";
    public static final String DEFAULT_LANGUAGE_CODE = "en";
    public static final String AMP_IMPLEMENTATION_LEVEL_ID_FIELD_PROPERTY = "AMPStaticProcessor.implementationLevel";
    public static final Integer AMP_IMPLEMENTATION_LEVEL_ID_DEFAULT_VALUE = 70; //Coming form common AMP configuration
    public static final String 	DEFAULT_TITLE_FIELD = "project_title";
    public static final String SESSION_COOKIE_NAME = "JSESSIONID";
    public static final String X_AUTH_HEADER = "X-Auth-Token";

    public static final Map<String, String> tTNameSourceMap = new HashMap<String, String>() {
        {
            put("AC", Constants.COMMITMENTS);
            put("AD", Constants.DISBURSEMENTS);
            put("AE", Constants.EXPENDITURES);
            put("PC", Constants.COMMITMENTS);
            put("PD", Constants.DISBURSEMENTS);
            put("PE", Constants.EXPENDITURES);
        }
    };
    public static final Map<String, String> aTNameDestinationMap = new HashMap<String, String>() {
        {
            put("AC", Constants.ACTUAL);
            put("AD", Constants.ACTUAL);
            put("AE", Constants.ACTUAL);
            put("PC", Constants.PLANNED);
            put("PD", Constants.PLANNED);
            put("PE", Constants.PLANNED);
        }
    };
    //end points
    public static final String FIELDS_ENDPOINT = "/rest/activity/fields";
    public static final String ALL_FIELDS_ENDPOINT = "/rest/activity/field/values";
    public static final String TRANSLATIONS_END_POINT = "/rest/translations/translate?translations=";
    public static final String DOCUMENTS_END_POINT = "/rest/activity/projects";


}
