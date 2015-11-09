package org.devgateway.importtool.services.processor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Component("IATI104")
@Scope("session")
public class IATI104Processor extends IATI1XProcessor {	
	public IATI104Processor(){		
		PROCESSOR_VERSION = "1.04";
		descriptiveName = "IATI 1.04";
		codelistPath = "IATI104/codelist/";
		propertiesFile = "IATI104/IATI104Processor.properties";
		configureDefaults();
		instantiateStaticFields();
	}	

}
