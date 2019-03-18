package org.devgateway.importtool.services.dto;

import java.util.Date;

import org.devgateway.importtool.services.processor.helper.DateUtils;

public class FundingDetail {
    
    private String transactionType;
    
    private Integer adjustmentType;
    
    private Date transactionDate;
    
    private Integer currency;
    
    private Double transactionAmount;
    
    private Boolean disasterResponse;

    Boolean isTransactionDateTimeStamp;
    
    public String getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    
    public Integer getAdjustmentType() {
        return adjustmentType;
    }
    
    public void setAdjustmentType(Integer adjustmentType) {
        this.adjustmentType = adjustmentType;
    }
    
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public Date getTransactionDate() {
        return transactionDate;
    }
    
    public Integer getCurrency() {
        return currency;
    }
    
    public void setCurrency(Integer currency) {
        this.currency = currency;
    }
    
    public Double getTransactionAmount() {
        return transactionAmount;
    }
    
    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
    
    public Boolean getDisasterResponse() {
        return disasterResponse;
    }

    public void setDisasterResponse(Boolean disasterResponse) {
        this.disasterResponse = disasterResponse;
    }

    public Boolean getTransactionDateTimeStamp() {
        return isTransactionDateTimeStamp;
    }

    public void setTransactionDateTimeStamp(Boolean transactionDateTimeStamp) {
        isTransactionDateTimeStamp = transactionDateTimeStamp;
    }

    public JsonBean toJsonBean() {
        JsonBean fundingDetail = new JsonBean();
    
        fundingDetail.set("adjustment_type", adjustmentType);
        fundingDetail.set("currency", currency);
        fundingDetail.set("transaction_date", DateUtils.formatDate(isTransactionDateTimeStamp, transactionDate));
        fundingDetail.set("transaction_amount", transactionAmount); 
        if (disasterResponse != null) {
            fundingDetail.set("disaster_response", disasterResponse); 
        }    
        
        return fundingDetail;
    }
    
    @Override
    public boolean equals(Object obj) {
        
        if (obj instanceof FundingDetail) {
            FundingDetail fd = (FundingDetail) obj;
            
            return adjustmentType.equals(fd.getAdjustmentType())
                    && transactionDate.equals(fd.getTransactionDate())
                    && currency.equals(fd.getCurrency())
                    && transactionAmount.equals(fd.getTransactionAmount());
        }
        
        return false;
    }
}
