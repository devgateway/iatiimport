package org.devgateway.importtool.services.test;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Map;

import org.devgateway.importtool.services.dto.FundingDetail;
import org.devgateway.importtool.services.dto.JsonBean;
import org.devgateway.importtool.services.processor.helper.DateUtils;
import org.devgateway.importtool.services.processor.helper.ProcessorUtils;
import org.junit.Test;

public class FundingTests {
    
    private String transactionJson = "{\"adjustment_type\":200,\"currency\":20,"
            + "\"transaction_date\":\"2009-09-07T00:00:00.000+0000\",\"transaction_amount\":400000.0}";
    
    private ProcessorUtils processorUtils = ProcessorUtils.getInstance();
    
    @Test
    public void testTransactionJson() throws ParseException {
    
        FundingDetail fd = new FundingDetail();
        fd.setAdjustmentType(200);
        fd.setCurrency(20);
        fd.setTransactionDate(DateUtils.parseDate(Boolean.FALSE,"2009-09-07"));
        fd.setTransactionAmount(400000d);
    
        JsonBean transactionJsonBean = JsonBean.getJsonBeanFromString(transactionJson);

        assertEquals(fd, processorUtils.getFundingDetailFromJsonBean(transactionJsonBean));
    }
    
    @Test
    public void testTransactionMap() throws ParseException {
        
        FundingDetail fd = new FundingDetail();
        fd.setAdjustmentType(200);
        fd.setCurrency(20);
        fd.setTransactionDate(DateUtils.parseDate(Boolean.FALSE,"2009-09-07"));
        fd.setTransactionAmount(400000d);
        Map<String, Object> transactionMap = JsonBean.getJsonBeanFromString(transactionJson).any();
        assertEquals(fd, processorUtils.getFundingDetailFromMap(transactionMap));
    }
    
    
    @Test
    public void testFundingsContainTransaction() throws ParseException {
        
        FundingDetail fd = new FundingDetail();
        fd.setAdjustmentType(200);
        fd.setCurrency(20);
        fd.setTransactionDate(DateUtils.parseDate(Boolean.FALSE,"2009-09-07"));
        fd.setTransactionAmount(400000d);
        
        Map<String, Object> transactionMap = JsonBean.getJsonBeanFromString(transactionJson).any();
        
        assertEquals(fd, processorUtils.getFundingDetailFromMap(transactionMap));
    }
}
