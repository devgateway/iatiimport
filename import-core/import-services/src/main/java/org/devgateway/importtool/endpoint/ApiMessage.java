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

}
