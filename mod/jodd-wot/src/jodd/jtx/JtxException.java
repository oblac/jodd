// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx;

import jodd.exception.UncheckedException;

/**
 * JTX unchecked exception.
 */
public class JtxException extends UncheckedException {

	public JtxException(Throwable t) {
		super(t);
	}

	public JtxException() {
		super();
	}

	public JtxException(String message) {
		super(message);
	}

	public JtxException(String message, Throwable t) {
		super(message, t);
	}
}
