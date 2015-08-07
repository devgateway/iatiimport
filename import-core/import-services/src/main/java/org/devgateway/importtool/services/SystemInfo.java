package org.devgateway.importtool.services;
import java.io.Serializable;
public class SystemInfo implements  Serializable {
	private static final long serialVersionUID = 1L;
    private String status = "OK";    
    public SystemInfo() {
	}

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
