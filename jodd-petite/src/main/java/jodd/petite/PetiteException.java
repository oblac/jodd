// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.exception.UncheckedException;

/**
 * Petite exception.
 */
public class PetiteException extends UncheckedException {

	public PetiteException(Throwable t) {
		super(t);
	}

	public PetiteException() {
		super();
	}

	public PetiteException(String message) {
		super(message);
	}

	public PetiteException(String message, Throwable t) {
		super(message, t);
	}

}
