// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.pathref;

import jodd.proxetta.ProxettaException;

/**
 * Pathref exception.
 */
public class PathrefException extends ProxettaException {

	public PathrefException(String message) {
		super(message);
	}

	public PathrefException(Throwable throwable) {
		super(throwable);
	}

	public PathrefException(String string, Throwable throwable) {
		super(string, throwable);
	}

}