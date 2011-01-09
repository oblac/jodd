// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.exception.UncheckedException;

/**
 * Madvoc exception.
 */
public class MadvocException extends UncheckedException {

	public MadvocException(Throwable t) {
		super(t);
	}

	public MadvocException() {
		super();
	}

	public MadvocException(String message) {
		super(message);
	}

	public MadvocException(String message, Throwable t) {
		super(message, t);
	}

}
