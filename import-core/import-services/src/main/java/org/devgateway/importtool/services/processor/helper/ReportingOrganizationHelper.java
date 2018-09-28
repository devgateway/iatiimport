package org.devgateway.importtool.services.processor.helper;

import org.devgateway.importtool.endpoint.DataFetchServiceConstants;

import java.io.File;

public class ReportingOrganizationHelper {
    public static String getFileName(String reportingOrg) {
        return System.getProperty(DataFetchServiceConstants.ACTIVITIES_FILES_STORE) + File.separator +
                reportingOrg + ".xml";
    }
}
