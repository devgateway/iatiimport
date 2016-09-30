package org.devgateway.importtool.exceptions;

public class MissingPrerequisitesException extends Exception {
	private static final long serialVersionUID = -4869520975500747723L;
	public MissingPrerequisitesException() {
	  }
	  public MissingPrerequisitesException(String message) {
	    super(message);
	  }

	  public MissingPrerequisitesException(String message, Throwable cause) {
	    super(message, cause);
	  }

	  public MissingPrerequisitesException(Throwable cause) {
	    super(cause);
	  }
}
