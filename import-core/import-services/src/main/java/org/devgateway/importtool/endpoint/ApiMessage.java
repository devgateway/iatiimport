package org.devgateway.importtool.endpoint;

public class ApiMessage {
	private Integer code;
	private String description;

	public ApiMessage(Integer code, String description) {
		this.code = code;
		this.description = description;
	}

	public Integer getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString(){
		return String.format(EPConstants.MESSAGE_FORMAT, this.getDescription(), this.getDescription(), this.getCode());		
	}

}
