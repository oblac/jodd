// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.exception;

import jodd.exception.UncheckedException;

/**
 * Just a simple application level unchecked exception.
 */
public class AppException extends UncheckedException {

	public AppException(Throwable t) {
		super(t);
	}

	public AppException(String message) {
		super(message);
	}

	public AppException(String message, Throwable t) {
		super(message, t);
	}
}
