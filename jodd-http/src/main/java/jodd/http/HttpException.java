// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.exception.UncheckedException;

/**
 * HTTP exception.
 */
public class HttpException extends UncheckedException {

	public HttpException(Throwable t) {
		super(t);
	}

	public HttpException(String message) {
		super(message);
	}

	public HttpException(Object networkObject, String message) {
		super(networkObject.toString() + ": " + message);
	}

	public HttpException(String message, Throwable t) {
		super(message, t);
	}

	public HttpException(Object networkObject, String message, Throwable t) {
		super(networkObject.toString() + ": " + message, t);
	}
}
