package org.devgateway.importtool.exceptions;
public class CurrencyNotFoundException extends Exception {

	private static final long serialVersionUID = -286367426493748747L;

	public CurrencyNotFoundException() {
	  }

	  public CurrencyNotFoundException(String message) {
	    super(message);
	  }

	  public CurrencyNotFoundException(String message, Throwable cause) {
	    super(message, cause);
	  }

	  public CurrencyNotFoundException(Throwable cause) {
	    super(cause);
	  }
}
