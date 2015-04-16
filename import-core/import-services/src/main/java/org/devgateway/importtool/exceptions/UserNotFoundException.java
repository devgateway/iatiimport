package org.devgateway.importtool.exceptions;

public class UserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -5309544181132763898L;

	public UserNotFoundException(long userId) {
        super("user#" + userId + " was not found");
        this.userId = userId;
    }

    private long userId;

    public long getUserId() {
        return userId;
    }

}
