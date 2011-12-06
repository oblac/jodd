// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.paramo;

import jodd.exception.UncheckedException;

/**
 * Exception thrown on {@link Paramo} problems.
 */
public class ParamoException extends UncheckedException {

	public ParamoException(Throwable t) {
		super(t);
	}

	public ParamoException() {
	}

	public ParamoException(String message) {
		super(message);
	}

	public ParamoException(String message, Throwable t) {
		super(message, t);
	}
}
