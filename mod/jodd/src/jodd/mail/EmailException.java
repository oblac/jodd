// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.exception.UncheckedException;

/**
 * Email exception.
 */
public class EmailException extends UncheckedException {

	public EmailException() {
	}

	public EmailException(String message) {
		super(message);
	}

	public EmailException(String message, Throwable t) {
		super(message, t);
	}

	public EmailException(Throwable t) {
		super(t);
	}
}
