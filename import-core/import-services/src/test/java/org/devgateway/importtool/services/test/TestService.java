package org.devgateway.importtool.services.test;

import static org.hamcrest.CoreMatchers.is;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.devgateway.importtool.services.ServiceConfiguration;
import org.devgateway.importtool.services.processor.AMPProcessor;
import org.devgateway.importtool.services.processor.IATI201Processor;
import org.devgateway.importtool.services.processor.IATI201StaticProcessor;
import org.devgateway.importtool.services.processor.helper.DocumentMapper;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldValue;
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
	// private Log log = LogFactory.getLog(getClass());

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

	// @Test
	// public void testImportFieldsCompleteCycle() throws Throwable {
	// // Data Source
	// InputStream is = this.getClass().getResourceAsStream(
	// "sample_files/activity-standard-example-minimal.xml");
	//
	// // Processor Class that will process the source
	// ISourceProcessor sourceProcessor = new IATI201Processor();
	//
	// // Set the datasource to the transformer
	// sourceProcessor.setInput(is);
	//
	// // Extract available field from the datasource
	// List<Field> sourceFields = sourceProcessor.getFields();
	// for (Field field : sourceFields) {
	// // Assert that the possible values exist for each field extracted
	// Map<String, String> fieldValues = field.getPossibleValues();
	//
	// if (field.getFieldName().equals("activity-status")) {
	// field.addFilter("2");
	// }
	// Assert.assertThat("There are values for the field.",
	// fieldValues.size(), is(not(0)));
	// Assert.assertThat("There are filters for the field.", field
	// .getFilters().size(), is(not(0)));
	// }
	//
	// List<InternalDocument> sourceDocuments = sourceProcessor.getDocuments();
	// Assert.assertThat("There are three source documents.",
	// sourceDocuments.size(), is(3));
	//
	// List<String> languages = sourceProcessor.getLanguages();
	//
	// Assert.assertThat("There are three source languages.",
	// languages.size(), is(3));
	// // Mark one document as operation INSERT
	//
	// // Processor Class that will process the destination calls
	// IDestinationProcessor destinationProcessor = new AMPProcessor();
	// destinationProcessor.setTestMode(true);
	//
	// List<InternalDocument> destinationDocuments = destinationProcessor
	// .getDocuments();
	// Assert.assertThat("There are three destination documents.",
	// destinationDocuments.size(), is(3));
	//
	// // Extract available fields from the destination
	// List<Field> destinationFields = destinationProcessor.getFields();
	//
	// // Instantiate document mapper that will connect documents from the
	// // source to the destination
	// DocumentMapper documentMapper = new DocumentMapper();
	//
	// // Set involved processors
	// documentMapper.setSourceProcessor(sourceProcessor);
	// documentMapper.setDestinationProcessor(destinationProcessor);
	//
	// Field firstFieldSource = sourceFields.stream().findFirst().get();
	// Field firstFieldDest = destinationFields.stream().findFirst().get();
	// // Map first field from source with first field of destination
	// documentMapper.addFieldMapping(firstFieldSource, firstFieldDest);
	//
	// // Map first field value from source with first field value of
	// // destination
	// Map<String, String> sourcePossibleValues = firstFieldSource
	// .getPossibleValues();
	// Map<String, String> destinationPossibleValues = firstFieldDest
	// .getPossibleValues();
	//
	// // From the field selected to be mapped, map the first value
	//
	// documentMapper.setValueMapping(
	// firstFieldSource,
	// sourcePossibleValues.get(sourcePossibleValues.keySet().stream()
	// .findFirst().get()),
	// destinationPossibleValues.get(destinationPossibleValues
	// .keySet().stream().findFirst().get()));
	//
	// // Map documents
	// // but first mark first document as operation INSERT
	// //
	// sourceDocuments.stream().findFirst().get().setOperation(OperationType.INSERT);
	//
	// // Map only document with operation INSERT with the first destination
	// // object
	// // documentMapper.addDocumentMapping(
	// // sourceDocuments.stream().filter(n -> {
	// // return n.getOperation() == OperationType.INSERT;
	// // }).findFirst().get(), destinationDocuments.stream().findFirst()
	// // .get());
	// //
	// // Initiate transfer to destination system
	// List<MappingResult> results = documentMapper.execute();
	//
	// Assert.assertThat("One document was transferred.", results.size(),
	// is(1));
	// }

	@Test
	public void testActivityStatusImport() throws Throwable {
		// Select type of Source -> Destination process
		ISourceProcessor sourceProcessor = new IATI201StaticProcessor();
		IDestinationProcessor destinationProcessor = new AMPProcessor();
		destinationProcessor.setTestMode(true);

		// Upload Source
		InputStream is = this.getClass().getResourceAsStream(
				"sample_files/activity-standard-example-minimal.xml");
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

		Assert.assertThat("One document to be inserted ", documentMapper
				.getDocumentMappings().stream().filter(n -> {
					return n.getOperation() == OperationType.INSERT;
				}).count(), is(2L));

		Assert.assertThat("One document to be updated", documentMapper
				.getDocumentMappings().stream().filter(n -> {
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
			documentMapper
					.addFieldMapping(optSrcField.get(), optDstField.get());
			// Assign Value mapping of the chosen fields
			// Find value for Activity Status SRC: 2, Implementation ,DST: 2,
			// Ongoing
			Field sourceField = optSrcField.get();
			FieldValue sourceFieldValue = sourceField.getPossibleValues()
					.stream().filter(n -> {
						return n.getCode().equals("2");
					}).findFirst().get();
			Field destinationField = optSrcField.get();
			FieldValue destinationFieldValue = destinationField
					.getPossibleValues().stream().filter(n -> {
						return n.getCode().equals("2");
					}).findFirst().get();
			documentMapper.addValueMapping(sourceField, sourceFieldValue,
					destinationFieldValue);
		}
		// Execute Import
		List<ActionResult> result = documentMapper.execute();
		result.stream().forEach(n -> {
			System.out.println("Result Status: " + n.getStatus());
			System.out.println("Result Message: " + n.getMessage());
		});
	}

}
