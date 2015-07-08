package org.devgateway.importtool.services.processor.helper;

public class DocumentMapping {
	private InternalDocument sourceDocument;
	private InternalDocument destinationDocument;
	private OperationType operation = OperationType.NOOP;
	private Boolean selected = false;

	public InternalDocument getSourceDocument() {
		return sourceDocument;
	}


	public void setSourceDocument(InternalDocument sourceDocument) {
		this.sourceDocument = sourceDocument;
	}


	public InternalDocument getDestinationDocument() {
		return destinationDocument;
	}


	public void setDestinationDocument(InternalDocument destinationDocument) {
		this.destinationDocument = destinationDocument;
	}


	public OperationType getOperation() {
		return operation;
	}


	public void setOperation(OperationType operation) {
		this.operation = operation;
	}


	public Boolean getSelected() {
		return selected;
	}


	public void setSelected(Boolean selected) {
		this.selected = selected;
	}


}
