package org.devgateway.importtool.services.iati202;

import org.devgateway.importtool.services.processor.AMP34TestDestinationProcessor;
import org.devgateway.importtool.services.processor.AMPProcessor;
import org.devgateway.importtool.services.processor.IATI202Processor;
import org.devgateway.importtool.services.processor.helper.*;
import org.devgateway.importtool.services.request.ImportRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DocumentMapper202Tests {
    @Before
    public void setUp() {
    }

    @Test
    public void testInitializeDocumentMapper() {
        ISourceProcessor sourceProcessor = new IATI202Processor();
        AMPProcessor destinationProcessor = new AMPProcessor();
        destinationProcessor.setTestMode(true);

        InputStream is = this.getClass().getResourceAsStream("../sample_files/drc/activity-example-2.02.xml");
        sourceProcessor.setInput(is);

        DocumentMapper documentMapper = new DocumentMapper();
        documentMapper.setSourceProcessor(sourceProcessor);
        documentMapper.setDestinationProcessor(destinationProcessor);
        documentMapper.initialize();

        Assert.assertEquals("DocumentMapper is initialized", true, documentMapper.isInitialized());
    }

    @Test
    public void testExecuteDocumentMapper() {
        ISourceProcessor sourceProcessor = new IATI202Processor();
        AMP34TestDestinationProcessor destinationProcessor = new AMP34TestDestinationProcessor();
        destinationProcessor.setTestMode(true);
        ImportRequest importRequest = new ImportRequest();
        importRequest.setImportOption(ImportOption.OVERWRITE_ALL_FUNDING);

        InputStream is = this.getClass().getResourceAsStream("../sample_files/drc/activity-example-2.02.xml");
        sourceProcessor.setInput(is);

        DocumentMapper documentMapper = new DocumentMapper();
        documentMapper.setSourceProcessor(sourceProcessor);
        documentMapper.setDestinationProcessor(destinationProcessor);
        documentMapper.initialize();

        List<Field> sourceFields = sourceProcessor.getFields();
        List<Field> destinationFields = destinationProcessor.getFields();

        Optional<Field> optSrcField = sourceFields.stream().filter(n -> {
            return n.getFieldName().equals("iati-identifier");
        }).findFirst();

        Optional<Field> optDstField = destinationFields.stream().filter(n -> {
            return n.getFieldName().equals("iati-identifier");
        }).findFirst();

        List<FieldMapping> fieldMappingList = new ArrayList<>();
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

        List<ActionResult> result = documentMapper.execute(importRequest);

        Assert.assertEquals("DocumentMapper - result size is equal to document mappings",
				result.size(), documentMapper.getDocumentMappings().size());
    }

}
