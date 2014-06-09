// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.exception.UncheckedException;

/**
 * Lagarto exception.
 */
public class LagartoException extends UncheckedException {

	public LagartoException(Throwable t) {
		super("Parsing error.", t);
	}

	public LagartoException(String message) {
		super(message);
	}

}