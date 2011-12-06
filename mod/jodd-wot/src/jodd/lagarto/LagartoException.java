// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.exception.UncheckedException;

/**
 * Lagarto exception.
 */
public class LagartoException extends UncheckedException {

	public LagartoException(Throwable t) {
		super(t);
	}

	public LagartoException() {
		super();
	}

	public LagartoException(String message) {
		super(message);
	}

	public LagartoException(String message, int state, int line, int column) {
		this(message + " (state: " + state + (line != -1 ? " error at: " + line + ':' + column : "" + ')'));
	}

	public LagartoException(String message, Throwable t) {
		super(message, t);
	}
}
