package org.devgateway.importtool.services.processor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Component("IATI105")
@Scope("session")
public class IATI105Processor extends IATI1XProcessor {	
	public IATI105Processor(){		
		PROCESSOR_VERSION = "1.05";
		descriptiveName = "IATI 1.05";
		codelistPath = "IATI105/codelist/";
		propertiesFile = "IATI105/IATI105Processor.properties";
		schemaPath = "IATI105/schema/";
		fieldsTooltipsLocation = "IATI105/tooltips/fields/tooltips";
		configureDefaults();
		instantiateStaticFields();
	}	

}