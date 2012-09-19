// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor;

import jodd.exception.UncheckedException;

/**
 * Vtor exception.
 */
public class VtorException extends UncheckedException {

	public VtorException(Throwable t) {
		super(t);
	}

	public VtorException() {
	}

	public VtorException(String message) {
		super(message);
	}

	public VtorException(String message, Throwable t) {
		super(message, t);
	}
}
