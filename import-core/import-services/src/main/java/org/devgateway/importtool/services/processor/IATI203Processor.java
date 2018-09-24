package org.devgateway.importtool.services.processor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("IATI203")
@Scope("session")
public class IATI203Processor extends IATI2XProcessor {

	public IATI203Processor(){
		PROCESSOR_VERSION = "2.03";
		descriptiveName = "IATI 2.03";
		codelistPath = "IATI203/codelist/";
		schemaPath = "IATI203/schema/";
		fieldsTooltipsLocation = "IATI203/tooltips/fields/tooltips";
		propertiesFile = "IATI203/IATI203Processor.properties";
		configureDefaults();
		instantiateStaticFields();
	}	

}
