package org.devgateway.importtool.services.processor;

import org.junit.Assert;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.devgateway.importtool.endpoint.EPMessages;
import org.devgateway.importtool.services.processor.helper.ActionStatus;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.devgateway.importtool.services.processor.helper.Status;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IATI104ProcessorTests.TestIATI104ProcessorConfiguration.class)
@Transactional
public class IATI104ProcessorTests {
		
	@Before
	public void setUp() throws Exception {
	}

	@Configuration
	@EnableAutoConfiguration
	static class TestIATI104ProcessorConfiguration {
	}
	
	@Test	
	public void testIATIFileIsParsedCorrectly() throws Exception {
		ISourceProcessor transformer = new IATI104Processor();
		InputStream is = this.getClass().getResourceAsStream("sample_files/activity-standard-example-minimal.xml");
		transformer.setActionStatus(this.getStatus());
		transformer.setInput(is);
		
		List<Field> fields = transformer.getFields();
		List<String> expectedFields = new ArrayList<String>();
		expectedFields.add("iati-identifier");
		expectedFields.add("activity-status");
		expectedFields.add("title");

		Boolean matched = fields.stream().anyMatch(n -> {
			return expectedFields.stream().anyMatch(m -> m == n.getFieldName());
		});
		
		
		Assert.assertNotNull("Document is not null ",transformer.getDoc());
		Assert.assertEquals("Number of activities is in parsed document is correct", 3, transformer.getDocuments().size());
		Assert.assertEquals("Expected fields are available", matched, true);
		
	}
	
	@Test
	public void testLanguagesAreParsed(){
		ISourceProcessor transformer = new IATI104Processor();
		InputStream is = this.getClass().getResourceAsStream("sample_files/activity-standard-example-minimal.xml");
		transformer.setInput(is);
		List<String> expectedLanguages = new ArrayList<String>();
		expectedLanguages.add("en");
		expectedLanguages.add("fr");
		expectedLanguages.add("es");		
		Boolean matched = transformer.getLanguages().stream().anyMatch(n -> {						
			return expectedLanguages.stream().anyMatch(m -> m.equals(n));
		});
		Assert.assertEquals("Expected languages are available", matched, true);
	}
	
	private ActionStatus getStatus() {
		ActionStatus status = new ActionStatus();
		status.setMessage(EPMessages.MAPPING_STATUS_MESSAGE.getDescription());
		status.setCode(EPMessages.MAPPING_STATUS_MESSAGE.getCode());
		status.setTotal(0L);
		status.setStatus(Status.IN_PROGRESS);
	    return status;
	}
	
}
