package org.devgateway.importtool.services.processor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("IATI202")
@Scope("session")
public class IATI202Processor extends IATI2XProcessor {

	public IATI202Processor(){		
		PROCESSOR_VERSION = "2.02";
		descriptiveName = "IATI 2.02";
		codelistPath = "IATI202/codelist/";
		schemaPath = "IATI202/schema/";
		fieldsTooltipsLocation = "IATI202/tooltips/fields/tooltips.properties";
		fieldsTooltipsFileName = "tooltips.properties";
		activtySchemaName = "iati-activities-schema.xsd";
		propertiesFile = "IATI202/IATI202Processor.properties";
		configureDefaults();
		instantiateStaticFields();
	}	

}
