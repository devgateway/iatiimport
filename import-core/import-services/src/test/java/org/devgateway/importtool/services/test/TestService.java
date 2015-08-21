package org.devgateway.importtool.services.test;

import static org.hamcrest.CoreMatchers.is;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.services.ServiceConfiguration;
import org.devgateway.importtool.services.processor.AMPProcessor;
import org.devgateway.importtool.services.processor.IATI104Processor;
import org.devgateway.importtool.services.processor.IATI201Processor;
import org.devgateway.importtool.services.processor.helper.DocumentMapper;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldMapping;
import org.devgateway.importtool.services.processor.helper.FieldValue;
import org.devgateway.importtool.services.processor.helper.FieldValueMapping;
import org.devgateway.importtool.services.processor.helper.IDestinationProcessor;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.devgateway.importtool.services.processor.helper.ActionResult;
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
	private Log log = LogFactory.getLog(getClass());

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

	// @Test
	public void testImportFieldsMinimal() throws Throwable {
		ISourceProcessor transformer = new IATI201Processor();
		InputStream is = this.getClass().getResourceAsStream("sample_files/activity-standard-example-minimal.xml");
		transformer.setInput(is);
		List<Field> fields = transformer.getFields();
		List<String> expectedFields = new ArrayList<String>();
		expectedFields.add("iati-identifier");
		expectedFields.add("activity-status");
		expectedFields.add("title");

		Boolean matched = fields.stream().anyMatch(n -> {
			return expectedFields.stream().anyMatch(m -> m == n.getFieldName());
		});
		Assert.assertEquals(matched, true);
	}

	@SuppressWarnings("unused")
	@Test
	public void testKosovoImport() throws Throwable {
		// Select type of Source -> Destination process
		ISourceProcessor sourceProcessor = new IATI104Processor();
		IDestinationProcessor destinationProcessor = new AMPProcessor();
		destinationProcessor.setTestMode(false);
		destinationProcessor.setAuthenticationToken("27368298-48e3-48ef-9f75-13a58f2f5cb6");

		// Upload Source
		InputStream is = this.getClass().getResourceAsStream("sample_files/Kosovo_Test_File.xml");
		sourceProcessor.setInput(is);

		// Filter Source Data
		// Get the "activity-status" field from the list of available filters
		// for the processor
		// Set a value for filtering
		// List<Field> filterFields = sourceProcessor.getFilterFields();
		// Field testFilterField = filterFields.stream().filter(n -> {
		// return n.getFieldName().equals("activity-status");
		// }).findFirst().get();
		// testFilterField.addFilter("2");

		// Choose Projects to be updated
		// Get List of Source Projects that match the filter
		// Get List of Destination Projects and match them to create a list of
		// new and existing projects
		// Set Operations

		DocumentMapper documentMapper = new DocumentMapper();
		// Assign Source and Destination Processor to the document mapper
		documentMapper.setSourceProcessor(sourceProcessor);
		documentMapper.setDestinationProcessor(destinationProcessor);
		documentMapper.initialize();

		long insertCount = documentMapper.getDocumentMappings().stream().filter(n -> {
			return n.getOperation() == OperationType.INSERT;
		}).count();

		// Assert.assertThat("One document to be inserted ", , is(2L));
		long updateCount = documentMapper.getDocumentMappings().stream().filter(n -> {
			return n.getOperation() == OperationType.UPDATE;
		}).count();
		// Assert.assertThat("One document to be updated", , is(1L));
		log.debug("to insert:" + insertCount);
		log.debug("to update:" + updateCount);

		// Choose Fields
		List<Field> sourceFields = sourceProcessor.getFields();
		List<Field> destinationFields = destinationProcessor.getFields();

		// Find source field activity status
		Optional<Field> optSrcField = sourceFields.stream().filter(n -> {
			return n.getFieldName().equals("activity-status");
		}).findFirst();

		Optional<Field> optDstField = destinationFields.stream().filter(n -> {
			return n.getFieldName().equals("activity_status");
		}).findFirst();

		if (optSrcField.isPresent() && optDstField.isPresent()) {
			// Assign the field mapping
			// documentMapper
			// .addFieldMapping(optSrcField.get(), optDstField.get());
			FieldMapping fm = new FieldMapping();
			fm.setSourceField(optSrcField.get());
			fm.setDestinationField(optDstField.get());

			// Assign Value mapping of the chosen fields
			// Find value for Activity Status SRC: 2, Implementation ,DST: 2,
			// Ongoing
			Field sourceField = optSrcField.get();

			FieldValue sourceFieldValue = sourceField.getPossibleValues().stream().filter(n -> {
				return n.getCode().equals("2");
			}).findFirst().get();
			Field destinationField = optSrcField.get();
			FieldValue destinationFieldValue = destinationField.getPossibleValues().stream().filter(n -> {
				return n.getCode().equals("2");
			}).findFirst().get();
			FieldValueMapping fvm = new FieldValueMapping();
			fvm.setSourceField(sourceField);
			// fvm.setSourceFieldValue(sourceFieldValue);
			// fvm.setDestinationFieldValue(destinationFieldValue);
			// documentMapper.addValueMapping(sourceField, sourceFieldValue,
			// destinationFieldValue);
		}
		// Execute Import
		List<ActionResult> result = documentMapper.execute();
		result.stream().forEach(n -> {
//			System.out.println("Result Status: " + n.getStatus());
//			System.out.println("Result Message: " + n.getMessage());
		});
	}

	// @Test
	@SuppressWarnings("unused")
	public void testActivityStatusImport() throws Throwable {
		// Select type of Source -> Destination process
		ISourceProcessor sourceProcessor = new IATI201Processor();
		IDestinationProcessor destinationProcessor = new AMPProcessor();
		destinationProcessor.setTestMode(true);

		// Upload Source
		InputStream is = this.getClass().getResourceAsStream("sample_files/activity-standard-example-minimal.xml");
		sourceProcessor.setInput(is);

		// Filter Source Data
		// Get the "activity-status" field from the list of available filters
		// for the processor
		// Set a value for filtering
		List<Field> filterFields = sourceProcessor.getFilterFields();
		Field testFilterField = filterFields.stream().filter(n -> {
			return n.getFieldName().equals("activity-status");
		}).findFirst().get();
		testFilterField.addFilter("2");

		// Choose Projects to be updated
		// Get List of Source Projects that match the filter
		// Get List of Destination Projects and match them to create a list of
		// new and existing projects
		// Set Operations

		DocumentMapper documentMapper = new DocumentMapper();
		// Assign Source and Destination Processor to the document mapper
		documentMapper.setSourceProcessor(sourceProcessor);
		documentMapper.setDestinationProcessor(destinationProcessor);
		documentMapper.initialize();

		Assert.assertThat("One document to be inserted ", documentMapper.getDocumentMappings().stream().filter(n -> {
			return n.getOperation() == OperationType.INSERT;
		}).count(), is(2L));

		Assert.assertThat("One document to be updated", documentMapper.getDocumentMappings().stream().filter(n -> {
			return n.getOperation() == OperationType.UPDATE;
		}).count(), is(1L));

		// Choose Fields
		List<Field> sourceFields = sourceProcessor.getFields();
		List<Field> destinationFields = destinationProcessor.getFields();

		// System.out.println(sourceFields.size());
		// Find source field activity status
		Optional<Field> optSrcField = sourceFields.stream().filter(n -> {
			return n.getFieldName().equals("activity-status");
		}).findFirst();

		Optional<Field> optDstField = destinationFields.stream().filter(n -> {
			return n.getFieldName().equals("activity_status");
		}).findFirst();

		if (optSrcField.isPresent() && optDstField.isPresent()) {
			// Assign the field mapping
			// documentMapper
			// .addFieldMapping(optSrcField.get(), optDstField.get());
			FieldMapping fm = new FieldMapping();
			fm.setSourceField(optSrcField.get());
			fm.setDestinationField(optDstField.get());

			// Assign Value mapping of the chosen fields
			// Find value for Activity Status SRC: 2, Implementation ,DST: 2,
			// Ongoing
			Field sourceField = optSrcField.get();

			FieldValue sourceFieldValue = sourceField.getPossibleValues().stream().filter(n -> {
				return n.getCode().equals("2");
			}).findFirst().get();
			Field destinationField = optSrcField.get();
			FieldValue destinationFieldValue = destinationField.getPossibleValues().stream().filter(n -> {
				return n.getCode().equals("2");
			}).findFirst().get();
			FieldValueMapping fvm = new FieldValueMapping();
			fvm.setSourceField(sourceField);
			// fvm.setSourceFieldValue(sourceFieldValue);
			// fvm.setDestinationFieldValue(destinationFieldValue);
			// documentMapper.addValueMapping(sourceField, sourceFieldValue,
			// destinationFieldValue);
		}
		// Execute Import
		List<ActionResult> result = documentMapper.execute();
		result.stream().forEach(n -> {
//			System.out.println("Result Status: " + n.getStatus());
//			System.out.println("Result Message: " + n.getMessage());
		});
	}

}
