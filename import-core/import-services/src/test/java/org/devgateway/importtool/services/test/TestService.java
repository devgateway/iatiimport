package org.devgateway.importtool.services.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.devgateway.importtool.services.ServiceConfiguration;
import org.devgateway.importtool.services.processor.AMPProcessor;
import org.devgateway.importtool.services.processor.IATI201Processor;
import org.devgateway.importtool.services.processor.helper.DocumentMapper;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.IDestinationProcessor;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.devgateway.importtool.services.processor.helper.InternalDocument;
import org.devgateway.importtool.services.processor.helper.MappingResult;
import org.devgateway.importtool.services.processor.helper.OperationType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestService.TestServiceConfiguration.class)
@Transactional
@TransactionConfiguration
/***
 * Sample test
 * @author Fernando
 *
 */
public class TestService {
	//private Log log = LogFactory.getLog(getClass());

	@Configuration
	@EnableAutoConfiguration
	@Import(ServiceConfiguration.class)
	static class TestServiceConfiguration {
	}

	@Before
	public void begin() throws Throwable {
	}

	@Test
	public void testCreateUser() throws Throwable {
		Assert.assertEquals(true, true);
	}

	@Test
	public void testImportFieldsMinimal() throws Throwable {
		ISourceProcessor transformer = new IATI201Processor();
		InputStream is = this.getClass().getResourceAsStream(
				"sample_files/activity-standard-example-minimal.xml");
		transformer.setInput(is);
		List<Field> fields = transformer.getFields();
		List<String> expectedFields = new ArrayList<String>();
		expectedFields.add("iati-identifier");
		expectedFields.add("activity-status");
		expectedFields.add("title");

		Boolean matched = fields.stream().anyMatch(
				n -> {
					return expectedFields.stream().anyMatch(
							m -> m == n.getFieldName());
				});
		Assert.assertEquals(matched, true);
	}

	@Test
	public void testImportFieldsCompleteCycle() throws Throwable {
		// Data Source
		InputStream is = this.getClass().getResourceAsStream(
				"sample_files/activity-standard-example-minimal.xml");

		// Processor Class that will process the source
		ISourceProcessor sourceProcessor = new IATI201Processor();

		// Set the datasource to the transformer
		sourceProcessor.setInput(is);

		// Extract available fields from the datasource
		List<Field> sourceFields = sourceProcessor.getFields();
		for (Field field : sourceFields) {
			// Assert that the possible values exist for each field extracted
			Map<String, String> fieldValues = field.getPossibleValues();
			Assert.assertThat("There are values for the field.",
					fieldValues.size(), is(not(0)));
			Assert.assertThat("There are filters for the field.", field
					.getFilters().size(), is(not(0)));
		}

		List<InternalDocument> sourceDocuments = sourceProcessor.getDocuments();
		Assert.assertThat("There are three source documents.",
				sourceDocuments.size(), is(3));
		
		//Mark one document as operation INSERT

		// Processor Class that will process the destination calls
		IDestinationProcessor destinationProcessor = new AMPProcessor();
		List<InternalDocument> destinationDocuments = destinationProcessor
				.getDocuments();
		Assert.assertThat("There are three destination documents.",
				destinationDocuments.size(), is(3));

		// Extract available fields from the destination
		List<Field> destinationFields = destinationProcessor.getFields();

		// Instantiate document mapper that will connect documents from the
		// source to the destination
		DocumentMapper documentMapper = new DocumentMapper();

		// Set involved processors
		documentMapper.setSourceProcessor(sourceProcessor);
		documentMapper.setDestinationProcessor(destinationProcessor);

		// Map first field from source with first field of destination
		documentMapper.addFieldMapping(sourceFields.stream().findFirst().get(),
				destinationFields.stream().findFirst().get());

		// Map first field value from source with first field value of
		// destination
//		documentMapper.addValueMapping(sourceFields.stream().findFirst().get()
//				.getPossibleValues().stream().findFirst().get(),
//				destinationFields.stream().findFirst().get()
//						.getPossibleValues().stream().findFirst().get());

		// Map documents
		// but first mark first document as operation INSERT
		sourceDocuments.stream().findFirst().get().setOperation(OperationType.INSERT);

		// Map only document with operation INSERT with the first destination object
		documentMapper.addDocumentMapping(
				sourceDocuments.stream().filter(n -> {
					return n.getOperation() == OperationType.INSERT;
				}).findFirst().get(), destinationDocuments.stream().findFirst()
						.get());

		// Initiate transfer to destination system
		List<MappingResult> results = documentMapper.execute();
		
		Assert.assertThat("One document was transferred.", results.size(), is(1));
	}

}
