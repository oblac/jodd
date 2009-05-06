// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import jodd.exception.UncheckedException;

public class ProxettaException extends UncheckedException {

	public ProxettaException(Throwable throwable) {
		super(throwable);
	}

	public ProxettaException() {
	}

	public ProxettaException(String string) {
		super(string);
	}

	public ProxettaException(String string, Throwable throwable) {
		super(string, throwable);
	}
}
