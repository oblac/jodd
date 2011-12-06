// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.LagartoException;

/**
 * Lagarto DOM exception
 */
public class LagartoDOMException extends LagartoException {

	public LagartoDOMException(Throwable t) {
		super(t);
	}

	public LagartoDOMException() {
	}

	public LagartoDOMException(String message) {
		super(message);
	}

	public LagartoDOMException(String message, Throwable t) {
		super(message, t);
	}
}
