package org.devgateway.importtool.services.processor;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("IATI103")
@Scope("session")
public class IATI103Processor  extends IATI1XProcessor {	
	public IATI103Processor(){		
		PROCESSOR_VERSION = "1.03";
		descriptiveName = "IATI 1.03";
		codelistPath = "IATI103/codelist/";
		propertiesFile = "IATI103/IATI103Processor.properties";
		schemaPath = "IATI103/schema/";
		fieldsTooltipsLocation = "IATI103/tooltips/fields/tooltips";
		configureDefaults();
		instantiateStaticFields();
	}	
		
	
}
