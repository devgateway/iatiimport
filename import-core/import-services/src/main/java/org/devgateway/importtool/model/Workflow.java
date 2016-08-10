package org.devgateway.importtool.model;

import java.io.Serializable;



import org.devgateway.importtool.services.processor.helper.Processor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.ANY)
public class Workflow implements Serializable {
	private static final long serialVersionUID = 1L;	
	private Processor sourceProcessor;
	private String translationKey;	

	public String getTranslationKey() {
		return translationKey;
	}

	public void setTranslationKey(String translationKey) {
		this.translationKey = translationKey;
	}

	public Processor getSourceProcessor() {
		return sourceProcessor;
	}

	public void setSourceProcessor(Processor sourceProcessor) {
		this.sourceProcessor = sourceProcessor;
	}

	private Processor destinationProcessor;

	public Processor getDestinationProcessor() {
		return destinationProcessor;
	}

	public void setDestinationProcessor(Processor destinationProcessor) {
		this.destinationProcessor = destinationProcessor;
	}

	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private boolean enabled;
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
