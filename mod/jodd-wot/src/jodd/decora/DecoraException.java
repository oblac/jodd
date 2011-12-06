// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.decora;

import jodd.exception.UncheckedException;

/**
 * Decora exception.
 */
public class DecoraException extends UncheckedException {

	public DecoraException(Throwable t) {
		super(t);
	}

	public DecoraException() {
		super();
	}

	public DecoraException(String message) {
		super(message);
	}

	public DecoraException(String message, Throwable t) {
		super(message, t);
	}
}
