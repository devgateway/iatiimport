package org.devgateway.importtool.services.processor.helper;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.devgateway.importtool.services.dto.FundingDetail;
import org.devgateway.importtool.services.dto.JsonBean;

public class ProcessorUtils {
    
    private static final Logger logger = Logger.getLogger(ProcessorUtils.class);
    
    private static ProcessorUtils processorUtils;
    
    private ProcessorUtils() {}
    
    public static ProcessorUtils getInstance() {
        if (processorUtils == null) {
            processorUtils = new ProcessorUtils();
        }
        
        return processorUtils;
    }
    
    /**
     * checks if a transaction from the IATI file exists in the funding details from
     * AMP. Since IATI transactions do not have a unique identifier, we compare the
     * fields to check if the transaction exists.
     *
     * @param transList
     *            - funding details from AMP
     * @param transaction
     *            - transaction from IATI file
     * @return
     */
    public boolean transactionExistsInTransactionList(List<Map<String, Object>> transList, JsonBean transaction) {
    
        return transList.stream()
                .anyMatch(n -> {
                    try {
                        return getFundingDetailFromMap(n).equals(getFundingDetailFromJsonBean(transaction));
                    } catch (ParseException e) {
                        logger.error(e.getMessage(), e);
                    }
                    
                    return false;
                });
    }
    
    public FundingDetail getFundingDetailFromJsonBean(JsonBean data) throws ParseException {
        FundingDetail fd = new FundingDetail();
        fd.setAdjustmentType(Integer.valueOf(data.getString("adjustment_type")));
        fd.setCurrency(Integer.valueOf(data.getString("currency")));
        fd.setTransactionAmount(Double.valueOf(data.getString("transaction_amount")));
        fd.setTransactionDate(DateUtils.getDateTimeFormatter().parse(data.getString("transaction_date")));
        
        return fd;
    }
    
    public FundingDetail getFundingDetailFromMap(Map<String, Object> data) throws ParseException {
        FundingDetail fd = new FundingDetail();
        fd.setAdjustmentType((Integer) data.get("adjustment_type"));
        fd.setCurrency((Integer) data.get("currency"));
        fd.setTransactionAmount((Double)data.get("transaction_amount"));
        fd.setTransactionDate(DateUtils.getDateTimeFormatter().parse((String) data.get("transaction_date")));
    
        return fd;
    }
    
}
