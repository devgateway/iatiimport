package org.devgateway.importtool.services.test;

import org.junit.Assert;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.devgateway.importtool.services.processor.AMPProcessor;
import org.devgateway.importtool.services.processor.IATI104Processor;
import org.devgateway.importtool.services.processor.helper.ActionResult;
import org.devgateway.importtool.services.processor.helper.DocumentMapper;
import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.FieldMapping;
import org.devgateway.importtool.services.processor.helper.IDestinationProcessor;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.junit.Before;
import org.junit.Test;

public class DocumentMapperTests {
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testInitialize() throws Exception {		
				ISourceProcessor sourceProcessor = new IATI104Processor();
				AMPProcessor destinationProcessor = new AMPProcessor();
				destinationProcessor.setTestMode(true);
				destinationProcessor.setAuthenticationToken("27368298-48e3-48ef-9f75-13a58f2f5cb6");

				InputStream is = this.getClass().getResourceAsStream("sample_files/Kosovo_Test_File.xml");
				sourceProcessor.setInput(is);
				
				DocumentMapper documentMapper = new DocumentMapper();
				documentMapper.setSourceProcessor(sourceProcessor);
				documentMapper.setDestinationProcessor(destinationProcessor);
				documentMapper.initialize();
				Assert.assertEquals("DocumentMapper is initialized", true, documentMapper.isInitialized());
				
				
	}
	
	@Test
	public void testExecute() throws Exception {
		
		        IATI104Processor sourceProcessor = new IATI104Processor();
		        AMPProcessor destinationProcessor = new AMPProcessor();
				destinationProcessor.setTestMode(true);
				destinationProcessor.setAuthenticationToken("27368298-48e3-48ef-9f75-13a58f2f5cb6");

				InputStream is = this.getClass().getResourceAsStream("sample_files/Kosovo_Test_File.xml");
				sourceProcessor.setInput(is);
				
				DocumentMapper documentMapper = new DocumentMapper();
				documentMapper.setSourceProcessor(sourceProcessor);
				documentMapper.setDestinationProcessor(destinationProcessor);
				documentMapper.initialize();
								
				List<Field> sourceFields = sourceProcessor.getFields();
				List<Field> destinationFields = destinationProcessor.getFields();

				// Find source field activity status
				Optional<Field> optSrcField = sourceFields.stream().filter(n -> {
					return n.getFieldName().equals("iati-identifier");
				}).findFirst();

				Optional<Field> optDstField = destinationFields.stream().filter(n -> {
					return n.getFieldName().equals("iati-identifier");
				}).findFirst();
                 
				List<FieldMapping> fieldMappingList =  new ArrayList<FieldMapping>();
				if (optSrcField.isPresent() && optDstField.isPresent()) {						
					FieldMapping fm = new FieldMapping();
					fm.setSourceField(optSrcField.get());
					fm.setDestinationField(optDstField.get());
					
					fieldMappingList.add(fm);
					
					documentMapper.setFieldMappingObject(fieldMappingList);

				}
				
				documentMapper.getDocumentMappings().stream().forEach(d -> {					
					d.setSelected(true);
				});
				
				List<ActionResult> result = documentMapper.execute();
				Assert.assertEquals("DocumentMapper - result size is equal to document mappings", result.size(), documentMapper.getDocumentMappings().size());				
	}

}
