package org.devgateway.importtool.rest;

import java.util.Date;

public class AuthenticationToken {
	private String authenticationToken;
	private Date creationDate;
	private String sourceProcessorName;
	private String destinationProcessorName;

	public AuthenticationToken(String authenticationToken, Date creationDate, String sourceProcessorName, String destinationProcessorName) {
		this.authenticationToken = authenticationToken;
		this.creationDate = creationDate;
		this.setSourceProcessorName(sourceProcessorName);
		this.setDestinationProcessorName(destinationProcessorName);
	}

	public String getAuthenticationToken() {
		return authenticationToken;
	}

	public void setAuthenticationToken(String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getSourceProcessorName() {
		return sourceProcessorName;
	}

	public void setSourceProcessorName(String sourceProcessorName) {
		this.sourceProcessorName = sourceProcessorName;
	}

	public String getDestinationProcessorName() {
		return destinationProcessorName;
	}

	public void setDestinationProcessorName(String destinationProcessorName) {
		this.destinationProcessorName = destinationProcessorName;
	}

}
