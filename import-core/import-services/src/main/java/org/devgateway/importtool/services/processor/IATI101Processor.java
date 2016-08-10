package org.devgateway.importtool.services.processor;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("IATI101")
@Scope("session")
public class IATI101Processor  extends IATI1XProcessor {	
	public IATI101Processor(){		
		PROCESSOR_VERSION = "1.01";
		descriptiveName = "IATI 1.01";
		codelistPath = "IATI101/codelist/";
		propertiesFile = "IATI101/IATI101Processor.properties";		
		configureDefaults();
		instantiateStaticFields();
	}	
		
	
}
