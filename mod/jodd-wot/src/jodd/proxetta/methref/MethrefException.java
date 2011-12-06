// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.methref;

import jodd.proxetta.ProxettaException;

/**
 * Methref exception.
 */
public class MethrefException extends ProxettaException {

	public MethrefException(Throwable throwable) {
		super(throwable);
	}

	public MethrefException() {
	}

	public MethrefException(String string) {
		super(string);
	}

	public MethrefException(String string, Throwable throwable) {
		super(string, throwable);
	}
}
