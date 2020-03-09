package org.devgateway.importtool.services.processor.destination;

import org.devgateway.importtool.services.processor.helper.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmpStaticProcessorConstants {
    public static final String BASEURL_PROPERTY = "AMPStaticProcessor.baseURL";
    public static final String BASEURL_DEFAULT_VALUE = "http://localhost:8081";
    public static final String AMP_IATI_ID_FIELD_PROPERTY = "AMPStaticProcessor.ampIatiIdField";
    public static final String AMP_IATI_ID_FIELD_DEFAULT_VALUE = "project_code";
    public static final String DEFAULT_LANGUAGE_CODE = "en";
    public static final String AMP_IMPLEMENTATION_LEVEL_ID_FIELD_PROPERTY = "AMPStaticProcessor.implementationLevel";
    public static final String 	DEFAULT_TITLE_FIELD = "project_title";
    public static final String SESSION_COOKIE_NAME = "JSESSIONID";

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
    public static final Map<String, String> TRANSACTION_DESTINATION_PATH = new HashMap<String, String>() {
        {
            put(Constants.COMMITMENTS, AMP_FUNDINGS_COMMITMENTS);
            put(Constants.DISBURSEMENTS, AMP_FUNDINGS_DISBURSEMENTS);
            put(Constants.EXPENDITURES, AMP_FUNDINGS_EXPENDITURES);
        }
    };
    public static final List<String> TRANSACTION_FIELDS = new ArrayList<>(Arrays.asList("Actual Commitments",
            "Actual Disbursements","Actual Expenditures","Planned Commitments", "Planned Disbursements",
            "Planned Expenditures"));
    public static final String AMP_ACTIVITY_STATUS = "activity_status";
    public static final String AMP_A_C_CHAPTER = "A C Chapter";
    public static final String AMP_FUNDINGS ="fundings";
    public static final String AMP_TYPE_OF_ASSISTANCE = "type_of_assistance";
    public static final String AMP_FUNDINGS_TYPE_OF_ASSISTANCE = AMP_FUNDINGS + "~"+ AMP_TYPE_OF_ASSISTANCE;
    public static final String AMP_FINANCING_INSTRUMENT = "financing_instrument";
    public static final String AMP_ADJUSTMENT_TYPE = "adjustment_type";
    public static final String AMP_TRANSACTION_TYPE = "transaction_type";
    public static final String AMP_IMPLEMENTATION_LEVEL = "implementation_level";

    public static final String AMP_PRIMARY_SECTORS = "primary_sectors";
    public static final String AMP_SECTOR = "sector";
    public static final String AMP_SECONDARY_SECTORS = "secondary_sectors";
    public static final String AMP_TERTIARY_SECTORS = "tertiary_sectors";
    public static final String AMP_CURRENCY = "currency";
    public static final String AMP_COMMITMENTS = "commitments";
    public static final String AMP_DISBURSEMENTS = "disbursements";
    public static final String AMP_EXPENDITURES = "expenditures";

    public static final String LOCATIONS_LOCATION = "locations~location";
    public static final String DONOR_ORGANIZATION_ID = "donor_organization_id";

    public static final String ACTIVITY_DOCUMENTS = "activity_documents";

    public static final String RESOURCE_TYPE = "type";

    public static final String AMP_FUNDINGS_FINANCING_INSTRUMENT = AMP_FUNDINGS + "~" + AMP_FINANCING_INSTRUMENT;

    public static final String AMP_PRIMARY_SECTORS_SECTOR = AMP_PRIMARY_SECTORS + "~" + AMP_SECTOR;

    public static final String AMP_SECONDARY_SECTORS_SECTOR = AMP_SECONDARY_SECTORS + "~" + AMP_SECTOR;
    public static final String AMP_TERTIARY_SECTORS_SECTOR = AMP_TERTIARY_SECTORS + "~" + AMP_SECTOR;
    public static final String AMP_FUNDINGS_COMMITMENTS = AMP_FUNDINGS + "~"+ AMP_COMMITMENTS;
    public static final String AMP_FUNDINGS_DISBURSEMENTS = AMP_FUNDINGS + "~"+ AMP_DISBURSEMENTS;
    public static final String AMP_FUNDINGS_EXPENDITURES = AMP_FUNDINGS + "~"+ AMP_EXPENDITURES;
    public static final String AMP_FUNDINGS_DONOR_ORGANIZATION_ID = AMP_FUNDINGS +  "~" + DONOR_ORGANIZATION_ID;
    public static final String AMP_FUNDINGS_COMMITMENTS_CURRENCY = AMP_FUNDINGS_COMMITMENTS +"~" + AMP_CURRENCY;
    public static final String AMP_FUNDINGS_DISBURSEMENTS_CURRENCY = AMP_FUNDINGS_DISBURSEMENTS +"~" + AMP_CURRENCY;
    public static final String AMP_FUNDINGS_CEXPENDITURES_CURRENCY = AMP_FUNDINGS_EXPENDITURES +"~" + AMP_CURRENCY;

    public static final String AMP_FUNDINGS_COMMITMENTS_ADJUSTMENT_TYPE = AMP_FUNDINGS_COMMITMENTS + "~" + AMP_ADJUSTMENT_TYPE;
    public static final String AMP_FUNDINGS_DISBURSEMENTS_ADJUSTMENT_TYPE = AMP_FUNDINGS_DISBURSEMENTS + "~" + AMP_ADJUSTMENT_TYPE;
    public static final String AMP_FUNDINGS_EXPENDITURES_ADJUSTMENT_TYPE = AMP_FUNDINGS_EXPENDITURES + "~" + AMP_ADJUSTMENT_TYPE;

    //end points
    public static final String TRANSLATIONS_ENDPOINT = "/rest/translations/translate?translations=";
    public static final String PROJECTS_ENDPOINT = "/rest/activity/projects";

}
