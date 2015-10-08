package org.devgateway.importtool.services.processor.helper;

import java.math.BigDecimal;
import java.util.Date;

public class FinancialTransaction {
	private String reference;
	private String type;
	private BigDecimal value;
	private Date date;
	private String receivingOrganization;

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getReceivingOrganization() {
		return receivingOrganization;
	}

	public void setReceivingOrganization(String receivingOrganization) {
		this.receivingOrganization = receivingOrganization;
	}
	
}
