package org.devgateway.importtool.security;

import java.util.Date;
import java.util.UUID;

public class ImportSessionToken {
	private String userName;
	private Date creationDate;
	private String sourceProcessorName;
	private String destinationProcessorName;
	private String ampJSessionId;
	private UUID importTokenSessionId;

	public ImportSessionToken(String ampJSessionId, String userName, Date creationDate, String sourceProcessorName, String destinationProcessorName) {
		this.ampJSessionId = ampJSessionId;
		this.importTokenSessionId = UUID.randomUUID();
		this.userName = userName;
		this.creationDate = creationDate;
		this.setSourceProcessorName(sourceProcessorName);
		this.setDestinationProcessorName(destinationProcessorName);
	}
	
	public String getAmpJSessionId() {
		return ampJSessionId;
	}
	
	public void setAmpJSessionId(String ampJSessionId) {
		this.ampJSessionId = ampJSessionId;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public UUID getImportTokenSessionId() {
		return importTokenSessionId;
	}

}
