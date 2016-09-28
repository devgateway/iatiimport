package org.devgateway.importtool.endpoint;

public class EPMessages {
 public static final ApiMessage PARSING_IN_PROGRESS = new ApiMessage(101, "Extracting project %s of %s");
 public static final ApiMessage FETCHING_DESTINATION_PROJECTS = new ApiMessage(102, "Fetching destination prpjects");
 public static final ApiMessage ERROR_EXCTRACTING_PROJECT = new ApiMessage(103, "An error occurred while extracting projects from the IATI file. Please check the file format");
 public static final ApiMessage ERROR_UPLOADING_FILE_CHECK_INITIAL_STEPS = new ApiMessage(104, "Error uploading file. Check if the initial steps are done.");
 public static final ApiMessage ERROR_UPLOADING_FILE = new ApiMessage(105, "Error uploading file."); 
 public static final ApiMessage IMPORT_STATUS_MESSAGE = new ApiMessage(106, "Importing %s of %s projects");
 public static final ApiMessage MAPPING_STATUS_MESSAGE = new ApiMessage(107, "Mapping %s of %s");
}
