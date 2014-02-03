// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.jspp;

import jodd.exception.UncheckedException;

/**
 * JSPP exception.
 */
public class JsppException extends UncheckedException {

	public JsppException(Throwable t) {
		super(t);
	}

	public JsppException(String message) {
		super(message);
	}

}