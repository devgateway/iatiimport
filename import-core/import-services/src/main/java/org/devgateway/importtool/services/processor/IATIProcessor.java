package org.devgateway.importtool.services.processor;

import org.devgateway.importtool.services.processor.helper.ISourceProcessor;

/**
 * Class that will hold common code among all IATI processors
 */

public abstract class IATIProcessor implements ISourceProcessor {

    public static final String DEFAULT_ID_FIELD = "iati-identifier";
    public static final String LAST_UPDATED_DATE = "last-updated-datetime";
    public static final String DEFAULT_PATH_API = "/results/iati-activities/iati-activity";
    public static String DEFAULT_GROUPING_FIELD = "reporting-org";

}
