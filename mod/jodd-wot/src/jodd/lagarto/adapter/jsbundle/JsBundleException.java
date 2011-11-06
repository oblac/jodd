// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.adapter.jsbundle;

import jodd.exception.UncheckedException;

/**
 * JS bundle exception.
 */
public class JsBundleException extends UncheckedException {

	public JsBundleException(Throwable t) {
		super(t);
	}

	public JsBundleException() {
		super();
	}

	public JsBundleException(String message) {
		super(message);
	}

	public JsBundleException(String message, Throwable t) {
		super(message, t);
	}
}