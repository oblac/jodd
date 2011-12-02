// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.csselly;

import jodd.lagarto.LagartoException;

/**
 * CSSelly exception.
 */
public class CSSellyException extends LagartoException {

	public CSSellyException(Throwable t) {
		super(t);
	}

	public CSSellyException() {
		super();
	}

	public CSSellyException(String message) {
		super(message);
	}

	public CSSellyException(String message, Throwable t) {
		super(message, t);
	}

	public CSSellyException(String message, int state, int line, int column) {
		super(message, state, line, column);
	}
}
