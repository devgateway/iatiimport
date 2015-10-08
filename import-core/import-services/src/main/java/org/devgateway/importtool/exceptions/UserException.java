package org.devgateway.importtool.exceptions;

import org.devgateway.importtool.model.User;

public class UserException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UserException(User user) {
		this(user.getId());
	}

	public UserException(User user, Throwable cause) {
		this(user == null ? -1 : user.getId(), cause);
	}

	public UserException(long userId, Throwable cause) {
		super("Error for user # " + userId, cause);
	}


	public UserException(long userId) {
		super("Error for user # " + userId);
	}

}
