package org.devgateway.importtool.services.processor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Component("IATI201")
@Scope("session")
public class IATI201Processor extends IATI2XProcessor{
	public IATI201Processor(){		
		PROCESSOR_VERSION = "2.01";
		descriptiveName = "IATI 2.01";
		codelistPath = "IATI201/codelist/";
		propertiesFile = "IATI201/IATI201Processor.properties";
		configureDefaults();
		instantiateStaticFields();
	}	

	
}
