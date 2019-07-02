package org.devgateway.importtool.services.processor.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Constants {
	public static final String SOURCE_PROCESSOR = "SOURCE_PROCESSOR";
	public static final String IATI_STORE_ACTIVITIES = "IATI_STORE_ACTIVITIES";
	public static final String DESTINATION_PROCESSOR = "DESTINATION_PROCESSOR";
	public static final String DOCUMENT_MAPPER = "DOCUMENT_MAPPER";
	public static final String CURRENT_FILE_ID = "CURRENT_FILE_ID";
	public static final String SESSION_TOKEN = "SESSION_TOKEN";
	public static final String WORKFLOW_LIST = "WORKFLOW_LIST";
	public static final String REPORTING_ORG = "REPORTING_ORG";
	
	public static final String WORKFLOW_FILE = "workflows.xml";
	public static final String WORKFLOW_TAG_NAME = "workflow";
	public static final String DESTINATION_PROCESSOR_TAG_NAME = "destination-processor";
	public static final String SOURCE_PROCESSOR_TAG_NAME = "source-processor";
	public static final String NAME_TAG = "name";
	public static final String LABEL_TAG = "label";
	public static final String CLASS_NAME_TAG = "class-name";
	public static final String DESCRIPTION_TAG = "description";
	public static final String TRANSLATION_KEY_TAG = "translation-key";
	public static final String 	ENABLED_TAG = "enabled";
	public static final String IMPORT_STATUS_MESSAGE = "Importing %s of %s projects";

	public static final String ACTUAL = "Actual";
	public static final String PLANNED = "Planned";
	public static final String COMMITMENTS = "Commitments";
	public static final String DISBURSEMENTS = "Disbursements";
	public static final String EXPENDITURES = "Expenditures";

	public static final String ACTUAL_EXPENDITURES = ACTUAL + " " + EXPENDITURES;
	public static final String PLANNED_EXPENDITURES = PLANNED + " " + EXPENDITURES;

	public static final String TRANSACTION_TYPE_ACTUAL_COMMITMENTS ="AC";
	public static final String TRANSACTION_TYPE_PLANNED_COMMITMENTS ="PC";
	public static final String TRANSACTION_TYPE_ACTUAL_DISBURSEMENTS ="AD";
	public static final String TRANSACTION_TYPE_PLANNED_DISBURSEMENTS ="PD";
	public static final String TRANSACTION_TYPE_ACTUAL_EXPENDITURES ="AE";
	public static final String TRANSACTION_TYPE_PLANNED_EXPENDITURES ="PE";

	public static final String ORG_ROLE_FUNDING_CODE = "1";
	public static final String ORG_ROLE_FUNDING = "Funding";
	public static final String ORG_ROLE_ACCOUNTABLE_CODE = "2";
	public static final String ORG_ROLE_ACCOUNTABLE = "Accountable";
	public static final String ORG_ROLE_EXTENDING_CODE = "3";
	public static final String ORG_ROLE_EXTENDING = "Extending";	
	public static final String ORG_ROLE_IMPLEMENTING_CODE = "4";
	public static final String ORG_ROLE_IMPLEMENTING = "Implementing";
		
	public static final String FUNDING_ORG_DISPLAY_NAME = "Funding Organization";
	public static final String PROVIDER_ORG_DISPLAY_NAME = "Provider Organization";	
	public static final String IMPLEMENTING_ORG_DISPLAY_NAME = "Implementing Organization";
	public static final String EXTENDING_ORG_DISPLAY_NAME = "Extending Organization";
	public static final String ACCOUNTABLE_ORG_DISPLAY_NAME = "Accountable Organization";
	public static final Set<String> SUPPORTED_LOCALES= new HashSet<>(Arrays.asList("en","fr", "es"));

	public static final Integer AMP_PUSH_BATCH_SIZE = 3;
	public static final Integer AMP_PULL_BATCH_SIZE = 100;
	public static final String AMP_ACTIVITY_ENDPOINT = "rest/activity";

	//AMP FIELDS to be moved to its own property field
	public static final String AMP_INTERNAL_ID = "internal_id";

	public static final String AMP_UPDATE_OPERATION = "UPDATE";
	public static final String AMP_INSERT_OPERATION = "INSERT";

	public static final String LANG_PACK_TOOLTIPS="TOOLTIPS";
	public static final String LANG_PACK_LABELS="LABELS";
	public static final List<String> TRANSACTION_FIELDS = new ArrayList<>(Arrays.asList("Actual Commitments",
			"Actual Disbursements","Actual Expenditures","Planned Commitments", "Planned Disbursements",
			"Planned Expenditures"));
	public static final String AMP_ACTIVITY_STATUS = "activity_status";
	public static final String AMP_A_C_CHAPTER = "A C Chapter";
	public static final String AMP_FUNDINGS ="fundings";
	public static final String AMP_FUNDING_DETAILS ="funding_details";
	public static final String AMP_TYPE_OF_ASSISTANCE = "type_of_assistance";
	public static final String AMP_FUNDINGS_TYPE_OF_ASSISTANCE = AMP_FUNDINGS + "~"+ AMP_TYPE_OF_ASSISTANCE;
	public static final String AMP_FINANCING_INSTRUMENT = "financing_instrument";
	public static final String AMP_ADJUSTMENT_TYPE = "adjustment_type";
	public static final String AMP_TRANSACTION_TYPE = "transaction_type";

	public static final String AMP_PRIMARY_SECTORS = "primary_sectors";
	public static final String AMP_SECTOR = "sector";
	public static final String AMP_SECONDARY_SECTORS = "secondary_sectors";
	public static final String AMP_TERTIARY_SECTORS = "tertiary_sectors";
	public static final String AMP_CURRENCY = "currency";
	public static final String LOCATIONS_LOCATION = "locations~location";
	public static final String DONOR_ORGANIZATION_ID = "donor_organization_id";
	public static final String AMP_FUNDINGS_FUNDING_DETAILS_ADJUSTMENT_TYPE = AMP_FUNDINGS + "~" + AMP_FUNDING_DETAILS
			+ "~" + AMP_ADJUSTMENT_TYPE;
	public static final String AMP_FUNDINGS_FINANCING_INSTRUMENT = AMP_FUNDINGS + "~" + AMP_FINANCING_INSTRUMENT;
	public static final String AMP_FUNDINGS_FUNDING_DETAILS_TRANSACTION_TYPE = AMP_FUNDINGS + "~"+ AMP_FUNDING_DETAILS + "~"
			+ AMP_TRANSACTION_TYPE;
	public static final String AMP_PRIMARY_SECTORS_SECTOR = AMP_PRIMARY_SECTORS + "~" + AMP_SECTOR;

	public static final String AMP_SECONDARY_SECTORS_SECTOR = AMP_SECONDARY_SECTORS + "~" + AMP_SECTOR;
	public static final String AMP_TERTIARY_SECTORS_SECTOR = AMP_TERTIARY_SECTORS + "~" + AMP_SECTOR;

	public static final String AMP_FUNDINGS_DONOR_ORGANIZATION_ID = AMP_FUNDINGS +  "~" + DONOR_ORGANIZATION_ID;
	public static final String AMP_FUNDINGS_FUNDING_DETAILS_CURRENCY = AMP_FUNDINGS + "~"+ AMP_FUNDING_DETAILS + "~" + AMP_CURRENCY;
	public static final List<String> LIST_OF_VALUES_TO_FETCH = new ArrayList<>(Arrays.asList(AMP_ACTIVITY_STATUS,
			AMP_A_C_CHAPTER, AMP_FUNDINGS_TYPE_OF_ASSISTANCE, AMP_FUNDINGS_FINANCING_INSTRUMENT,
			AMP_FUNDINGS_FUNDING_DETAILS_ADJUSTMENT_TYPE, AMP_FUNDINGS_FUNDING_DETAILS_TRANSACTION_TYPE,
			AMP_PRIMARY_SECTORS_SECTOR, AMP_SECONDARY_SECTORS_SECTOR, AMP_TERTIARY_SECTORS_SECTOR,LOCATIONS_LOCATION ,
			AMP_FUNDINGS_DONOR_ORGANIZATION_ID, AMP_FUNDINGS_FUNDING_DETAILS_CURRENCY));

	public static final String NULL_STRING ="null";
}
